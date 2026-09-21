async function carregarProfessores() {
  const lista = document.getElementById("lista-professores");
  if (!lista) return;

  try {
    const response = await fetch("http://localhost:8080/prof");
    if (!response.ok) throw new Error(`Erro ao buscar professores (${response.status})`);

    const professores = await response.json();
    lista.replaceChildren();

    if (professores.length === 0) {
      lista.textContent = "Nenhum professor cadastrado.";
      return;
    }

    professores.forEach(professor => {
      const item = document.createElement("div");
      item.className = "border rounded p-3 mb-2 bg-light";
      item.textContent = `${professor.nome} - Carga horária máxima: ${professor.chm ?? "não informada"}`;
      lista.appendChild(item);
    });
  } catch (error) {
    console.error(error);
    lista.textContent = "Não foi possível carregar os professores cadastrados.";
  }
}

document.addEventListener("DOMContentLoaded", carregarProfessores);

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
    await carregarProfessores();
  } catch (error) {
    console.error(error);
    alert("Não foi possível salvar o professor no backend.");
  }
};
