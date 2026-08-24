function addMateria() {
    const select = document.getElementById('materia-input');
    const listaContainer = document.getElementById('listaDisciplinas');
  
    const valorSelecionado = select.value;
  
    if (valorSelecionado === '') {
      alert('Por favor, selecione uma disciplina da lista!');
      return;
    }
  

    const agora = new Date();
    const dataFormatada = agora.toLocaleString('pt-BR', {
      day: '2-digit',
      month: '2-digit',
      year: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });

    const novoCard = document.createElement('div');
    //para alinhar com o bootstrap
    novoCard.className = 'alert alert-secondary d-flex justify-content-between align-items-center mt-2';
    
    novoCard.innerHTML = `
      <div>
        <strong>${valorSelecionado}</strong>
        <small class="text-muted d-block">Adicionado em: ${dataFormatada}</small>
      </div>
      <button class="btn btn-danger btn-sm" onclick="this.parentElement.remove()">Excluir</button>
    `;
  
    listaContainer.appendChild(novoCard);
  
    select.selectedIndex = 0;
  }