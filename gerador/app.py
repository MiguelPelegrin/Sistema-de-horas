import re

from flask import Flask, jsonify, request, send_file

from backend import ErroBackend, buscar_dados_escola
from planilha import gerar_planilha, nome_do_arquivo
from regras import montar_grade


app = Flask(__name__)

ORIGENS_PERMITIDAS = re.compile(r"^http://(localhost|127\.0\.0\.1)(:\d+)?$")


@app.after_request
def liberar_cors(resposta):
    origem = request.headers.get("Origin", "")

    if ORIGENS_PERMITIDAS.match(origem):
        resposta.headers["Access-Control-Allow-Origin"] = origem
        resposta.headers["Access-Control-Allow-Headers"] = "Authorization, Content-Type"
        resposta.headers["Access-Control-Allow-Methods"] = "GET, POST, OPTIONS"
        resposta.headers["Access-Control-Expose-Headers"] = (
            "Content-Disposition, X-Grade-Completa, X-Aulas-Pendentes, X-Solucao-Sugerida"
        )

    return resposta


@app.route("/status")
def status():
    return jsonify({"status": "ok"})


@app.route("/planilha/gerar", methods=["POST", "OPTIONS"])
def gerar():
    if request.method == "OPTIONS":
        return "", 204

    cabecalho = request.headers.get("Authorization", "")

    if not cabecalho.startswith("Bearer "):
        return jsonify({"mensagem": "Entre no sistema para gerar a planilha."}), 401

    token = cabecalho.replace("Bearer ", "")

    try:
        dados = buscar_dados_escola(token)
    except ErroBackend as erro:
        return jsonify({"mensagem": erro.mensagem}), erro.status

    if not dados["turmas"]:
        return jsonify({
            "mensagem": "Cadastre pelo menos uma turma com disciplinas antes de gerar a planilha."
        }), 400

    resultado = montar_grade(
        dados["professores"],
        dados["turmas"],
        dados["divisoes"],
        dados["dias"]
    )

    escola = dados["escola"]

    arquivo = gerar_planilha(
        escola.get("nome") or "Grade Escolar",
        escola.get("coordenacao"),
        dados["turmas"],
        resultado,
        dados["cores"]
    )

    resposta = send_file(
        arquivo,
        as_attachment=True,
        download_name=nome_do_arquivo(escola.get("nome") or "Escola"),
        mimetype="application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
    )

    resposta.headers["X-Grade-Completa"] = "true" if resultado["completo"] else "false"
    resposta.headers["X-Aulas-Pendentes"] = str(resultado["agendador"].total_faltas())
    resposta.headers["X-Solucao-Sugerida"] = "true" if resultado["sugestao"] else "false"

    return resposta


if __name__ == "__main__":
    app.run(host="127.0.0.1", port=5000)
