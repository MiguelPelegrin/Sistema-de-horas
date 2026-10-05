// Atribui ao 'window' para funcionar com os onclick="" do HTML
window.adicionarTagDisciplina = function() {
  const input = document.getElementById('input-sigla-disciplina');
  const container = document.getElementById('container-tags-disciplinas');
  
  if (!input || !container) return;

  const texto = input.value.trim().toUpperCase();

  if (texto === '') {
    alert('Digite a sigla ou nome da disciplina!');
    return;
  }

  const emptyMsg = container.querySelector('.id-mensagem-vazia');
  if (emptyMsg) emptyMsg.remove();

  const tag = document.createElement('span');
  tag.className = 'badge bg-primary fs-6 fw-normal d-inline-flex align-items-center gap-2 p-2';

  tag.innerHTML = `
    ${texto}
    <button type="button" class="btn-close btn-close-white btn-sm" aria-label="Remover" onclick="removerTagDisciplina(this)"></button>
  `;

  container.appendChild(tag);
  input.value = '';
  input.focus();
};

window.removerTagDisciplina = function(botao) {
  const tag = botao.parentElement;
  const container = document.getElementById('container-tags-disciplinas');
  if (tag) tag.remove();

  if (container && container.children.length === 0) {
    container.innerHTML = '<span class="text-secondary small id-mensagem-vazia">Nenhuma disciplina adicionada ainda.</span>';
  }
};