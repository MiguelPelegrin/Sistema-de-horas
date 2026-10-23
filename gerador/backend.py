import os

import requests

from planilha import cor_da_disciplina
from regras import SEMANA, TODOS_SLOTS


API_BACKEND = os.environ.get("CRONOS_BACKEND", "http://localhost:8080")

DIAS_BACKEND = {
    "SEG": "Segunda-feira",
    "TER": "Terça-feira",
    "QUA": "Quarta-feira",
    "QUI": "Quinta-feira",
    "SEX": "Sexta-feira"
}

DIAS_ESCOLA = {
    "Segunda": "Segunda-feira",
    "Terça": "Terça-feira",
    "Quarta": "Quarta-feira",
    "Quinta": "Quinta-feira",
    "Sexta": "Sexta-feira"
}

TURNOS_TURMA = {
    "MANHA": ["Manhã"],
    "TARDE": ["Tarde"],
    "NOITE": ["Noite"],
    "INTEGRAL": ["Manhã", "Tarde"]
}


class ErroBackend(Exception):

    def __init__(self, mensagem, status=502):
        super().__init__(mensagem)
        self.mensagem = mensagem
        self.status = status


def buscar(caminho, token):
    try:
        resposta = requests.get(
            f"{API_BACKEND}{caminho}",
            headers={"Authorization": f"Bearer {token}"},
            timeout=15
        )
    except requests.RequestException:
        raise ErroBackend("Não foi possível conectar ao backend Java (porta 8080).")

    if resposta.status_code == 401:
        raise ErroBackend("Sessão expirada. Entre novamente no sistema.", 401)

    if resposta.status_code == 403:
        raise ErroBackend("Este usuário não tem acesso aos dados de uma escola.", 403)

    if not resposta.ok:
        raise ErroBackend(f"O backend respondeu com erro {resposta.status_code} em {caminho}.")

    return resposta.json()


def formatar_horario(hora_inicio, hora_fim):
    inicio = hora_inicio[:5].replace(":", "h")
    fim = hora_fim[:5].replace(":", "h")
    return f"{inicio} - {fim}"


def buscar_dados_escola(token):
    escola = buscar("/escola", token)
    materias = buscar("/materia", token)
    lista_professores = buscar("/prof", token)
    materias_professores = buscar("/materia-prof", token)
    disponibilidades = buscar("/disp-prof", token)
    lista_turmas = buscar("/turma", token)
    materias_turmas = buscar("/turma-materia", token)
    lista_divisoes = buscar("/divisao", token)

    professores = {
        professor["id"]: {
            "id": professor["id"],
            "nome": professor["nome"],
            "disciplinas": {},
            "disponibilidade": {},
            "carga_maxima": professor.get("chm")
        }
        for professor in lista_professores
    }

    for item in materias_professores:
        professor = professores.get(item["prof"]["id"])

        if professor:
            professor["disciplinas"][str(item["materia"]["id"])] = item["materia"]["nome"]

    for item in disponibilidades:
        professor = professores.get(item["prof"]["id"])
        horario = item["horario"]
        dia = DIAS_BACKEND.get(horario.get("diaSemana"))

        if not professor or not dia or not horario.get("horaInicio") or not horario.get("horaFim"):
            continue

        slot = formatar_horario(horario["horaInicio"], horario["horaFim"])

        if slot in TODOS_SLOTS:
            professor["disponibilidade"].setdefault(dia, set()).add(slot)

    turmas = {
        turma["id"]: {
            "id": turma["id"],
            "nome": turma["nome"],
            "chave": str(turma["id"]),
            "turnos": TURNOS_TURMA.get(turma.get("turno"), []),
            "disciplinas": {}
        }
        for turma in lista_turmas
    }

    for item in materias_turmas:
        turma = turmas.get(item["turma"]["id"])

        if turma:
            turma["disciplinas"][str(item["materia"]["id"])] = {
                "nome": item["materia"]["nome"],
                "qtd": item["qtdAulas"]
            }

    divisoes = [
        {
            "turma": str(divisao["turma"]["id"]),
            "disciplina_a": str(divisao["materiaA"]["id"]),
            "disciplina_b": str(divisao["materiaB"]["id"])
        }
        for divisao in lista_divisoes
    ]

    dias = [
        DIAS_ESCOLA[dia]
        for dia in (escola.get("diasLetivos") or "").split(",")
        if dia in DIAS_ESCOLA
    ] or SEMANA

    cores = {
        str(materia["id"]): cor_da_disciplina(materia.get("cor"))
        for materia in materias
    }

    return {
        "escola": escola,
        "professores": list(professores.values()),
        "turmas": [turma for turma in turmas.values() if turma["disciplinas"] and turma["turnos"]],
        "divisoes": divisoes,
        "dias": dias,
        "cores": cores
    }
