    function adicionarDisciplina() {
        const nome = prompt("Digite o nome da disciplina:");

        // Se o usuário cancelar ou deixar vazio
        if (nome === null || nome.trim() === "") {
            return;
        }

        // Cria a div
        const disciplina = document.createElement("div");

        // Coloca o nome dentro da div
        disciplina.textContent = nome;

        // Adiciona uma classe para poder estilizar no CSS
        disciplina.classList.add("disciplina");

        // Coloca a div na lista
        document.getElementById("listaDisciplinas").appendChild(disciplina);
    }