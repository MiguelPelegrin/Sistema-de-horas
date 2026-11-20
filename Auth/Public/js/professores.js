let professoresCadastrados = [];

async function carregarProfessores() {
  const lista = document.getElementById("lista-professores");
  if (!lista) return;

  try {
    const response = await apiFetch("/prof");
    if (!response.ok) throw new Error(`Erro ${response.status}`);
    professoresCadastrados = await response.json();
    mostrarProfessores();
  } catch (error) {
    console.error(error);
    lista.className = "estado-vazio";
    lista.textContent = "Não foi possível carregar os professores cadastrados.";
  }
}

function mostrarProfessores() {
  const lista = document.getElementById("lista-professores");
  const busca = normalizarBusca(document.getElementById("pesquisa-professores")?.value);
  const professores = professoresCadastrados.filter((professor) => normalizarBusca(professor.nome).includes(busca));

  lista.replaceChildren();

  if (!professores.length) {
    lista.className = "estado-vazio";
    lista.textContent = professoresCadastrados.length
      ? "Nenhum professor encontrado para essa pesquisa."
      : "Nenhum professor cadastrado.";
    return;
  }

  lista.className = "";
  professores.forEach((professor) => {
    const item = document.createElement("div");
    item.className = "item-cadastro";
    const info = document.createElement("div");
    const nome = document.createElement("div");
    nome.className = "item-titulo d-flex align-items-center gap-2";

    if (professor.cor) {
      const cor = document.createElement("span");
      cor.className = "d-inline-block rounded";
      cor.style.width = "14px";
      cor.style.height = "14px";
      cor.style.background = professor.cor;
      cor.title = "Cor do professor na planilha";
      nome.appendChild(cor);
    }

    nome.append(professor.nome);
    const detalhes = document.createElement("div");
    detalhes.className = "item-detalhes";
    detalhes.textContent = `Carga horária máxima: ${professor.chm ?? "não informada"} aulas por semana`;
    info.append(nome, detalhes);
    const botao = document.createElement("button");
    botao.type = "button";
    botao.className = "btn btn-outline-danger btn-sm";
    botao.innerHTML = '<i class="bi bi-trash3 me-1"></i> Apagar';
    botao.addEventListener("click", () => apagarProfessor(professor.id, professor.nome));
    item.append(info, botao);
    lista.appendChild(item);
  });
}

async function apagarProfessor(id, nome) {
  const confirmado = await confirmarAcao(
    "Apagar professor",
    `Deseja apagar o professor "${nome}"? As disciplinas e a disponibilidade dele também serão apagadas.`
  );

  if (!confirmado) return;

  try {
    const response = await apiFetch(`/prof/${id}`, { method: "DELETE" });
    if (!response.ok) {
      mostrarAviso(await lerMensagemErro(response, "Não foi possível apagar o professor."), "erro");
      return;
    }
    mostrarAviso(`Professor ${nome} apagado.`);
    await carregarProfessores();
  } catch (error) {
    console.error(error);
    mostrarAviso("Não foi possível apagar o professor.", "erro");
  }
}

document.addEventListener("DOMContentLoaded", () => {
  carregarProfessores();
  document.getElementById("pesquisa-professores")?.addEventListener("input", mostrarProfessores);
});
