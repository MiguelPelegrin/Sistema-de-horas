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

    alert("Professor salvo com sucesso!");
    document.getElementById("input-nome-professor").value = "";
    document.getElementById("input-sobrenome-professor").value = "";
    document.getElementById("input-carga-horaria").value = "";
  } catch (error) {
    console.error(error);
    alert("Não foi possível salvar o professor no backend.");
  }
};

window.adicionarProfessor = function() {
  const nome = prompt("Digite o nome do(a) Professor(a):");

  if (nome === null || nome.trim() === "") {
    return;
  }

  const professor = document.createElement("div");
  professor.textContent = nome;
  professor.classList.add("professor");

  const lista = document.getElementById("listaProfessor");
  if (lista) lista.appendChild(professor);
};