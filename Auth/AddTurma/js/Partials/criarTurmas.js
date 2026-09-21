async function carregarTurmas() {
  const lista = document.getElementById("lista-turmas-cadastradas");
  if (!lista) return;

  try {
    const response = await fetch("http://localhost:8080/turma");
    if (!response.ok) throw new Error(`Erro ao buscar turmas (${response.status})`);

    const turmas = await response.json();
    lista.replaceChildren();

    if (turmas.length === 0) {
      lista.textContent = "Nenhuma turma cadastrada.";
      return;
    }

    turmas.forEach(turma => {
      const item = document.createElement("div");
      item.className = "border rounded p-3 mb-2 bg-light d-flex justify-content-between align-items-center gap-3";

      const texto = document.createElement("span");
      texto.textContent = `${turma.nome} - Turno: ${turma.turno || "não informado"}`;

      const botao = document.createElement("button");
      botao.type = "button";
      botao.className = "btn btn-outline-danger btn-sm";
      botao.textContent = "Apagar";
      botao.addEventListener("click", () => apagarTurma(turma.id, turma.nome));

      item.append(texto, botao);
      lista.appendChild(item);
    });
  } catch (error) {
    console.error(error);
    lista.textContent = "Não foi possível carregar as turmas cadastradas.";
  }
}

async function apagarTurma(id, nome) {
  if (!confirm(`Deseja apagar a turma "${nome}"?`)) return;

  try {
    const response = await fetch(`http://localhost:8080/turma/${id}`, { method: "DELETE" });
    if (!response.ok) throw new Error(`Erro ao apagar turma (${response.status})`);
    await carregarTurmas();
  } catch (error) {
    console.error(error);
    alert("Não foi possível apagar a turma.");
  }
}

document.addEventListener("DOMContentLoaded", carregarTurmas);

// Tornando a função visível para o onclick do HTML
window.adicionarTurma = async function() {
  const selectAno = document.getElementById("select-ano");
  const selectTurno = document.getElementById("select-turno");
  const inputCurso = document.getElementById("input-curso");

  const anoSelecionado = selectAno.value;
  const turnoSelecionado = selectTurno.value;
  const curso = inputCurso.value.trim();

  if (!anoSelecionado) {
    alert("Por favor, selecione se é 1º, 2º ou 3º ano!");
    return;
  }

  if (curso === "") {
    alert("Por favor, digite o nome do curso!");
    return;
  }

  if (!turnoSelecionado) {
    alert("Por favor, selecione o turno!");
    return;
  }

  const turnoApi = {
    Matutino: "MANHA",
    Noturno: "NOITE",
    Integral: "INTEGRAL"
  }[turnoSelecionado];

  try {
    const response = await fetch("http://localhost:8080/turma", {
      method: "POST",
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify({
        nome: `${anoSelecionado} - ${curso}`,
        turno: turnoApi
      })
    });

    if (!response.ok) {
      throw new Error(`Erro ao salvar turma (${response.status})`);
    }
  } catch (error) {
    console.error(error);
    alert("Não foi possível salvar a turma no backend.");
    return;
  }

  // A lista abaixo é a fonte única e vem do backend.
  selectAno.selectedIndex = 0;
  selectTurno.selectedIndex = 0;
  inputCurso.value = "";
  await carregarTurmas();
};