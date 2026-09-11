function marcarCampo(campo, valido) {
    if (!campo) return;
    campo.style.borderColor = valido ? 'green' : 'red';
    campo.style.backgroundColor = valido ? '#e6ffe6' : '#ffe6e6';
}

function mostrarAvisoPersonalizado(mensagem, titulo = 'Aviso') {
    const modal = document.getElementById('meuModal');
    const elTitulo = document.getElementById('modalTitulo');
    const elMensagem = document.getElementById('modalMensagem');

    if (elTitulo && elMensagem) {
        elTitulo.textContent = titulo;
        elMensagem.textContent = mensagem;
    }

    if (modal) {
        modal.style.display = 'flex'; 
    }
}

function validarUser() {
    const usuario = document.getElementById('user');
    const valido = !!usuario && usuario.value.trim().length >= 3; 

    marcarCampo(usuario, valido);

    if (!valido) {
        mostrarAvisoPersonalizado('O usuário deve ter no mínimo 3 caracteres.', 'Erro no Usuário');
    }

    return valido;
}

function validarSenha() {
    const password = document.getElementById('senha');
    const valido = !!password && password.value.length >= 6;
    
    marcarCampo(password, valido);

    if (!valido) {
        mostrarAvisoPersonalizado('A senha deve ter pelo menos 6 caracteres.', 'Erro na Senha');
    }

    return valido;
}

// Configuração dos eventos
const botao = document.getElementById('validacao');

if (botao) {
    botao.addEventListener('click', (e) => {
        e.preventDefault(); 
        
        const userValido = validarUser();
        const senhaValida = validarSenha();

        if (userValido && senhaValida) {
            window.location.href = "./Home.html";
        }
    });
}

// Evento para fechar o modal
const botaoFechar = document.getElementById('fecharModal');
if (botaoFechar) {
    botaoFechar.addEventListener('click', () => {
        const modal = document.getElementById('meuModal');
        if (modal) modal.style.display = 'none';
    });
}