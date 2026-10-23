const DIAS_PADRAO = ["Segunda", "Terça", "Quarta", "Quinta", "Sexta"];

let escolaAtual = null;
let diasSelecionados = [...DIAS_PADRAO];

document.addEventListener('DOMContentLoaded', () => {
    carregarEstatisticas();
    carregarEscola();
    configurarBotaoSalvar();
    configurarDiasLetivos();
});

function carregarEstatisticas() {
    apiFetch('/dashboard/estatisticas')
        .then(response => {
            if (!response.ok) throw new Error('Erro ao buscar estatísticas');
            return response.json();
        })
        .then(dados => {
            document.getElementById('statProf').innerText = dados.totalProfessores || 0;
            document.getElementById('statDisc').innerText = dados.totalDisciplinas || 0;
            document.getElementById('statTurma').innerText = dados.totalTurmas || 0;
            document.getElementById('statHor').innerText = dados.totalAulas || 0;
        })
        .catch(error => console.error('Erro na requisição das estatísticas:', error));
}

function carregarEscola() {
    apiFetch('/escola')
        .then(response => {
            if (!response.ok) throw new Error('Erro ao buscar a escola');
            return response.json();
        })
        .then(escola => {
            escolaAtual = escola;
            document.getElementById('school').value = escola.nome || '';
            document.getElementById('semester').value = escola.semestre || '';
            document.getElementById('coordination').value = escola.coordenacao || '';

            if (escola.turno) {
                document.getElementById('shift').value = escola.turno;
            }

            if (escola.diasLetivos) {
                diasSelecionados = escola.diasLetivos.split(',').filter(Boolean);
            }

            atualizarDiasLetivos();
        })
        .catch(error => console.error('Erro ao carregar os dados da escola:', error));
}

function montarDadosEscola() {
    return {
        nome: document.getElementById('school').value.trim(),
        semestre: document.getElementById('semester').value.trim(),
        turno: document.getElementById('shift').value,
        coordenacao: document.getElementById('coordination').value.trim(),
        diasLetivos: diasSelecionados.join(',')
    };
}

function salvarEscola(mensagemSucesso) {
    const dadosEscola = montarDadosEscola();

    if (!dadosEscola.nome) {
        alert('Informe o nome da escola.');
        return;
    }

    apiFetch('/escola', {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(dadosEscola)
    })
        .then(async response => {
            if (response.ok) {
                if (mensagemSucesso) alert(mensagemSucesso);
            } else {
                alert(await lerMensagemErro(response, 'Erro ao salvar os dados.'));
            }
        })
        .catch(error => {
            console.error('Erro ao conectar com o servidor:', error);
            alert('Não foi possível conectar ao servidor backend.');
        });
}

function configurarBotaoSalvar() {
    const btnSalvar = document.querySelector('.card .btn-primary');

    if (btnSalvar) {
        btnSalvar.addEventListener('click', () => salvarEscola('Dados da escola salvos com sucesso!'));
    }
}

function atualizarDiasLetivos() {
    document.querySelectorAll(".day-pill").forEach(botao => {
        const ativo = diasSelecionados.includes(botao.dataset.day);
        botao.classList.toggle("active", ativo);
        botao.setAttribute("aria-pressed", String(ativo));
    });
}

function configurarDiasLetivos() {
    const botoes = document.querySelectorAll(".day-pill");
    if (!botoes.length) return;

    botoes.forEach(botao => {
        botao.addEventListener("click", () => {
            const dia = botao.dataset.day;
            if (diasSelecionados.includes(dia)) {
                if (diasSelecionados.length === 1) {
                    alert("Pelo menos um dia letivo precisa permanecer selecionado.");
                    return;
                }
                diasSelecionados = diasSelecionados.filter(item => item !== dia);
            } else {
                diasSelecionados.push(dia);
                diasSelecionados = DIAS_PADRAO.filter(item => diasSelecionados.includes(item));
            }
            atualizarDiasLetivos();

            if (escolaAtual) {
                salvarEscola();
            }
        });
    });

    atualizarDiasLetivos();
}
