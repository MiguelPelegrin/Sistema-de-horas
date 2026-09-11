document.addEventListener("DOMContentLoaded", () => {
    const card = document.querySelector(".card");
    
    let container = document.getElementById("lista-resultados");
    if (!container) {
      container = document.createElement("div");
      container.id = "lista-resultados";
      container.style.marginTop = "20px";
      card.appendChild(container);
    }
  
    document.querySelector(".btn-primary").addEventListener("click", (e) => {
      e.preventDefault();
  
      const disciplina = document.getElementById("school").value.trim();
      if (!disciplina) return alert("Preencha a disciplina!");
  

      const item = document.createElement("div");
      item.style.cssText = "display: flex; justify-content: space-between; align-items: center; gap: 10px; padding: 10px 0; border-bottom: 1px solid #eee;";
      
      item.innerHTML = `
        <span><strong>Disciplina:</strong> ${disciplina}</span>
        <span><strong>Prioridade:</strong> ${document.getElementById("temPrioridade").value}</span>
        <span><strong>Cor:</strong> ${document.getElementById("shift").value}</span>
        <span><strong>Local:</strong> ${document.getElementById("qualPrioridade").value}</span>
        <button class="btn btn-danger btn-sm">Excluir</button>
      `;

      item.querySelector(".btn-danger").addEventListener("click", () => item.remove());
  
      container.appendChild(item);
    });
  });