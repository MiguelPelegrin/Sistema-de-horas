const API_BASE_URL = "http://localhost:8080";

async function carregarProfessores() {
  const lista = document.getElementById("lista-professores");
  if (!lista) return;

  try {
    const response = await fetch(`${API_BASE_URL}/prof`);
    if (!response.ok) throw new Error(`Erro ${response.status}`);
    const professores = await response.json();
    lista.replaceChildren();

    if (!professores.length) {
      lista.className = "estado-vazio";
      lista.textContent = "Nenhum professor cadastrado.";
      return;
    }

    lista.className = "";
    professores.forEach((professor) => {
      const item = document.createElement("div");
      item.className = "item-cadastro";
      const info = document.createElement("div");
      const nome = document.createElement("div");
      nome.className = "item-titulo";
      nome.textContent = professor.nome;
      const detalhes = document.createElement("div");
      detalhes.className = "item-detalhes";
      detalhes.textContent = `Carga horária máxima: ${professor.chm ?? "não informada"}`;
      info.append(nome, detalhes);
      const botao = document.createElement("button");
      botao.type = "button";
      botao.className = "btn btn-outline-danger btn-sm";
      botao.innerHTML = '<i class="bi bi-trash3 me-1"></i> Apagar';
      botao.addEventListener("click", () => apagarProfessor(professor.id, professor.nome));
      item.append(info, botao);
      lista.appendChild(item);
    });
  } catch (error) {
    console.error(error);
    lista.className = "estado-vazio";
    lista.textContent = "Não foi possível carregar os professores cadastrados.";
  }
}

async function apagarProfessor(id, nome) {
  if (!confirm(`Deseja apagar o professor "${nome}"?`)) return;
  try {
    const response = await fetch(`${API_BASE_URL}/prof/${id}`, { method: "DELETE" });
    if (!response.ok) throw new Error(`Erro ${response.status}`);
    await carregarProfessores();
  } catch (error) {
    console.error(error);
    alert("Não foi possível apagar o professor.");
  }
}

document.addEventListener("DOMContentLoaded", carregarProfessores);
