function adicionarProfessor() {
    const nome = prompt("Digite o nome do(a) Professor(a):");

    if (nome === null || nome.trim() === "") {
        return;
    }

    const professor = document.createElement("div");

    professor.textContent = nome;
    professor.classList.add("professor");

    document.getElementById("listaProfessor").appendChild(professor);
}