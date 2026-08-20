import './Partials/checklist.js';
import { API_BASE_URL } from '../../Global/Js/config.js';

const botao = document.getElementById('validacao');
const mensagem = document.getElementById('mensagem');

const campoNome = document.getElementById('nome');
const campoEmail = document.getElementById('email');
const campoSenha = document.getElementById('senha');
const campoConfirmarSenha = document.getElementById('confirmarSenha');

function mostrarMensagem(texto, cor) {
    mensagem.textContent = texto;
    mensagem.style.color = cor;
}

botao.addEventListener('click', async (e) => {
    e.preventDefault();

    const nome = campoNome.value.trim();
    const email = campoEmail.value.trim();
    const senha = campoSenha.value;
    const confirmarSenha = campoConfirmarSenha.value;

    if (!nome || !email || !senha) {
        mostrarMensagem('Preencha todos os campos.', 'red');
        return;
    }

    if (senha.length < 6) {
        mostrarMensagem('A senha deve ter no mínimo 6 caracteres.', 'red');
        return;
    }

    if (senha !== confirmarSenha) {
        mostrarMensagem('As senhas não coincidem.', 'red');
        return;
    }

    botao.disabled = true;

    try {
        const resposta = await fetch(`${API_BASE_URL}/auth/cadastro`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ nome, email, senha }),
        });

        const dados = await resposta.json();

        if (!resposta.ok) {
            const primeiroErro = Object.values(dados)[0];
            mostrarMensagem(primeiroErro || 'Não foi possível cadastrar.', 'red');
            return;
        }

        mostrarMensagem('Cadastro realizado com sucesso! Redirecionando...', 'green');
        setTimeout(() => {
            window.location.href = './Login.html';
        }, 1500);
    } catch (erro) {
        mostrarMensagem('Não foi possível conectar ao servidor.', 'red');
    } finally {
        botao.disabled = false;
    }
});
