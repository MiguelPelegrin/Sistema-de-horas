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
      item.className = "border rounded p-3 mb-2 bg-light";
      item.textContent = `${turma.nome} - Turno: ${turma.turno || "não informado"}`;
      lista.appendChild(item);
    });
  } catch (error) {
    console.error(error);
    lista.textContent = "Não foi possível carregar as turmas cadastradas.";
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