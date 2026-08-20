import './Partials/checklist.js';
import { API_BASE_URL } from '../../Global/Js/config.js';

const botao = document.getElementById('validacao');
const mensagem = document.getElementById('mensagem');
const campoUser = document.getElementById('user');
const campoSenha = document.getElementById('senha');

function mostrarMensagem(texto, cor) {
    mensagem.textContent = texto;
    mensagem.style.color = cor;
}

botao.addEventListener('click', async (e) => {
    e.preventDefault();

    const email = campoUser.value.trim();
    const senha = campoSenha.value;

    if (!email || !senha) {
        mostrarMensagem('Preencha usuário e senha.', 'red');
        return;
    }

    botao.disabled = true;

    try {
        const resposta = await fetch(`${API_BASE_URL}/auth/login`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ email, senha }),
        });

        const dados = await resposta.json();

        if (!resposta.ok) {
            mostrarMensagem(dados.erro || 'Usuário ou senha inválidos.', 'red');
            return;
        }

        localStorage.setItem('token', dados.token);
        localStorage.setItem('usuario', JSON.stringify({ id: dados.id, nome: dados.nome, email: dados.email }));

        window.location.href = './Home.html';
    } catch (erro) {
        mostrarMensagem('Não foi possível conectar ao servidor.', 'red');
    } finally {
        botao.disabled = false;
    }
});
