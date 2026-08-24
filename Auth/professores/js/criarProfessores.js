    function adicionarProfessor() {
        const nome = prompt("Digite o nome do(a) Professor(a):");

        // Se o usuário cancelar ou deixar vazio
        if (nome === null || nome.trim() === "") {
            return;
        }

        // Cria a div
        const professor = document.createElement("div");

        // Coloca o nome dentro da div
        professor.textContent = nome;

        // Adiciona uma classe para poder estilizar no CSS
        professor.classList.add("professor");

        // Coloca a div na lista
        document.getElementById("listaProfessor").appendChild(professor);
    }