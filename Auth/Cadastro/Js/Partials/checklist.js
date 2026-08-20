function validarEmail() {
    const email = document.getElementById('email');

    if (!email.value.includes('@') || !email.value.includes('.com')) {
        email.style.borderColor = 'red';
        email.style.backgroundColor = '#ffe6e6';
        return;
    }

    email.style.borderColor = 'green';
    email.style.backgroundColor = '#e6ffe6';
}

function validarSenha() {
    const password = document.getElementById('senha');

    if (password.value.length < 6) {
        password.style.borderColor = 'red';
        password.style.backgroundColor = '#ffe6e6';
        return;
    }

    password.style.borderColor = 'green';
    password.style.backgroundColor = '#e6ffe6';
}

const botao = document.getElementById('validacao');

botao.addEventListener('click', (e) => {
    e.preventDefault(); // Evita recarregar a página
    validarEmail();
    validarSenha();
});
