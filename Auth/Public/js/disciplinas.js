async function carregarDisciplinas() {
  const lista = document.getElementById("lista-disciplinas");
  if (!lista) return;

  try {
    const response = await apiFetch("/materia");
    if (!response.ok) throw new Error(`Erro ${response.status}`);
    const disciplinas = await response.json();
    lista.replaceChildren();

    if (!disciplinas.length) {
      lista.className = "estado-vazio";
      lista.textContent = "Nenhuma disciplina cadastrada.";
      return;
    }

    lista.className = "";
    disciplinas.forEach((disciplina) => {
      const item = document.createElement("div");
      item.className = "item-cadastro";
      const nome = document.createElement("div");
      nome.className = "item-titulo";
      nome.textContent = disciplina.nome;
      const botao = document.createElement("button");
      botao.type = "button";
      botao.className = "btn btn-outline-danger btn-sm";
      botao.innerHTML = '<i class="bi bi-trash3 me-1"></i> Apagar';
      botao.addEventListener("click", () => apagarDisciplina(disciplina.id, disciplina.nome));
      item.append(nome, botao);
      lista.appendChild(item);
    });
  } catch (error) {
    console.error(error);
    lista.className = "estado-vazio";
    lista.textContent = "Não foi possível carregar as disciplinas cadastradas.";
  }
}

async function apagarDisciplina(id, nome) {
  if (!confirm(`Deseja apagar a disciplina "${nome}"?`)) return;
  try {
    const response = await apiFetch(`/materia/${id}`, { method: "DELETE" });
    if (!response.ok) {
      alert(await lerMensagemErro(response, "Não foi possível apagar a disciplina."));
      return;
    }
    await carregarDisciplinas();
  } catch (error) {
    console.error(error);
    alert("Não foi possível apagar a disciplina.");
  }
}

document.addEventListener("DOMContentLoaded", carregarDisciplinas);
