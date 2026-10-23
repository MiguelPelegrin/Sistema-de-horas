const API_BASE_URL = "http://localhost:8080";

const scriptAuth = document.currentScript;
const PAGINA_LOGIN = new URL("../../Login/html/login.html", scriptAuth.src).href;
const PAGINA_ADMIN = new URL("../../Admin/html/admin.html", scriptAuth.src).href;
const PAGINA_HOME = new URL("../../Public/Home.html", scriptAuth.src).href;

function obterToken() {
  return localStorage.getItem("cronos_token");
}

function obterUsuario() {
  try {
    return JSON.parse(localStorage.getItem("cronos_usuario")) || {};
  } catch (erro) {
    return {};
  }
}

function sair() {
  localStorage.removeItem("cronos_token");
  localStorage.removeItem("cronos_usuario");
  window.location.replace(PAGINA_LOGIN);
}

async function apiFetch(caminho, opcoes = {}) {
  const response = await fetch(`${API_BASE_URL}${caminho}`, {
    ...opcoes,
    headers: {
      ...(opcoes.headers || {}),
      Authorization: `Bearer ${obterToken()}`
    }
  });

  if (response.status === 401) {
    sair();
    return new Promise(() => {});
  }

  return response;
}

async function lerMensagemErro(response, mensagemPadrao) {
  try {
    const dados = await response.json();
    return dados.mensagem || mensagemPadrao;
  } catch (erro) {
    return mensagemPadrao;
  }
}

(function verificarAcesso() {
  if (!obterToken()) {
    window.location.replace(PAGINA_LOGIN);
    return;
  }

  const paginaAdmin = scriptAuth.dataset.pagina === "admin";
  const usuario = obterUsuario();

  if (usuario.admin && !paginaAdmin) {
    window.location.replace(PAGINA_ADMIN);
  } else if (!usuario.admin && paginaAdmin) {
    window.location.replace(PAGINA_HOME);
  }
})();
