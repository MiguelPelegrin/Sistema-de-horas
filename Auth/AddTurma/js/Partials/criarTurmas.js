let salvandoTurma = false;

window.adicionarTurma = async function() {
  if (salvandoTurma) return;

  const selectAno = document.getElementById("select-ano");
  const selectTurno = document.getElementById("select-turno");
  const inputCurso = document.getElementById("input-curso");

  const anoSelecionado = selectAno.value;
  const turnoSelecionado = selectTurno.value;
  const curso = inputCurso.value.trim();
  const disciplinas = window.obterDisciplinasTurma?.() || [];
  const divisoes = window.obterDivisoesTurma?.() || [];

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
  if (!disciplinas.length) {
    alert("Adicione pelo menos uma disciplina à turma.");
    return;
  }

  const botao = document.getElementById("btn-salvar-turma");
  salvandoTurma = true;
  if (botao) botao.disabled = true;

  const turnoApi = {
    Matutino: "MANHA",
    Vespertino: "TARDE",
    Noturno: "NOITE",
    Integral: "INTEGRAL"
  }[turnoSelecionado];

  try {
    const response = await apiFetch("/turma", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        nome: `${anoSelecionado} - ${curso}`,
        turno: turnoApi
      })
    });

    if (!response.ok) throw new Error(await lerMensagemErro(response, `Erro ${response.status}`));

    const turmaSalva = await response.json();

    for (const item of disciplinas) {
      const respostaDisciplina = await apiFetch("/turma-materia", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          turma: { id: turmaSalva.id },
          materia: { id: item.materiaId },
          qtdAulas: item.qtdAulas
        })
      });

      if (!respostaDisciplina.ok) {
        throw new Error(await lerMensagemErro(respostaDisciplina, "Turma salva, mas as disciplinas não foram salvas."));
      }
    }

    for (const item of divisoes) {
      const respostaDivisao = await apiFetch("/divisao", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          turma: { id: turmaSalva.id },
          materiaA: { id: item.materiaA },
          materiaB: { id: item.materiaB }
        })
      });

      if (!respostaDivisao.ok) {
        throw new Error(await lerMensagemErro(respostaDivisao, "Turma salva, mas as divisões não foram salvas."));
      }
    }

    alert("Turma cadastrada com sucesso!");
    selectAno.selectedIndex = 0;
    selectTurno.selectedIndex = 0;
    inputCurso.value = "";
    window.limparDisciplinasTurma?.();
  } catch (error) {
    console.error(error);
    alert(error.message || "Não foi possível salvar a turma no backend.");
  } finally {
    salvandoTurma = false;
    if (botao) botao.disabled = false;
  }
};
