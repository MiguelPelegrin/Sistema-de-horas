const API_BASE_URL = "http://localhost:8080";

document.addEventListener("DOMContentLoaded", () => {
  if (localStorage.getItem("cronos_token")) {
    window.location.replace("../../../index.html");
    return;
  }

  document.getElementById("form-login").addEventListener("submit", entrar);
});

async function entrar(evento) {
  evento.preventDefault();

  const email = document.getElementById("input-email").value.trim();
  const senha = document.getElementById("input-senha").value;
  const mensagem = document.getElementById("mensagem-login");
  const botao = document.getElementById("btn-entrar");

  mensagem.textContent = "";

  if (!email || !senha) {
    mensagem.textContent = "Preencha o e-mail e a senha.";
    return;
  }

  botao.disabled = true;

  try {
    const response = await fetch(`${API_BASE_URL}/auth/login`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ email, senha })
    });

    if (response.status === 401) {
      mensagem.textContent = "E-mail ou senha inválidos.";
      return;
    }

    if (!response.ok) throw new Error(`Erro ${response.status}`);

    const dados = await response.json();
    localStorage.setItem("cronos_token", dados.token);
    localStorage.setItem("cronos_usuario", JSON.stringify({
      nome: dados.nome,
      admin: dados.admin,
      escola: dados.escola
    }));

    window.location.replace("../../../index.html");
  } catch (error) {
    console.error(error);
    mensagem.textContent = "Não foi possível conectar ao servidor.";
  } finally {
    botao.disabled = false;
  }
}
