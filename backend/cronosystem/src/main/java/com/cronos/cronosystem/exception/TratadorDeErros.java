package com.cronos.cronosystem.exception;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.ConcurrencyFailureException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.format.DateTimeParseException;
import java.util.Map;

@RestControllerAdvice
public class TratadorDeErros {

    private static final Logger log = LoggerFactory.getLogger(TratadorDeErros.class);

    private static final Map<String, String> NOMES_CAMPOS = Map.ofEntries(
            Map.entry("nome", "Nome"),
            Map.entry("nomeEscola", "Nome da escola"),
            Map.entry("chm", "Carga horária máxima"),
            Map.entry("qtdAulas", "Aulas por semana"),
            Map.entry("turno", "Turno"),
            Map.entry("turma", "Turma"),
            Map.entry("materia", "Disciplina"),
            Map.entry("materiaA", "Disciplina da turma A"),
            Map.entry("materiaB", "Disciplina da turma B"),
            Map.entry("prof", "Professor"),
            Map.entry("materiaProf", "Disciplina do professor"),
            Map.entry("horario", "Horário"),
            Map.entry("diaSemana", "Dia da semana"),
            Map.entry("horaInicio", "Hora de início"),
            Map.entry("horaFim", "Hora de término"),
            Map.entry("email", "E-mail"),
            Map.entry("senha", "Senha"),
            Map.entry("senhaAtual", "Senha atual"),
            Map.entry("novaSenha", "Nova senha"),
            Map.entry("coordenacao", "Coordenação"),
            Map.entry("semestre", "Semestre"),
            Map.entry("diasLetivos", "Dias letivos"),
            Map.entry("cor", "Cor"),
            Map.entry("laboratorio", "Laboratório")
    );

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Map<String, String>> naoEncontrado(EntityNotFoundException erro) {
        return resposta(HttpStatus.NOT_FOUND, erro.getMessage());
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Map<String, String>> rotaInexistente(NoResourceFoundException erro) {
        return resposta(HttpStatus.NOT_FOUND, "Endereço não encontrado.");
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Map<String, String>> metodoNaoPermitido(HttpRequestMethodNotSupportedException erro) {
        return resposta(HttpStatus.METHOD_NOT_ALLOWED, "Método " + erro.getMethod() + " não permitido nesse endereço.");
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<Map<String, String>> formatoNaoSuportado(HttpMediaTypeNotSupportedException erro) {
        return resposta(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "Envie os dados em JSON.");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> camposInvalidos(MethodArgumentNotValidException erro) {
        var campo = erro.getFieldErrors().isEmpty() ? null : erro.getFieldErrors().get(0);
        String mensagem = campo == null ? "Dados inválidos." : nomeCampo(campo.getField()) + ": " + campo.getDefaultMessage();
        return resposta(HttpStatus.BAD_REQUEST, mensagem);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, String>> regraInvalida(ConstraintViolationException erro) {
        String mensagem = erro.getConstraintViolations().stream()
                .findFirst()
                .map(this::descreverViolacao)
                .orElse("Preencha todos os campos obrigatórios.");
        return resposta(HttpStatus.BAD_REQUEST, mensagem);
    }

    @ExceptionHandler(RegraNegocioException.class)
    public ResponseEntity<Map<String, String>> regraNegocio(RegraNegocioException erro) {
        return resposta(HttpStatus.BAD_REQUEST, erro.getMessage());
    }

    @ExceptionHandler({IllegalArgumentException.class, DateTimeParseException.class, HttpMessageNotReadableException.class,
            MethodArgumentTypeMismatchException.class})
    public ResponseEntity<Map<String, String>> requisicaoInvalida(Exception erro) {
        return resposta(HttpStatus.BAD_REQUEST, "Dados enviados em formato inválido.");
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, String>> conflito(DataIntegrityViolationException erro) {
        String mensagem = erro.getMessage() != null && !erro.getMessage().contains("could not execute statement")
                ? erro.getMessage()
                : "Esse registro está em uso ou já existe.";
        return resposta(HttpStatus.CONFLICT, mensagem);
    }

    @ExceptionHandler(ConcurrencyFailureException.class)
    public ResponseEntity<Map<String, String>> alteracaoSimultanea(ConcurrencyFailureException erro) {
        return resposta(HttpStatus.CONFLICT, "Outra alteração foi feita ao mesmo tempo. Tente novamente.");
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Map<String, String>> naoAutenticado(AuthenticationException erro) {
        return resposta(HttpStatus.UNAUTHORIZED, "E-mail ou senha inválidos.");
    }

    @ExceptionHandler({AccessDeniedException.class, java.nio.file.AccessDeniedException.class})
    public ResponseEntity<Map<String, String>> semPermissao(Exception erro) {
        return resposta(HttpStatus.FORBIDDEN, erro.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> erroInesperado(Exception erro) {
        log.error("Erro inesperado na requisição", erro);
        return resposta(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno no servidor. Tente novamente.");
    }

    private String descreverViolacao(ConstraintViolation<?> violacao) {
        return nomeCampo(violacao.getPropertyPath().toString()) + ": " + violacao.getMessage();
    }

    private String nomeCampo(String campo) {
        String ultimo = campo.contains(".") ? campo.substring(campo.lastIndexOf('.') + 1) : campo;
        return NOMES_CAMPOS.getOrDefault(ultimo, ultimo);
    }

    private ResponseEntity<Map<String, String>> resposta(HttpStatus status, String mensagem) {
        return ResponseEntity.status(status).body(Map.of("mensagem", mensagem == null ? status.getReasonPhrase() : mensagem));
    }
}
