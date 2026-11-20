const nomesTurno = {
  MANHA: "Manhã",
  TARDE: "Tarde",
  NOITE: "Noite",
  INTEGRAL: "Integral",
  MODULO: "Noturno em módulo"
};

let turmasCadastradas = [];

async function carregarTurmas() {
  const lista = document.getElementById("lista-turmas");
  if (!lista) return;

  try {
    const response = await apiFetch("/turma");
    if (!response.ok) throw new Error(`Erro ${response.status}`);
    turmasCadastradas = await response.json();
    mostrarTurmas();
  } catch (error) {
    console.error(error);
    lista.className = "estado-vazio";
    lista.textContent = "Não foi possível carregar as turmas cadastradas.";
  }
}

function mostrarTurmas() {
  const lista = document.getElementById("lista-turmas");
  const busca = normalizarBusca(document.getElementById("pesquisa-turmas")?.value);
  const turmas = turmasCadastradas.filter((turma) =>
    normalizarBusca(`${turma.nome} ${nomesTurno[turma.turno] || ""}`).includes(busca)
  );

  lista.replaceChildren();

  if (!turmas.length) {
    lista.className = "estado-vazio";
    lista.textContent = turmasCadastradas.length
      ? "Nenhuma turma encontrada para essa pesquisa."
      : "Nenhuma turma cadastrada.";
    return;
  }

  lista.className = "";
  turmas.forEach((turma) => {
    const item = document.createElement("div");
    item.className = "item-cadastro";
    const info = document.createElement("div");
    const nome = document.createElement("div");
    nome.className = "item-titulo";
    nome.textContent = turma.nome;
    const detalhes = document.createElement("div");
    detalhes.className = "item-detalhes";
    detalhes.textContent = `Turno: ${nomesTurno[turma.turno] || turma.turno || "não informado"}`;
    info.append(nome, detalhes);
    const botao = document.createElement("button");
    botao.type = "button";
    botao.className = "btn btn-outline-danger btn-sm";
    botao.innerHTML = '<i class="bi bi-trash3 me-1"></i> Apagar';
    botao.addEventListener("click", () => apagarTurma(turma.id, turma.nome));
    item.append(info, botao);
    lista.appendChild(item);
  });
}

async function apagarTurma(id, nome) {
  const confirmado = await confirmarAcao(
    "Apagar turma",
    `Deseja apagar a turma "${nome}"? As disciplinas e divisões dela também serão apagadas.`
  );

  if (!confirmado) return;

  try {
    const response = await apiFetch(`/turma/${id}`, { method: "DELETE" });
    if (!response.ok) {
      mostrarAviso(await lerMensagemErro(response, "Não foi possível apagar a turma."), "erro");
      return;
    }
    mostrarAviso(`Turma ${nome} apagada.`);
    await carregarTurmas();
  } catch (error) {
    console.error(error);
    mostrarAviso("Não foi possível apagar a turma.", "erro");
  }
}

document.addEventListener("DOMContentLoaded", () => {
  carregarTurmas();
  document.getElementById("pesquisa-turmas")?.addEventListener("input", mostrarTurmas);
});
