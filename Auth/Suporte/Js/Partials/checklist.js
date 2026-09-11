function marcarCampo(campo, valido) {
    if (!campo) return;
    campo.style.borderColor = valido ? 'green' : 'red';
    campo.style.backgroundColor = valido ? '#e6ffe6' : '#ffe6e6';
}

function validarUser() {
    const usuario = document.getElementById('user');
    const valido = !!usuario && /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(usuario.value.trim());
    marcarCampo(usuario, valido);
    return valido;
}

function validarSenha() {
    const password = document.getElementById('senha');
    const valido = !!password && password.value.length >= 6;
    marcarCampo(password, valido);
    return valido;
}

const botao = document.getElementById('validacao');

if (botao) {
    botao.addEventListener('click', (e) => {
        e.preventDefault();
        validarUser();
        validarSenha();
    });
}
