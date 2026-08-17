    function adicionarTurma() {
        const nome = prompt("Digite o nome da turma:");

        // Se o usuário cancelar ou deixar vazio
        if (nome === null || nome.trim() === "") {
            return;
        }

        // Cria a div
        const turma = document.createElement("div");

        // Coloca o nome dentro da div
        turma.textContent = nome;

        // Adiciona uma classe para poder estilizar no CSS
        turma.classList.add("turma");

        // Coloca a div na lista
        document.getElementById("listaTurma").appendChild(turma);
    }