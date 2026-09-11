// URL base da sua API Spring Boot
const API_BASE_URL = 'http://localhost:8080/api';

// Executa assim que o HTML terminar de carregar
document.addEventListener('DOMContentLoaded', () => {
    carregarEstatisticas();
    configurarBotaoSalvar();
});

// 1. FUNÇÃO GET: Busca os contadores das estatísticas do banco de dados
function carregarEstatisticas() {
    fetch(`${API_BASE_URL}/dashboard/estatisticas`)
        .then(response => {
            if (!response.ok) throw new Error('Erro ao buscar estatísticas');
            return response.json();
        })
        .then(dados => {
            // Atualiza os elementos HTML usando os IDs corretos do seu HTML
            document.getElementById('statProf').innerText = dados.totalProfessores || 0;
            document.getElementById('statDisc').innerText = dados.totalDisciplinas || 0;
            document.getElementById('statTurma').innerText = dados.totalTurmas || 0;
            document.getElementById('statHor').innerText = dados.totalAulas || 0;
        })
        .catch(error => console.error('Erro na requisição das estatísticas:', error));
}

// 2. FUNÇÃO POST: Envia os dados do formulário da escola para o Spring Boot
function configurarBotaoSalvar() {
    // Captura o botão "Salvar" que está dentro da seção "Dados da escola"
    const btnSalvar = document.querySelector('.card .btn-primary');
    
    if (btnSalvar) {
        btnSalvar.addEventListener('click', () => {
            // Monta o objeto com os valores atuais dos inputs HTML
            const dadosEscola = {
                escola: document.getElementById('school').value,
                semestre: document.getElementById('semester').value,
                turno: document.getElementById('shift').value,
                coordenacao: document.getElementById('coordination').value
            };

            // Faz o disparo POST convertendo o objeto em uma String JSON
            fetch(`${API_BASE_URL}/escola`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(dadosEscola)
            })
            .then(response => {
                if (response.ok) {
                    alert('Dados da escola salvos com sucesso!');
                } else {
                    alert('Erro ao salvar os dados.');
                }
            })
            .catch(error => {
                console.error('Erro ao conectar com o servidor:', error);
                alert('Não foi possível conectar ao servidor backend.');
            });
        });
    }
}