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

    if (response.headers.get("X-Historico-Salvo") === "true") {
      mostrarAviso("Planilha gerada e salva no histórico.");
    } else {
      mostrarAviso("Planilha gerada, mas não foi possível salvar no histórico.", "alerta");
    }

    await carregarHistorico();
  } catch (error) {
    console.error(error);
    mensagem.textContent = "Não foi possível conectar ao gerador de planilhas (Python, porta 5000).";
  } finally {
    botao.disabled = false;
    botao.innerHTML = textoOriginal;
  }
}

function formatarDataGeracao(data) {
  if (!data) return "";
  return new Date(data).toLocaleString("pt-BR", {
    day: "2-digit",
    month: "2-digit",
    year: "numeric",
    hour: "2-digit",
    minute: "2-digit"
  });
}

async function carregarHistorico() {
  const lista = document.getElementById("lista-historico");
  if (!lista) return;

  try {
    const response = await apiFetch("/planilha");
    if (!response.ok) throw new Error(`Erro ${response.status}`);
    const planilhas = await response.json();
    lista.replaceChildren();

    if (!planilhas.length) {
      lista.className = "estado-vazio";
      lista.textContent = "Nenhuma planilha gerada ainda.";
      return;
    }

    lista.className = "";
    planilhas.forEach((planilha) => {
      const item = document.createElement("div");
      item.className = "item-cadastro item-historico";

      const icone = document.createElement("i");
      icone.className = planilha.completa
        ? "bi bi-file-earmark-check-fill text-success"
        : "bi bi-file-earmark-excel-fill text-danger";

      const info = document.createElement("div");
      const titulo = document.createElement("div");
      titulo.className = "item-titulo";
      titulo.textContent = planilha.nomeArquivo;

      const status = document.createElement("span");
      status.className = `etiqueta-status ${planilha.completa ? "status-completa" : "status-pendente"}`;
      status.textContent = planilha.completa
        ? "Completa"
        : `${planilha.aulasPendentes} pendente${planilha.aulasPendentes === 1 ? "" : "s"}`;
      titulo.appendChild(status);

      const detalhes = document.createElement("div");
      detalhes.className = "item-detalhes";
      detalhes.textContent = `Gerada em ${formatarDataGeracao(planilha.dataGeracao)}${planilha.solucaoSugerida ? " · com solução sugerida" : ""}`;
      info.append(titulo, detalhes);

      const acoes = document.createElement("div");
      acoes.className = "acoes-historico";

      const baixar = document.createElement("button");
      baixar.type = "button";
      baixar.className = "btn btn-primary btn-sm";
      baixar.innerHTML = '<i class="bi bi-download me-1"></i> Baixar';
      baixar.addEventListener("click", () => baixarDoHistorico(planilha, baixar));

      const apagar = document.createElement("button");
      apagar.type = "button";
      apagar.className = "btn btn-outline-danger btn-sm";
      apagar.innerHTML = '<i class="bi bi-trash3 me-1"></i> Apagar';
      apagar.addEventListener("click", () => apagarDoHistorico(planilha));

      acoes.append(baixar, apagar);
      item.append(icone, info, acoes);
      lista.appendChild(item);
    });
  } catch (error) {
    console.error(error);
    lista.className = "estado-vazio";
    lista.textContent = "Não foi possível carregar o histórico de planilhas.";
  }
}

async function baixarDoHistorico(planilha, botao) {
  botao.disabled = true;

  try {
    const response = await apiFetch(`/planilha/${planilha.id}/arquivo`);

    if (!response.ok) {
      mostrarAviso(await lerMensagemErro(response, "Não foi possível baixar a planilha."), "erro");
      return;
    }

    baixarArquivo(await response.blob(), planilha.nomeArquivo);
  } catch (error) {
    console.error(error);
    mostrarAviso("Não foi possível baixar a planilha.", "erro");
  } finally {
    botao.disabled = false;
  }
}

async function apagarDoHistorico(planilha) {
  const confirmado = await confirmarAcao(
    "Apagar planilha do histórico",
    `Deseja apagar a planilha gerada em ${formatarDataGeracao(planilha.dataGeracao)}?`
  );

  if (!confirmado) return;

  try {
    const response = await apiFetch(`/planilha/${planilha.id}`, { method: "DELETE" });

    if (!response.ok) {
      mostrarAviso(await lerMensagemErro(response, "Não foi possível apagar a planilha."), "erro");
      return;
    }

    mostrarAviso("Planilha apagada do histórico.");
    await carregarHistorico();
  } catch (error) {
    console.error(error);
    mostrarAviso("Não foi possível apagar a planilha.", "erro");
  }
}

document.addEventListener("DOMContentLoaded", carregarHistorico);
