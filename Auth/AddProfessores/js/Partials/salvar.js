let salvandoProfessor = false;

window.salvarDadosProfessor = async function() {
  const nome = document.getElementById("input-nome-professor")?.value.trim();
  const sobrenome = document.getElementById("input-sobrenome-professor")?.value.trim();
  const cargaHoraria = document.getElementById("input-carga-horaria")?.value;
  const botao = document.getElementById("btn-salvar-professor");

  if (salvandoProfessor) return;

  if (!nome || !sobrenome) {
    alert("Preencha o nome e o sobrenome do professor.");
    return;
  }

  if (`${nome} ${sobrenome}`.length > 255) {
    alert("O nome completo do professor pode ter no máximo 255 caracteres.");
    return;
  }

  const carga = Number(cargaHoraria);

  if (cargaHoraria === "" || !Number.isInteger(carga) || carga < 0 || carga > 65) {
    alert("Informe uma carga horária máxima entre 0 e 65 aulas por semana.");
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
        chm: carga
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

    alert(disponibilidade.length > 0
      ? "Professor e disponibilidade salvos com sucesso!"
      : "Professor salvo com sucesso! Nenhum horário foi selecionado.");
    document.getElementById("input-nome-professor").value = "";
    document.getElementById("input-sobrenome-professor").value = "";
    document.getElementById("input-carga-horaria").value = "";
    document.getElementById("input-quantidade-materias").value = "";
    document.getElementById("input-sigla-disciplina").value = "";
    document.getElementById("container-tags-disciplinas").innerHTML = '<span class="text-secondary small id-mensagem-vazia">Nenhuma disciplina adicionada ainda.</span>';
    window.limparDisponibilidadeProfessor?.();
  } catch (error) {
    console.error(error);
    alert(error.message || "Não foi possível salvar o professor no backend.");
  } finally {
    salvandoProfessor = false;
    if (botao) botao.disabled = false;
  }
};
