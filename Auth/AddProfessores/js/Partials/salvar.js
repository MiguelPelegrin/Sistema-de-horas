window.salvarDadosProfessor = async function() {
  const nome = document.getElementById("input-nome-professor")?.value.trim();
  const sobrenome = document.getElementById("input-sobrenome-professor")?.value.trim();
  const cargaHoraria = document.getElementById("input-carga-horaria")?.value;

  if (!nome || !sobrenome) {
    alert("Preencha o nome e o sobrenome do professor.");
    return;
  }

  if (cargaHoraria === "" || Number(cargaHoraria) < 0) {
    alert("Informe uma carga horária máxima válida.");
    return;
  }

  try {
    const response = await fetch("http://localhost:8080/prof", {
      method: "POST",
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify({
        nome: `${nome} ${sobrenome}`,
        chm: Number(cargaHoraria)
      })
    });

    if (!response.ok) {
      throw new Error(`Erro ao salvar professor (${response.status})`);
    }

    const professorSalvo = await response.json();
    const disponibilidade = window.obterDisponibilidadeProfessor?.() || [];

    if (professorSalvo.id && disponibilidade.length > 0) {
      const respostaDisponibilidade = await fetch(`http://localhost:8080/disp-prof/professor/${professorSalvo.id}`, {
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
    alert("Não foi possível salvar o professor no backend.");
  }
};
