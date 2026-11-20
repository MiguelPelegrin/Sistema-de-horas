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
    mostrarAviso("Selecione o ano da turma.", "alerta");
    return;
  }
  if (!curso) {
    mostrarAviso("Digite o nome do curso.", "alerta");
    return;
  }
  if (!turnoSelecionado) {
    mostrarAviso("Selecione o turno da turma.", "alerta");
    return;
  }
  if (!disciplinas.length) {
    mostrarAviso("Adicione pelo menos uma disciplina à turma.", "alerta");
    return;
  }

  const botao = document.getElementById("btn-salvar-turma");
  salvandoTurma = true;
  if (botao) botao.disabled = true;

  const turnoApi = {
    Matutino: "MANHA",
    Vespertino: "TARDE",
    Noturno: "NOITE",
    Integral: "INTEGRAL",
    Modulo: "MODULO"
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

    mostrarAviso(`Turma ${turmaSalva.nome} cadastrada com sucesso!`);
    selectAno.selectedIndex = 0;
    selectTurno.selectedIndex = 0;
    selectTurno.dispatchEvent(new Event("change"));
    inputCurso.value = "";
    window.limparDisciplinasTurma?.();
  } catch (error) {
    console.error(error);
    mostrarAviso(error.message || "Não foi possível salvar a turma no backend.", "erro");
  } finally {
    salvandoTurma = false;
    if (botao) botao.disabled = false;
  }
};
