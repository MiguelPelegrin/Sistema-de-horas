let disciplinasCadastradas = [];
let disciplinasTurma = [];
let divisoesTurma = [];

async function carregarDisciplinasCadastradas() {
  const select = document.getElementById("select-disciplina-turma");
  if (!select) return;

  try {
    const response = await apiFetch("/materia");
    if (!response.ok) throw new Error(`Erro ${response.status}`);
    disciplinasCadastradas = await response.json();

    select.replaceChildren(criarOpcao("", disciplinasCadastradas.length
      ? "Selecione a disciplina..."
      : "Nenhuma disciplina cadastrada", true));

    disciplinasCadastradas.forEach((disciplina) => {
      select.appendChild(criarOpcao(disciplina.id, disciplina.nome));
    });
  } catch (error) {
    console.error(error);
    select.replaceChildren(criarOpcao("", "Não foi possível carregar as disciplinas", true));
  }
}

function criarOpcao(valor, texto, desabilitada = false) {
  const opcao = document.createElement("option");
  opcao.value = valor;
  opcao.textContent = texto;
  opcao.disabled = desabilitada;
  opcao.selected = desabilitada;
  return opcao;
}

function nomeDisciplina(id) {
  return disciplinasCadastradas.find((disciplina) => disciplina.id === id)?.nome || "";
}

window.adicionarDisciplinaTurma = function() {
  const select = document.getElementById("select-disciplina-turma");
  const inputQtd = document.getElementById("input-qtd-aulas");
  const materiaId = Number(select.value);
  const qtdAulas = Number(inputQtd.value);

  if (!materiaId) {
    alert("Selecione a disciplina.");
    return;
  }

  if (!Number.isInteger(qtdAulas) || qtdAulas < 1 || qtdAulas > 40) {
    alert("Informe quantas aulas por semana (número inteiro de 1 a 40).");
    return;
  }

  if (disciplinasTurma.some((item) => item.materiaId === materiaId)) {
    alert("Essa disciplina já foi adicionada à turma.");
    return;
  }

  disciplinasTurma.push({ materiaId, qtdAulas });
  select.selectedIndex = 0;
  inputQtd.value = "";
  atualizarListas();
};

window.adicionarDivisaoTurma = function() {
  const materiaA = Number(document.getElementById("select-divisao-a").value);
  const materiaB = Number(document.getElementById("select-divisao-b").value);

  if (!materiaA || !materiaB) {
    alert("Adicione as disciplinas da turma e escolha as duas disciplinas da divisão.");
    return;
  }

  if (materiaA === materiaB) {
    alert("Escolha duas disciplinas diferentes.");
    return;
  }

  const repetida = divisoesTurma.some((item) =>
    (item.materiaA === materiaA && item.materiaB === materiaB) ||
    (item.materiaA === materiaB && item.materiaB === materiaA)
  );

  if (repetida) {
    alert("Essa divisão já foi adicionada.");
    return;
  }

  divisoesTurma.push({ materiaA, materiaB });
  atualizarListas();
};

function removerDisciplinaTurma(materiaId) {
  disciplinasTurma = disciplinasTurma.filter((item) => item.materiaId !== materiaId);
  divisoesTurma = divisoesTurma.filter((item) => item.materiaA !== materiaId && item.materiaB !== materiaId);
  atualizarListas();
}

function removerDivisaoTurma(indice) {
  divisoesTurma.splice(indice, 1);
  atualizarListas();
}

function criarEtiqueta(texto, aoRemover, classe) {
  const etiqueta = document.createElement("span");
  etiqueta.className = `badge ${classe} fs-6 fw-normal d-inline-flex align-items-center gap-2 p-2`;
  etiqueta.append(texto);

  const botao = document.createElement("button");
  botao.type = "button";
  botao.className = "btn-close btn-close-white btn-sm";
  botao.setAttribute("aria-label", "Remover");
  botao.addEventListener("click", aoRemover);
  etiqueta.appendChild(botao);

  return etiqueta;
}

function atualizarListas() {
  const listaDisciplinas = document.getElementById("lista-disciplinas-turma");
  const listaDivisoes = document.getElementById("lista-divisoes-turma");

  listaDisciplinas.replaceChildren();
  if (!disciplinasTurma.length) {
    listaDisciplinas.innerHTML = '<span class="text-secondary small id-mensagem-vazia">Nenhuma disciplina adicionada ainda.</span>';
  }
  disciplinasTurma.forEach((item) => {
    listaDisciplinas.appendChild(criarEtiqueta(
      `${nomeDisciplina(item.materiaId)} · ${item.qtdAulas} aula${item.qtdAulas === 1 ? "" : "s"}/semana`,
      () => removerDisciplinaTurma(item.materiaId),
      "bg-primary"
    ));
  });

  listaDivisoes.replaceChildren();
  if (!divisoesTurma.length) {
    listaDivisoes.innerHTML = '<span class="text-secondary small id-mensagem-vazia">Nenhuma divisão adicionada.</span>';
  }
  divisoesTurma.forEach((item, indice) => {
    listaDivisoes.appendChild(criarEtiqueta(
      `A: ${nomeDisciplina(item.materiaA)} | B: ${nomeDisciplina(item.materiaB)}`,
      () => removerDivisaoTurma(indice),
      "bg-secondary"
    ));
  });

  ["select-divisao-a", "select-divisao-b"].forEach((id) => {
    const select = document.getElementById(id);
    select.replaceChildren(criarOpcao("", disciplinasTurma.length ? "Selecione..." : "Adicione disciplinas à turma", true));
    disciplinasTurma.forEach((item) => {
      select.appendChild(criarOpcao(item.materiaId, nomeDisciplina(item.materiaId)));
    });
  });
}

window.obterDisciplinasTurma = function() {
  return disciplinasTurma;
};

window.obterDivisoesTurma = function() {
  return divisoesTurma;
};

window.limparDisciplinasTurma = function() {
  disciplinasTurma = [];
  divisoesTurma = [];
  atualizarListas();
};

document.addEventListener("DOMContentLoaded", () => {
  carregarDisciplinasCadastradas();
  atualizarListas();
});
