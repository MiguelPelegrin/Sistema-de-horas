const LIMITE_CARGA_HORARIA = 44;

let salvandoProfessor = false;

window.salvarDadosProfessor = async function() {
  const nome = document.getElementById("input-nome-professor")?.value.trim();
  const sobrenome = document.getElementById("input-sobrenome-professor")?.value.trim();
  const cargaHoraria = document.getElementById("input-carga-horaria")?.value;
  const botao = document.getElementById("btn-salvar-professor");

  if (salvandoProfessor) return;

  if (!nome || !sobrenome) {
    mostrarAviso("Preencha o nome e o sobrenome do professor.", "alerta");
    return;
  }

  if (`${nome} ${sobrenome}`.length > 255) {
    mostrarAviso("O nome completo do professor pode ter no máximo 255 caracteres.", "alerta");
    return;
  }

  const carga = Number(cargaHoraria);

  if (cargaHoraria === "" || !Number.isInteger(carga) || carga < 0 || carga > LIMITE_CARGA_HORARIA) {
    mostrarAviso(`Informe uma carga horária máxima entre 0 e ${LIMITE_CARGA_HORARIA} aulas por semana (limite da jornada semanal da CLT).`, "alerta");
    return;
  }

  salvandoProfessor = true;
  if (botao) botao.disabled = true;

  try {
    const response = await apiFetch("/prof", {
      method: "POST",
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify({
        nome: `${nome} ${sobrenome}`,
        chm: carga,
        cor: window.obterCorProfessor?.() || null
      })
    });

    if (!response.ok) {
      throw new Error(await lerMensagemErro(response, `Erro ao salvar professor (${response.status})`));
    }

    const professorSalvo = await response.json();
    const disciplinas = window.obterDisciplinasProfessor?.() || [];
    const disponibilidade = window.obterDisponibilidadeProfessor?.() || [];

    for (const materiaId of disciplinas) {
      const respostaDisciplina = await apiFetch("/materia-prof", {
        method: "POST",
        headers: {
          "Content-Type": "application/json"
        },
        body: JSON.stringify({
          prof: { id: professorSalvo.id },
          materia: { id: materiaId }
        })
      });

      if (!respostaDisciplina.ok) {
        throw new Error(`Professor salvo, mas as disciplinas não foram vinculadas (${respostaDisciplina.status})`);
      }
    }

    if (professorSalvo.id && disponibilidade.length > 0) {
      const respostaDisponibilidade = await apiFetch(`/disp-prof/professor/${professorSalvo.id}`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json"
        },
        body: JSON.stringify(disponibilidade)
      });

      if (!respostaDisponibilidade.ok) {
        throw new Error(`Professor salvo, mas a disponibilidade não foi salva (${respostaDisponibilidade.status})`);
      }
    }

    if (disponibilidade.length > 0) {
      mostrarAviso(`Professor ${nome} ${sobrenome} e disponibilidade salvos com sucesso!`);
    } else {
      mostrarAviso(`Professor ${nome} ${sobrenome} salvo, mas nenhum horário foi marcado na disponibilidade.`, "alerta");
    }

    document.getElementById("input-nome-professor").value = "";
    document.getElementById("input-sobrenome-professor").value = "";
    document.getElementById("input-carga-horaria").value = "";
    document.getElementById("input-quantidade-materias").value = "";
    document.getElementById("input-sigla-disciplina").value = "";
    document.getElementById("container-tags-disciplinas").innerHTML = '<span class="text-secondary small id-mensagem-vazia">Nenhuma disciplina adicionada ainda.</span>';
    window.limparDisponibilidadeProfessor?.();
    window.limparCorProfessor?.();
  } catch (error) {
    console.error(error);
    mostrarAviso(error.message || "Não foi possível salvar o professor no backend.", "erro");
  } finally {
    salvandoProfessor = false;
    if (botao) botao.disabled = false;
  }
};
