package com.cronos.cronosystem.service;

import com.cronos.cronosystem.exception.MuitasTentativasException;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TentativaLoginService {

    private static final int LIMITE_TENTATIVAS = 5;
    private static final Duration TEMPO_BLOQUEIO = Duration.ofMinutes(15);

    private final Map<String, Tentativa> tentativas = new ConcurrentHashMap<>();

    private record Tentativa(int erros, Instant ultimoErro) {
    }

    public String chave(String email, String ip) {
        return email.trim().toLowerCase() + "|" + ip;
    }

    public void validarBloqueio(String chave) {
        Tentativa tentativa = tentativas.get(chave);

        if (tentativa == null) {
            return;
        }

        Instant liberaEm = tentativa.ultimoErro().plus(TEMPO_BLOQUEIO);

        if (Instant.now().isAfter(liberaEm)) {
            tentativas.remove(chave);
            return;
        }

        if (tentativa.erros() >= LIMITE_TENTATIVAS) {
            long minutos = Math.max(1, Duration.between(Instant.now(), liberaEm).toMinutes() + 1);
            throw new MuitasTentativasException("Muitas tentativas de login erradas. Tente novamente em " + minutos + " minuto(s).");
        }
    }

    public void registrarErro(String chave) {
        tentativas.merge(chave, new Tentativa(1, Instant.now()),
                (atual, nova) -> new Tentativa(atual.erros() + 1, Instant.now()));
    }

    public void limpar(String chave) {
        tentativas.remove(chave);
    }
}
