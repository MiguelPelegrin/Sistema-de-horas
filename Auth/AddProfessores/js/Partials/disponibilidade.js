const DIAS = [
  { codigo: "SEG", nome: "Segunda" },
  { codigo: "TER", nome: "Terça" },
  { codigo: "QUA", nome: "Quarta" },
  { codigo: "QUI", nome: "Quinta" },
  { codigo: "SEX", nome: "Sexta" },
  { codigo: "SAB", nome: "Sábado" }
];

const HORARIOS = [
  { inicio: "07:00", fim: "07:50", turno: "Manhã" },
  { inicio: "07:50", fim: "08:40", turno: "Manhã" },
  { inicio: "08:40", fim: "09:30", turno: "Manhã" },
  { inicio: "10:05", fim: "10:55", turno: "Manhã" },
  { inicio: "10:55", fim: "11:45", turno: "Manhã" },
  { inicio: "11:45", fim: "12:35", turno: "Manhã" },
  { inicio: "13:30", fim: "14:20", turno: "Tarde" },
  { inicio: "14:20", fim: "15:10", turno: "Tarde" },
  { inicio: "19:00", fim: "19:50", turno: "Noite" },
  { inicio: "19:50", fim: "20:40", turno: "Noite" },
  { inicio: "20:40", fim: "21:30", turno: "Noite" },
  { inicio: "21:30", fim: "22:20", turno: "Noite" }
];

const LIMITE_AULAS_DIA = 8;
const selecionados = new Map();

function chaveHorario(dia, horario) {
  return `${dia}-${horario.inicio}-${horario.fim}`;
}

function montarGradeDisponibilidade() {
  const corpo = document.getElementById("corpo-tabela-disponibilidade");
  if (!corpo) return;

  corpo.replaceChildren();

  HORARIOS.forEach(horario => {
    const linha = document.createElement("tr");
    const tituloHorario = document.createElement("th");
    tituloHorario.scope = "row";
    tituloHorario.textContent = `${horario.inicio} - ${horario.fim}`;
    tituloHorario.title = horario.turno;
    linha.appendChild(tituloHorario);

    DIAS.forEach(dia => {
      const celula = document.createElement("td");
      const bloco = document.createElement("button");
      bloco.type = "button";
      bloco.className = "bloco-disponibilidade";
      bloco.dataset.dia = dia.codigo;
      bloco.dataset.diaNome = dia.nome;
      bloco.dataset.inicio = horario.inicio;
      bloco.dataset.fim = horario.fim;
      bloco.dataset.turno = horario.turno;
      bloco.setAttribute("aria-pressed", "false");
      bloco.setAttribute("aria-label", `${dia.nome}, ${horario.inicio} às ${horario.fim}`);
      bloco.textContent = "";
      bloco.addEventListener("click", () => alternarHorario(bloco));
      celula.appendChild(bloco);
      linha.appendChild(celula);
    });

    corpo.appendChild(linha);
  });

  atualizarResumo();
}

function alternarHorario(bloco) {
  const dia = bloco.dataset.dia;
  const horario = {
    inicio: bloco.dataset.inicio,
    fim: bloco.dataset.fim
  };
  const chave = chaveHorario(dia, horario);

  if (selecionados.has(chave)) {
    selecionados.delete(chave);
    bloco.classList.remove("selecionado");
    bloco.setAttribute("aria-pressed", "false");
    bloco.textContent = "";
    atualizarResumo();
    return;
  }

  const quantidadeNoDia = [...selecionados.values()].filter(item => item.dia === dia).length;
  if (quantidadeNoDia >= LIMITE_AULAS_DIA) {
    mostrarMensagem(`O limite de ${LIMITE_AULAS_DIA} aulas por dia foi atingido.`, true);
    return;
  }

  selecionados.set(chave, {
    dia,
    diaNome: bloco.dataset.diaNome,
    horaInicio: bloco.dataset.inicio,
    horaFim: bloco.dataset.fim,
    turno: bloco.dataset.turno,
    tempoAula: 50
  });

  bloco.classList.add("selecionado");
  bloco.setAttribute("aria-pressed", "true");
  bloco.textContent = "✓";
  atualizarResumo();
}

function atualizarResumo() {
  const contador = document.getElementById("contador-disponibilidade");
  const input = document.getElementById("input-disponibilidade-professor");
  const mensagem = document.getElementById("mensagem-disponibilidade");
  const lista = [...selecionados.values()];

  if (contador) {
    contador.textContent = `${lista.length} horário${lista.length === 1 ? "" : "s"} selecionado${lista.length === 1 ? "" : "s"}`;
  }

  if (input) {
    input.value = JSON.stringify(lista);
  }

  if (mensagem && mensagem.dataset.erro !== "true") {
    mensagem.textContent = lista.length
      ? `${lista.length} bloco${lista.length === 1 ? "" : "s"} marcado${lista.length === 1 ? "" : "s"} para a disponibilidade.`
      : "Nenhum horário selecionado.";
  }
}

function limparDisponibilidade() {
  selecionados.clear();
  document.querySelectorAll(".bloco-disponibilidade.selecionado").forEach(bloco => {
    bloco.classList.remove("selecionado");
    bloco.setAttribute("aria-pressed", "false");
    bloco.textContent = "";
  });
  const mensagem = document.getElementById("mensagem-disponibilidade");
  if (mensagem) mensagem.dataset.erro = "false";
  atualizarResumo();
}

function mostrarMensagem(texto, erro = false) {
  const mensagem = document.getElementById("mensagem-disponibilidade");
  if (!mensagem) return;
  mensagem.textContent = texto;
  mensagem.dataset.erro = erro ? "true" : "false";
  if (erro) {
    mensagem.style.color = "var(--cor-perigo, #b42318)";
    window.clearTimeout(mostrarMensagem.timer);
    mostrarMensagem.timer = window.setTimeout(() => {
      mensagem.dataset.erro = "false";
      mensagem.style.color = "";
      atualizarResumo();
    }, 2600);
  }
}

window.obterDisponibilidadeProfessor = function() {
  return [...selecionados.values()];
};

window.limparDisponibilidadeProfessor = limparDisponibilidade;

document.addEventListener("DOMContentLoaded", () => {
  montarGradeDisponibilidade();
  document.getElementById("btn-limpar-disponibilidade")?.addEventListener("click", limparDisponibilidade);
});
