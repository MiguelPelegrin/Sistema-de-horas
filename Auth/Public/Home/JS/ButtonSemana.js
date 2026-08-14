// 1. Seleciona TODOS os elementos que possuem a classe .ButtonSemana
const botoes = document.querySelectorAll('.ButtonSemana');

// 2. Passa por cada botão da lista aplicando a lógica
botoes.forEach(function(botao) {
  
  botao.addEventListener('click', function() {
    // Alterna a classe 'ativo' no botão específico que foi clicado
    botao.classList.toggle('ativo');
  });

});