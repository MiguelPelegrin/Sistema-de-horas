function marcarCampo(campo, valido) {
    if (!campo) return;
    campo.style.borderColor = valido ? 'green' : 'red';
    campo.style.backgroundColor = valido ? '#e6ffe6' : '#ffe6e6';
}

function validarUser() {
    const usuario = document.getElementById('user');
    const valido = !!usuario && usuario.value.trim().length >= 2;
    marcarCampo(usuario, valido);
    return valido;
}

function validarEmail() {
    const email = document.getElementById('email');
    const valido = !!email && /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email.value.trim());
    marcarCampo(email, valido);
    return valido;
}

function validarSenha() {
    const password = document.getElementById('senha');
    const valido = !!password && password.value.length >= 6;
    marcarCampo(password, valido);
    return valido;
}

function validarConfirmacaoSenha() {
    const password = document.getElementById('senha');
    const confirmacao = document.getElementById('confirmarSenha');
    const valido = !!password && !!confirmacao && password.value === confirmacao.value && confirmacao.value.length >= 6;
    marcarCampo(confirmacao, valido);
    return valido;
}

const botao = document.getElementById('validacao');

if (botao) {
    botao.addEventListener('click', (e) => {
        e.preventDefault();
        validarUser();
        validarEmail();
        validarSenha();
        validarConfirmacaoSenha();
    });
}
