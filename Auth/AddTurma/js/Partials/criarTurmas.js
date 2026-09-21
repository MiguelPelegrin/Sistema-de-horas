const coresAno = {
  "1° ANO MÉDIO": { bg: "#0d6efd", text: "#ffffff" },
  "2° ANO MÉDIO": { bg: "#198754", text: "#ffffff" },
  "3° ANO MÉDIO": { bg: "#dc3545", text: "#ffffff" }
};

// Tornando a função visível para o onclick do HTML
window.adicionarTurma = async function() {
  const selectAno = document.getElementById("select-ano");
  const selectTurno = document.getElementById("select-turno");
  const inputCurso = document.getElementById("input-curso");
  const containerLista = document.getElementById("container-lista-turmas");

  const anoSelecionado = selectAno.value;
  const turnoSelecionado = selectTurno.value;
  const curso = inputCurso.value.trim();

  if (!anoSelecionado) {
    alert("Por favor, selecione se é 1º, 2º ou 3º ano!");
    return;
  }

  if (curso === "") {
    alert("Por favor, digite o nome do curso!");
    return;
  }

  if (!turnoSelecionado) {
    alert("Por favor, selecione o turno!");
    return;
  }

  const turnoApi = {
    Matutino: "MANHA",
    Noturno: "NOITE",
    Integral: "INTEGRAL"
  }[turnoSelecionado];

  try {
    const response = await fetch("http://localhost:8080/turma", {
      method: "POST",
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify({
        nome: `${anoSelecionado} - ${curso}`,
        turno: turnoApi
      })
    });

    if (!response.ok) {
      throw new Error(`Erro ao salvar turma (${response.status})`);
    }
  } catch (error) {
    console.error(error);
    alert("Não foi possível salvar a turma no backend.");
    return;
  }

  const estiloCor = coresAno[anoSelecionado] || { bg: "#6c757d", text: "#ffffff" };

  const novoCard = document.createElement("div");
  novoCard.className = "cartao-turma-custom shadow-sm p-3 mb-2";

  novoCard.innerHTML = `
    <div class="d-flex justify-content-between align-items-center flex-wrap gap-2">
      <div class="linha-informacoes-turma d-flex align-items-center gap-3">
        <span class="badge p-2" style="background-color: ${estiloCor.bg}; color: ${estiloCor.text}; font-size: 0.9rem;">
          ${anoSelecionado}
        </span>
        <h5 class="mb-0 fw-bold">${curso}</h5>
        <span class="text-muted"><strong>Turno:</strong> ${turnoSelecionado || 'Não informado'}</span>
      </div>
      <button class="btn btn-outline-danger btn-sm" onclick="removerCardTurma(this, '${curso}')">Apagar</button>
    </div>
  `;

  containerLista.appendChild(novoCard);

  // Reseta os campos do formulário
  selectAno.selectedIndex = 0;
  selectTurno.selectedIndex = 0;
  inputCurso.value = "";
};

// Tornando a função de apagar visível para o onclick do card
window.removerCardTurma = function(botao, nomeCurso) {
  if (confirm(`Deseja realmente apagar a turma de "${nomeCurso}"?`)) {
    const card = botao.closest(".cartao-turma-custom");
    if (card) card.remove();
  }
};