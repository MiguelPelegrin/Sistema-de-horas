let disciplinasCadastradas = [];

async function carregarDisciplinas() {
  const lista = document.getElementById("lista-disciplinas");
  if (!lista) return;

  try {
    const response = await apiFetch("/materia");
    if (!response.ok) throw new Error(`Erro ${response.status}`);
    disciplinasCadastradas = await response.json();
    mostrarDisciplinas();
  } catch (error) {
    console.error(error);
    lista.className = "estado-vazio";
    lista.textContent = "Não foi possível carregar as disciplinas cadastradas.";
  }
}

function mostrarDisciplinas() {
  const lista = document.getElementById("lista-disciplinas");
  const busca = normalizarBusca(document.getElementById("pesquisa-disciplinas")?.value);
  const disciplinas = disciplinasCadastradas.filter((disciplina) =>
    normalizarBusca(`${disciplina.nome} ${disciplina.sigla || ""}`).includes(busca)
  );

  lista.replaceChildren();

  if (!disciplinas.length) {
    lista.className = "estado-vazio";
    lista.textContent = disciplinasCadastradas.length
      ? "Nenhuma disciplina encontrada para essa pesquisa."
      : "Nenhuma disciplina cadastrada.";
    return;
  }

  lista.className = "";
  disciplinas.forEach((disciplina) => {
    const item = document.createElement("div");
    item.className = "item-cadastro";
    const info = document.createElement("div");
    const nome = document.createElement("div");
    nome.className = "item-titulo";
    nome.textContent = disciplina.nome;
    const detalhes = document.createElement("div");
    detalhes.className = "item-detalhes";
    detalhes.textContent = `Sigla: ${disciplina.sigla || "não informada"}${disciplina.laboratorio ? ` · ${disciplina.laboratorio}` : ""}`;
    info.append(nome, detalhes);
    const botao = document.createElement("button");
    botao.type = "button";
    botao.className = "btn btn-outline-danger btn-sm";
    botao.innerHTML = '<i class="bi bi-trash3 me-1"></i> Apagar';
    botao.addEventListener("click", () => apagarDisciplina(disciplina.id, disciplina.nome));
    item.append(info, botao);
    lista.appendChild(item);
  });
}

async function apagarDisciplina(id, nome) {
  const confirmado = await confirmarAcao(
    "Apagar disciplina",
    `Deseja apagar a disciplina "${nome}"? Ela também sai das turmas e dos professores que a usam.`
  );

  if (!confirmado) return;

  try {
    const response = await apiFetch(`/materia/${id}`, { method: "DELETE" });
    if (!response.ok) {
      mostrarAviso(await lerMensagemErro(response, "Não foi possível apagar a disciplina."), "erro");
      return;
    }
    mostrarAviso(`Disciplina ${nome} apagada.`);
    await carregarDisciplinas();
  } catch (error) {
    console.error(error);
    mostrarAviso("Não foi possível apagar a disciplina.", "erro");
  }
}

document.addEventListener("DOMContentLoaded", () => {
  carregarDisciplinas();
  document.getElementById("pesquisa-disciplinas")?.addEventListener("input", mostrarDisciplinas);
});
