window.salvarDadosProfessor = function() {
  alert("Informações salvas!");
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