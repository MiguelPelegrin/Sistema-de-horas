let disciplinasCadastradas = [];

async function carregarDisciplinasCadastradas() {
  const lista = document.getElementById('lista-disciplinas-cadastradas');
  if (!lista) return;

  try {
    const response = await apiFetch('/materia');
    if (!response.ok) throw new Error(`Erro ${response.status}`);
    disciplinasCadastradas = await response.json();

    lista.replaceChildren();
    disciplinasCadastradas.forEach((disciplina) => {
      const opcao = document.createElement('option');
      opcao.value = disciplina.nome;
      lista.appendChild(opcao);
    });
  } catch (error) {
    console.error(error);
  }
}

// Atribui ao 'window' para funcionar com os onclick="" do HTML
window.adicionarTagDisciplina = function() {
  const input = document.getElementById('input-sigla-disciplina');
  const container = document.getElementById('container-tags-disciplinas');

  if (!input || !container) return;

  const texto = input.value.trim();

  if (texto === '') {
    alert('Digite o nome da disciplina!');
    return;
  }

  const disciplina = disciplinasCadastradas.find(
    (item) => item.nome.toLowerCase() === texto.toLowerCase()
  );

  if (!disciplina) {
    alert('Disciplina não encontrada. Cadastre a disciplina antes de vincular ao professor.');
    return;
  }

  if (container.querySelector(`[data-id="${disciplina.id}"]`)) {
    alert('Essa disciplina já foi adicionada.');
    return;
  }

  const emptyMsg = container.querySelector('.id-mensagem-vazia');
  if (emptyMsg) emptyMsg.remove();

  const tag = document.createElement('span');
  tag.className = 'badge bg-primary fs-6 fw-normal d-inline-flex align-items-center gap-2 p-2';
  tag.dataset.id = disciplina.id;
  tag.append(disciplina.nome);

  const botao = document.createElement('button');
  botao.type = 'button';
  botao.className = 'btn-close btn-close-white btn-sm';
  botao.setAttribute('aria-label', 'Remover');
  botao.addEventListener('click', () => removerTagDisciplina(botao));
  tag.appendChild(botao);

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

window.obterDisciplinasProfessor = function() {
  return [...document.querySelectorAll('#container-tags-disciplinas [data-id]')]
    .map((tag) => Number(tag.dataset.id));
};

document.addEventListener('DOMContentLoaded', carregarDisciplinasCadastradas);
