function addDiciplina() {
  const inputNome = document.getElementById('school');
  const selectTemPrioridade = document.getElementById('temPrioridade');
  const selectCor = document.getElementById('shift');
  const selectQualPrioridade = document.getElementById('qualPrioridade');
  const listaContainer = document.getElementById('lista-disciplinas');

  const disciplina = inputNome.value.trim();
  const temPrioridade = selectTemPrioridade.value;
  const cor = selectCor.value;
  const qualPrioridade = selectQualPrioridade.value;

  if (disciplina === '') {
    alert('Por favor, digite o nome da disciplina!');
    return;
  }

  const novoCard = document.createElement('div');
  novoCard.className = 'card card-disciplina-custom shadow-sm p-3 mb-2';
  
  // Estrutura interna configurada em linha (horizontal)
  novoCard.innerHTML = `
    <div class="d-flex justify-content-between align-items-center flex-wrap gap-2">
      <div class="info-linha">
        <h5 class="mb-0 text-primary fw-bold me-2">${disciplina}</h5>
        <span class="info-item"><strong>Prioridade:</strong> ${temPrioridade}</span>
        <span class="info-item"><strong>Cor:</strong> <span class="badge-azul">${cor}</span></span>
        <span class="info-item"><strong>Qual:</strong> ${qualPrioridade}</span>
      </div>
      <button class="btn btn-outline-danger btn-sm" onclick="removerCard(this, '${disciplina}')">Apagar</button>
    </div>
  `;

  listaContainer.appendChild(novoCard);
  inputNome.value = '';
}

function removerCard(botao, nomeDisciplina) {
  if (confirm(`Deseja realmente apagar a disciplina "${nomeDisciplina}"?`)) {
    const card = botao.closest('.card-disciplina-custom');
    card.remove();
  }
}