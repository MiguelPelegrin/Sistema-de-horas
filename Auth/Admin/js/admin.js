async function carregarEscolas() {
  const lista = document.getElementById("lista-escolas");
  if (!lista) return;

  try {
    const response = await apiFetch("/admin/escola");
    if (!response.ok) throw new Error(`Erro ${response.status}`);
    const escolas = await response.json();
    lista.replaceChildren();

    if (!escolas.length) {
      lista.className = "estado-vazio";
      lista.textContent = "Nenhuma escola cadastrada.";
      return;
    }

    lista.className = "";
    escolas.forEach((escola) => {
      const item = document.createElement("div");
      item.className = "item-cadastro";
      const info = document.createElement("div");
      const nome = document.createElement("div");
      nome.className = "item-titulo";
      nome.textContent = escola.nome;
      const detalhes = document.createElement("div");
      detalhes.className = "item-detalhes";
      detalhes.textContent = `Coordenação: ${escola.coordenacao || "não informada"} · Acesso: ${escola.responsavel} (${escola.email})`;
      info.append(nome, detalhes);
      item.appendChild(info);
      lista.appendChild(item);
    });
  } catch (error) {
    console.error(error);
    lista.className = "estado-vazio";
    lista.textContent = "Não foi possível carregar as escolas cadastradas.";
  }
}

let salvandoEscola = false;

async function cadastrarEscola(evento) {
  evento.preventDefault();

  if (salvandoEscola) return;

  const nomeEscola = document.getElementById("input-nome-escola").value.trim();
  const coordenacao = document.getElementById("input-coordenacao").value.trim();
  const nome = document.getElementById("input-nome-usuario").value.trim();
  const email = document.getElementById("input-email-usuario").value.trim();
  const senha = document.getElementById("input-senha-usuario").value;

  if (!nomeEscola || !nome || !email || !senha) {
    alert("Preencha o nome da escola, o responsável, o e-mail e a senha.");
    return;
  }

  if (senha.length < 6) {
    alert("A senha precisa ter pelo menos 6 caracteres.");
    return;
  }

  const botao = document.querySelector("#form-escola button[type=submit]");
  salvandoEscola = true;
  botao.disabled = true;

  try {
    const response = await apiFetch("/admin/escola", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ nomeEscola, coordenacao, nome, email, senha })
    });

    if (!response.ok) {
      alert(await lerMensagemErro(response, "Não foi possível cadastrar a escola."));
      return;
    }

    alert("Escola cadastrada com sucesso!");
    document.getElementById("form-escola").reset();
    await carregarEscolas();
  } catch (error) {
    console.error(error);
    alert("Não foi possível conectar ao servidor.");
  } finally {
    salvandoEscola = false;
    botao.disabled = false;
  }
}

document.addEventListener("DOMContentLoaded", () => {
  carregarEscolas();
  document.getElementById("form-escola").addEventListener("submit", cadastrarEscola);
});
