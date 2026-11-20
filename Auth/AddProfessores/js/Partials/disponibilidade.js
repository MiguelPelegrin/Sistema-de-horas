const DIAS_SEMANA = [
  { codigo: "SEG", nome: "Segunda" },
  { codigo: "TER", nome: "Terça" },
  { codigo: "QUA", nome: "Quarta" },
  { codigo: "QUI", nome: "Quinta" },
  { codigo: "SEX", nome: "Sexta" },
  { codigo: "SAB", nome: "Sábado" }
];

const HORARIOS = [
  { inicio: "07:20", fim: "08:10", turno: "Manhã" },
  { inicio: "08:10", fim: "09:00", turno: "Manhã" },
  { inicio: "09:00", fim: "09:50", turno: "Manhã" },
  { inicio: "10:05", fim: "10:55", turno: "Manhã" },
  { inicio: "10:55", fim: "11:45", turno: "Manhã" },
  { inicio: "11:45", fim: "12:35", turno: "Manhã" },
  { inicio: "13:30", fim: "14:20", turno: "Tarde" },
  { inicio: "14:20", fim: "15:10", turno: "Tarde" },
  { inicio: "18:10", fim: "19:00", turno: "Noite" },
  { inicio: "19:00", fim: "19:50", turno: "Noite" },
  { inicio: "19:50", fim: "20:40", turno: "Noite" },
  { inicio: "21:05", fim: "21:55", turno: "Noite" },
  { inicio: "21:55", fim: "22:45", turno: "Noite" }
];

const BLOCOS_MODULO = [
  { inicio: "19:00", fim: "20:50", turno: "Módulo", rotulo: "1º bloco" },
  { inicio: "21:05", fim: "22:50", turno: "Módulo", rotulo: "2º bloco" }
];

const TEMPO_AULA = 50;
const TEMPO_BLOCO = 110;

const selecionados = new Map();
let diasDaGrade = DIAS_SEMANA.slice(0, 5);
let marcandoArrastando = null;

function chaveHorario(dia, horario) {
  return `${dia}-${horario.inicio}-${horario.fim}`;
}

async function carregarDiasLetivos() {
  try {
    const response = await apiFetch("/escola");
    if (!response.ok) return;

    const escola = await response.json();
    const dias = (escola.diasLetivos || "").split(",");
    const diasEscola = DIAS_SEMANA.filter((dia) => dias.includes(dia.nome));

    if (diasEscola.length) diasDaGrade = diasEscola;
  } catch (error) {
    console.error(error);
  }
}

function criarBloco(dia, horario, tempoAula) {
  const bloco = document.createElement("button");
  bloco.type = "button";
  bloco.className = "bloco-disponibilidade";
  bloco.dataset.dia = dia.codigo;
  bloco.dataset.diaNome = dia.nome;
  bloco.dataset.inicio = horario.inicio;
  bloco.dataset.fim = horario.fim;
  bloco.dataset.turno = horario.turno;
  bloco.dataset.tempoAula = tempoAula;
  bloco.setAttribute("aria-pressed", "false");
  bloco.setAttribute("aria-label", `${dia.nome}, ${horario.inicio} às ${horario.fim}`);

  bloco.addEventListener("pointerdown", (evento) => {
    if (evento.button !== 0) return;
    evento.preventDefault();
    marcandoArrastando = !bloco.classList.contains("selecionado");
    marcarBloco(bloco, marcandoArrastando);
    atualizarResumo();
  });

  bloco.addEventListener("click", (evento) => {
    if (evento.detail !== 0) return;
    marcarBloco(bloco, !bloco.classList.contains("selecionado"));
    atualizarResumo();
  });

  return bloco;
}

function criarBotaoCabecalho(texto, titulo, aoClicar) {
  const botao = document.createElement("button");
  botao.type = "button";
  botao.className = "cabecalho-disponibilidade";
  botao.innerHTML = texto;
  botao.title = titulo;
  botao.addEventListener("click", aoClicar);
  return botao;
}

function montarTabela(tabela, horarios, tempoAula) {
  if (!tabela) return;

  const cabecalho = document.createElement("thead");
  const linhaCabecalho = document.createElement("tr");
  const colunaHorario = document.createElement("th");
  colunaHorario.scope = "col";
  colunaHorario.className = "coluna-horario";
  colunaHorario.textContent = "Horário";
  linhaCabecalho.appendChild(colunaHorario);

  diasDaGrade.forEach((dia) => {
    const coluna = document.createElement("th");
    coluna.scope = "col";
    coluna.appendChild(criarBotaoCabecalho(dia.nome, `Marcar ou desmarcar ${dia.nome} inteira`, () => {
      marcarVarios(tabela.querySelectorAll(`.bloco-disponibilidade[data-dia="${dia.codigo}"]`));
    }));
    linhaCabecalho.appendChild(coluna);
  });

  cabecalho.appendChild(linhaCabecalho);

  const corpo = document.createElement("tbody");

  horarios.forEach((horario) => {
    const linha = document.createElement("tr");
    const tituloHorario = document.createElement("th");
    tituloHorario.scope = "row";
    const texto = horario.rotulo
      ? `${horario.rotulo}<small>${horario.inicio} - ${horario.fim}</small>`
      : `${horario.inicio} - ${horario.fim}`;

    tituloHorario.appendChild(criarBotaoCabecalho(texto, `Marcar ou desmarcar ${horario.inicio} - ${horario.fim} em todos os dias`, () => {
      marcarVarios(tabela.querySelectorAll(`.bloco-disponibilidade[data-inicio="${horario.inicio}"][data-fim="${horario.fim}"]`));
    }));
    linha.appendChild(tituloHorario);

    diasDaGrade.forEach((dia) => {
      const celula = document.createElement("td");
      celula.appendChild(criarBloco(dia, horario, tempoAula));
      linha.appendChild(celula);
    });

    corpo.appendChild(linha);
  });

  tabela.replaceChildren(cabecalho, corpo);

  tabela.addEventListener("pointermove", (evento) => {
    if (marcandoArrastando === null) return;

    const bloco = document.elementFromPoint(evento.clientX, evento.clientY)?.closest(".bloco-disponibilidade");

    if (bloco && tabela.contains(bloco) && bloco.classList.contains("selecionado") !== marcandoArrastando) {
      marcarBloco(bloco, marcandoArrastando);
      atualizarResumo();
    }
  });
}

function montarGradesDisponibilidade() {
  montarTabela(document.getElementById("tabela-aulas"), HORARIOS, TEMPO_AULA);
  montarTabela(document.getElementById("tabela-modulo"), BLOCOS_MODULO, TEMPO_BLOCO);
  atualizarResumo();
}

function marcarBloco(bloco, marcar) {
  const chave = chaveHorario(bloco.dataset.dia, {
    inicio: bloco.dataset.inicio,
    fim: bloco.dataset.fim
  });

  if (marcar) {
    selecionados.set(chave, {
      dia: bloco.dataset.dia,
      diaNome: bloco.dataset.diaNome,
      horaInicio: bloco.dataset.inicio,
      horaFim: bloco.dataset.fim,
      turno: bloco.dataset.turno,
      tempoAula: Number(bloco.dataset.tempoAula)
    });
  } else {
    selecionados.delete(chave);
  }

  bloco.classList.toggle("selecionado", marcar);
  bloco.setAttribute("aria-pressed", String(marcar));
  bloco.textContent = marcar ? "✓" : "";
}

function marcarVarios(blocos) {
  const lista = [...blocos];
  const marcar = lista.some((bloco) => !bloco.classList.contains("selecionado"));
  lista.forEach((bloco) => marcarBloco(bloco, marcar));
  atualizarResumo();
}

function marcarAtalho(botao) {
  const tabela = document.getElementById(botao.dataset.grade === "modulo" ? "tabela-modulo" : "tabela-aulas");
  const seletor = botao.dataset.turno === "todos"
    ? ".bloco-disponibilidade"
    : `.bloco-disponibilidade[data-turno="${botao.dataset.turno}"]`;

  marcarVarios(tabela.querySelectorAll(seletor));
}

function atualizarResumo() {
  const contador = document.getElementById("contador-disponibilidade");
  const input = document.getElementById("input-disponibilidade-professor");
  const mensagem = document.getElementById("mensagem-disponibilidade");
  const lista = [...selecionados.values()];
  const blocos = lista.filter((item) => item.turno === "Módulo").length;
  const aulas = lista.length - blocos;

  if (contador) {
    contador.textContent = `${lista.length} horário${lista.length === 1 ? "" : "s"} selecionado${lista.length === 1 ? "" : "s"}`;
  }

  if (input) {
    input.value = JSON.stringify(lista);
  }

  if (mensagem) {
    mensagem.textContent = lista.length
      ? `${aulas} aula${aulas === 1 ? "" : "s"} e ${blocos} bloco${blocos === 1 ? "" : "s"} do módulo noturno marcados.`
      : "Nenhum horário selecionado.";
  }
}

function limparDisponibilidade() {
  document.querySelectorAll(".bloco-disponibilidade.selecionado").forEach((bloco) => marcarBloco(bloco, false));
  selecionados.clear();
  atualizarResumo();
}

window.obterDisponibilidadeProfessor = function() {
  return [...selecionados.values()];
};

window.limparDisponibilidadeProfessor = limparDisponibilidade;

document.addEventListener("pointerup", () => {
  marcandoArrastando = null;
});

document.addEventListener("pointercancel", () => {
  marcandoArrastando = null;
});

document.addEventListener("DOMContentLoaded", async () => {
  await carregarDiasLetivos();
  montarGradesDisponibilidade();
  document.getElementById("btn-limpar-disponibilidade")?.addEventListener("click", limparDisponibilidade);
  document.querySelectorAll(".disponibilidade-atalhos [data-turno]").forEach((botao) => {
    botao.addEventListener("click", () => marcarAtalho(botao));
  });
});
