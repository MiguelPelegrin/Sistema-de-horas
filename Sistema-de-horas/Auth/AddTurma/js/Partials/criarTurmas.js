window.adicionarTurma = async function() {
  const selectAno = document.getElementById("select-ano");
  const selectTurno = document.getElementById("select-turno");
  const inputCurso = document.getElementById("input-curso");

  const anoSelecionado = selectAno.value;
  const turnoSelecionado = selectTurno.value;
  const curso = inputCurso.value.trim();

  if (!anoSelecionado) {
    alert("Por favor, selecione o ano.");
    return;
  }
  if (!curso) {
    alert("Por favor, digite o nome do curso.");
    return;
  }
  if (!turnoSelecionado) {
    alert("Por favor, selecione o turno.");
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
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        nome: `${anoSelecionado} - ${curso}`,
        turno: turnoApi
      })
    });

    if (!response.ok) throw new Error(`Erro ${response.status}`);

    alert("Turma cadastrada com sucesso!");
    selectAno.selectedIndex = 0;
    selectTurno.selectedIndex = 0;
    inputCurso.value = "";
  } catch (error) {
    console.error(error);
    alert("Não foi possível salvar a turma no backend.");
  }
};
