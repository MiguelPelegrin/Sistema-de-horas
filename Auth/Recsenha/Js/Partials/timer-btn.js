const btnEmail = document.getElementById('btn-email');
      const btnTexto = document.getElementById('btn-texto');
      
      const TEMPO_ESPERA = 60; 

      btnEmail.addEventListener('click', function(e) {
        e.preventDefault(); // Evita o recarregamento da página

        //Desabilita o botão 
        btnEmail.disabled = true;
        btnEmail.style.cursor = 'not-allowed';
        btnEmail.style.opacity = '0.6'; 

        let tempoRestante = TEMPO_ESPERA;
        btnTexto.textContent = `Aguarde (${tempoRestante}s)`;

        //intervalo de 1 segundo
        const contador = setInterval(() => {
          tempoRestante--;
          btnTexto.textContent = `Aguarde (${tempoRestante}s)`;

          // reativação do botão
          if (tempoRestante <= 0) {
            clearInterval(contador);
            btnEmail.disabled = false;
            btnEmail.style.cursor = 'pointer';
            btnEmail.style.opacity = '1';
            btnTexto.textContent = 'Reenviar email';
          }
        }, 1000);
      });