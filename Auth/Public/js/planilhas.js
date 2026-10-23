const API_GERADOR_URL = "http://localhost:5000";

function nomeDoArquivo(response) {
  const cabecalho = response.headers.get("Content-Disposition") || "";
  const comAcento = cabecalho.match(/filename\*=UTF-8''([^;]+)/i);

  if (comAcento) return decodeURIComponent(comAcento[1]);

  const simples = cabecalho.match(/filename="?([^";]+)"?/i);
  return simples ? simples[1] : "Grade_Escolar.xlsx";
}

function baixarArquivo(blob, nome) {
  const link = document.createElement("a");
  link.href = URL.createObjectURL(blob);
  link.download = nome;
  document.body.appendChild(link);
  link.click();
  link.remove();
  setTimeout(() => URL.revokeObjectURL(link.href), 1000);
}

function mostrarResultado(completa, pendentes, temSugestao, nomeArquivo) {
  const resultado = document.getElementById("resultado-planilha");
  const item = document.getElementById("resultado-item");
  const icone = document.getElementById("resultado-icone");
  const titulo = document.getElementById("resultado-titulo");
  const detalhes = document.getElementById("resultado-detalhes");

  resultado.classList.add("mostrar");
  item.classList.toggle("resultado-ok", completa);
  item.classList.toggle("resultado-pendente", !completa);
  icone.className = completa ? "bi bi-check-circle-fill" : "bi bi-exclamation-triangle-fill";

  if (completa) {
    titulo.textContent = "Grade completa gerada!";
    detalhes.textContent = `Todas as aulas foram encaixadas. Arquivo baixado: ${nomeArquivo}`;
    return;
  }

  titulo.textContent = `Grade gerada com ${pendentes} aula${pendentes === 1 ? "" : "s"} pendente${pendentes === 1 ? "" : "s"}`;
  detalhes.textContent = temSugestao
    ? `Veja as abas "Erros", "Solução sugerida" e "Ajustes da solução" no arquivo ${nomeArquivo}.`
    : `Veja a aba "Erros" no arquivo ${nomeArquivo} para saber o que corrigir nos cadastros.`;
}

async function gerarPlanilha() {
  const botao = document.getElementById("btn-gerar-planilha");
  const mensagem = document.getElementById("mensagem-planilha");
  const textoOriginal = botao.innerHTML;

  mensagem.textContent = "";
  botao.disabled = true;
  botao.innerHTML = '<span class="spinner-border spinner-border-sm me-1"></span> Montando a grade...';

  try {
    const response = await fetch(`${API_GERADOR_URL}/planilha/gerar`, {
      method: "POST",
      headers: { Authorization: `Bearer ${obterToken()}` }
    });

    if (response.status === 401) {
      sair();
      return;
    }

    if (!response.ok) {
      mensagem.textContent = await lerMensagemErro(response, "Não foi possível gerar a planilha.");
      return;
    }

    const nomeArquivo = nomeDoArquivo(response);
    baixarArquivo(await response.blob(), nomeArquivo);

    mostrarResultado(
      response.headers.get("X-Grade-Completa") === "true",
      Number(response.headers.get("X-Aulas-Pendentes") || 0),
      response.headers.get("X-Solucao-Sugerida") === "true",
      nomeArquivo
    );
  } catch (error) {
    console.error(error);
    mensagem.textContent = "Não foi possível conectar ao gerador de planilhas (Python, porta 5000).";
  } finally {
    botao.disabled = false;
    botao.innerHTML = textoOriginal;
  }
}
