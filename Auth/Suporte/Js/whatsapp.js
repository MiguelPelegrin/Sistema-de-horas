const WHATSAPP_SUPORTE = "5514998667190";

function montarMensagemSuporte() {
  const usuario = obterUsuario();
  const assunto = document.getElementById("select-assunto").value;
  const mensagem = document.getElementById("input-mensagem-suporte").value.trim();

  const linhas = ["Olá! Preciso de ajuda com o Cronos System."];

  if (usuario.escola) linhas.push(`Escola: ${usuario.escola}`);
  if (usuario.nome) linhas.push(`Usuário: ${usuario.nome}`);

  linhas.push(`Assunto: ${assunto}`);

  if (mensagem) linhas.push("", mensagem);

  return linhas.join("\n");
}

function abrirWhatsapp(evento) {
  evento.preventDefault();

  const link = `https://wa.me/${WHATSAPP_SUPORTE}?text=${encodeURIComponent(montarMensagemSuporte())}`;
  window.open(link, "_blank", "noopener");
}

document.addEventListener("DOMContentLoaded", () => {
  document.getElementById("form-whatsapp").addEventListener("submit", abrirWhatsapp);
});
