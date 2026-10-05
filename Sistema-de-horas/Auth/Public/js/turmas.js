const API_BASE_URL = "http://localhost:8080";

const nomesTurno = {
  MANHA: "Manhã",
  NOITE: "Noite",
  INTEGRAL: "Integral"
};

async function carregarTurmas() {
  const lista = document.getElementById("lista-turmas");
  if (!lista) return;

  try {
    const response = await fetch(`${API_BASE_URL}/turma`);
    if (!response.ok) throw new Error(`Erro ${response.status}`);
    const turmas = await response.json();
    lista.replaceChildren();

    if (!turmas.length) {
      lista.className = "estado-vazio";
      lista.textContent = "Nenhuma turma cadastrada.";
      return;
    }

    lista.className = "";
    turmas.forEach((turma) => {
      const item = document.createElement("div");
      item.className = "item-cadastro";
      const info = document.createElement("div");
      const nome = document.createElement("div");
      nome.className = "item-titulo";
      nome.textContent = turma.nome;
      const detalhes = document.createElement("div");
      detalhes.className = "item-detalhes";
      detalhes.textContent = `Turno: ${nomesTurno[turma.turno] || turma.turno || "não informado"}`;
      info.append(nome, detalhes);
      const botao = document.createElement("button");
      botao.type = "button";
      botao.className = "btn btn-outline-danger btn-sm";
      botao.innerHTML = '<i class="bi bi-trash3 me-1"></i> Apagar';
      botao.addEventListener("click", () => apagarTurma(turma.id, turma.nome));
      item.append(info, botao);
      lista.appendChild(item);
    });
  } catch (error) {
    console.error(error);
    lista.className = "estado-vazio";
    lista.textContent = "Não foi possível carregar as turmas cadastradas.";
  }
}

async function apagarTurma(id, nome) {
  if (!confirm(`Deseja apagar a turma "${nome}"?`)) return;
  try {
    const response = await fetch(`${API_BASE_URL}/turma/${id}`, { method: "DELETE" });
    if (!response.ok) throw new Error(`Erro ${response.status}`);
    await carregarTurmas();
  } catch (error) {
    console.error(error);
    alert("Não foi possível apagar a turma.");
  }
}

document.addEventListener("DOMContentLoaded", carregarTurmas);
