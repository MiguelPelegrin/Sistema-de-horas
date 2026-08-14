function validarUser() {
    const usuario = document.getElementById('user');

    if (!usuario.value.includes('@') || !usuario.value.includes('.com')) {
        usuario.style.borderColor = 'red';
        usuario.style.backgroundColor = '#ffe6e6';
        return;
    }

    usuario.style.borderColor = 'green';
    usuario.style.backgroundColor = '#e6ffe6';
}

function validarSenha() {
    const password = document.getElementById('senha');

    // Colocar para validar a senha do BD
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
    validarUser();
    validarSenha();
});