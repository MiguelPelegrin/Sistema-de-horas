const CORES_PROFESSOR = [
  { nome: "Azul petróleo", valor: "#1F5F7A" },
  { nome: "Azul anil", valor: "#2E3A8C" },
  { nome: "Ciano", valor: "#0B7285" },
  { nome: "Verde-água", valor: "#2E9C7A" },
  { nome: "Verde oliva", valor: "#4F6B2E" },
  { nome: "Mostarda", valor: "#C08A00" },
  { nome: "Laranja", valor: "#D35400" },
  { nome: "Vinho", valor: "#8E4B4B" },
  { nome: "Magenta", valor: "#C2185B" },
  { nome: "Roxo", valor: "#7B1FA2" },
  { nome: "Lilás", valor: "#9C6B98" },
  { nome: "Marrom", valor: "#8D6E63" },
  { nome: "Cinza azulado", valor: "#5D6D7E" },
  { nome: "Grafite", valor: "#37474F" }
];

let corSelecionada = null;

function criarOpcaoCor(cor) {
  const botao = document.createElement("button");
  botao.type = "button";
  botao.className = "opcao-cor";
  botao.title = cor ? cor.nome : "Automática";
  botao.setAttribute("aria-label", cor ? cor.nome : "Cor automática");
  botao.dataset.valor = cor ? cor.valor : "";

  if (cor) {
    botao.style.background = cor.valor;
  } else {
    botao.classList.add("opcao-cor-automatica");
    botao.textContent = "Auto";
  }

  botao.addEventListener("click", () => selecionarCor(cor ? cor.valor : null));
  return botao;
}

function selecionarCor(valor) {
  corSelecionada = valor;

  document.querySelectorAll("#container-cores-professor .opcao-cor").forEach((botao) => {
    const ativa = botao.dataset.valor === (valor || "");
    botao.classList.toggle("ativa", ativa);
    botao.setAttribute("aria-pressed", String(ativa));
  });
}

function montarCoresProfessor() {
  const container = document.getElementById("container-cores-professor");
  if (!container) return;

  container.replaceChildren(criarOpcaoCor(null), ...CORES_PROFESSOR.map(criarOpcaoCor));
  selecionarCor(null);
}

window.obterCorProfessor = function() {
  return corSelecionada;
};

window.limparCorProfessor = function() {
  selecionarCor(null);
};

document.addEventListener("DOMContentLoaded", montarCoresProfessor);
