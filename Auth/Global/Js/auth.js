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

const ICONES_AVISO = {
  sucesso: "bi-check-circle-fill",
  erro: "bi-x-circle-fill",
  alerta: "bi-exclamation-triangle-fill"
};

function obterAreaAvisos() {
  let area = document.getElementById("area-avisos");

  if (!area) {
    area = document.createElement("div");
    area.id = "area-avisos";
    area.className = "area-avisos";
    area.setAttribute("aria-live", "polite");
    document.body.appendChild(area);
  }

  return area;
}

function mostrarAviso(mensagem, tipo = "sucesso") {
  const aviso = document.createElement("div");
  aviso.className = `aviso aviso-${tipo}`;
  aviso.setAttribute("role", tipo === "erro" ? "alert" : "status");

  const icone = document.createElement("i");
  icone.className = `bi ${ICONES_AVISO[tipo] || ICONES_AVISO.sucesso}`;

  const texto = document.createElement("span");
  texto.textContent = mensagem;

  const fechar = document.createElement("button");
  fechar.type = "button";
  fechar.className = "aviso-fechar";
  fechar.setAttribute("aria-label", "Fechar aviso");
  fechar.textContent = "×";

  aviso.append(icone, texto, fechar);
  obterAreaAvisos().appendChild(aviso);
  requestAnimationFrame(() => aviso.classList.add("mostrar"));

  const remover = () => {
    aviso.classList.remove("mostrar");
    setTimeout(() => aviso.remove(), 250);
  };

  fechar.addEventListener("click", remover);
  setTimeout(remover, tipo === "erro" ? 6000 : 4000);
}

function confirmarAcao(titulo, mensagem, textoConfirmar = "Apagar") {
  return new Promise((resolve) => {
    const fundo = document.createElement("div");
    fundo.className = "confirmacao-fundo";

    const caixa = document.createElement("div");
    caixa.className = "confirmacao-caixa";
    caixa.setAttribute("role", "dialog");
    caixa.setAttribute("aria-modal", "true");

    const cabecalho = document.createElement("h2");
    cabecalho.textContent = titulo;

    const texto = document.createElement("p");
    texto.textContent = mensagem;

    const acoes = document.createElement("div");
    acoes.className = "confirmacao-acoes";

    const cancelar = document.createElement("button");
    cancelar.type = "button";
    cancelar.className = "confirmacao-cancelar";
    cancelar.textContent = "Cancelar";

    const confirmar = document.createElement("button");
    confirmar.type = "button";
    confirmar.className = "confirmacao-confirmar";
    confirmar.textContent = textoConfirmar;

    acoes.append(cancelar, confirmar);
    caixa.append(cabecalho, texto, acoes);
    fundo.appendChild(caixa);
    document.body.appendChild(fundo);
    cancelar.focus();

    const teclado = (evento) => {
      if (evento.key === "Escape") fechar(false);
    };

    const fechar = (resposta) => {
      document.removeEventListener("keydown", teclado);
      fundo.remove();
      resolve(resposta);
    };

    cancelar.addEventListener("click", () => fechar(false));
    confirmar.addEventListener("click", () => fechar(true));
    fundo.addEventListener("click", (evento) => {
      if (evento.target === fundo) fechar(false);
    });
    document.addEventListener("keydown", teclado);
  });
}

function normalizarBusca(texto) {
  return (texto || "")
    .normalize("NFD")
    .replace(/[̀-ͯ]/g, "")
    .toLowerCase()
    .trim();
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
