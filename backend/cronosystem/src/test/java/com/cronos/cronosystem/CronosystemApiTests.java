package com.cronos.cronosystem;

import com.cronos.cronosystem.model.Usuario;
import com.cronos.cronosystem.repository.UsuarioRepository;
import com.jayway.jsonpath.JsonPath;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.Filter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Date;
import java.util.UUID;

import static org.hamcrest.Matchers.startsWith;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class CronosystemApiTests {

    @Autowired
    private WebApplicationContext contexto;

    @Autowired
    private Filter springSecurityFilterChain;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private MockMvc mvc;

    private String tokenAdmin;

    @BeforeEach
    void preparar() throws Exception {
        mvc = MockMvcBuilders.webAppContextSetup(contexto)
                .addFilters(springSecurityFilterChain)
                .build();

        if (usuarioRepository.findByEmail("admin@teste.com").isEmpty()) {
            Usuario admin = new Usuario();
            admin.setNome("Administrador");
            admin.setEmail("admin@teste.com");
            admin.setSenha(passwordEncoder.encode("admin123"));
            admin.setAdmin(true);
            usuarioRepository.save(admin);
        }

        tokenAdmin = login("admin@teste.com", "admin123");
    }

    private String login(String email, String senha) throws Exception {
        MvcResult resultado = mvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"" + email + "\",\"senha\":\"" + senha + "\"}"))
                .andExpect(status().isOk())
                .andReturn();

        return JsonPath.read(resultado.getResponse().getContentAsString(), "$.token");
    }

    private String criarEscola() throws Exception {
        String email = "escola-" + UUID.randomUUID() + "@teste.com";

        mvc.perform(post("/admin/escola")
                        .header("Authorization", "Bearer " + tokenAdmin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nomeEscola\":\"ETEC Teste\",\"nome\":\"Secretaria\",\"email\":\"" + email + "\",\"senha\":\"senha123\"}"))
                .andExpect(status().isOk());

        return login(email, "senha123");
    }

    private ResultActions enviar(String token, String metodo, String caminho, String json) throws Exception {
        var requisicao = switch (metodo) {
            case "POST" -> post(caminho);
            case "PUT" -> put(caminho);
            case "DELETE" -> delete(caminho);
            default -> get(caminho);
        };

        if (json != null) {
            requisicao.contentType(MediaType.APPLICATION_JSON).content(json);
        }

        return mvc.perform(requisicao.header("Authorization", "Bearer " + token));
    }

    private long criarId(String token, String caminho, String json) throws Exception {
        String resposta = enviar(token, "POST", caminho, json)
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        return ((Number) JsonPath.read(resposta, "$.id")).longValue();
    }

    @Test
    void rotaProtegidaSemTokenRetorna401() throws Exception {
        mvc.perform(get("/prof")).andExpect(status().isUnauthorized());
        mvc.perform(get("/prof").header("Authorization", "Bearer token-falso")).andExpect(status().isUnauthorized());
    }

    @Test
    void loginComSenhaErradaRetorna401() throws Exception {
        mvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"admin@teste.com\",\"senha\":\"errada\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.mensagem").value("E-mail ou senha inválidos."));
    }

    @Test
    void loginBloqueiaDepoisDeCincoSenhasErradas() throws Exception {
        String email = "bloqueio-" + UUID.randomUUID() + "@teste.com";

        mvc.perform(post("/admin/escola")
                        .header("Authorization", "Bearer " + tokenAdmin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nomeEscola\":\"ETEC Teste\",\"nome\":\"Secretaria\",\"email\":\"" + email + "\",\"senha\":\"senha123\"}"))
                .andExpect(status().isOk());

        for (int i = 0; i < 5; i++) {
            mvc.perform(post("/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"email\":\"" + email + "\",\"senha\":\"errada" + i + "\"}"))
                    .andExpect(status().isUnauthorized());
        }

        mvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"" + email + "\",\"senha\":\"senha123\"}"))
                .andExpect(status().isTooManyRequests())
                .andExpect(jsonPath("$.mensagem", startsWith("Muitas tentativas")));
    }

    @Test
    void tokenAssinadoComOutraChaveRetorna401() throws Exception {
        String tokenFalso = Jwts.builder()
                .setIssuer("cronosystem")
                .setSubject("admin@teste.com")
                .setExpiration(new Date(System.currentTimeMillis() + 3600000))
                .signWith(Keys.secretKeyFor(SignatureAlgorithm.HS256))
                .compact();

        mvc.perform(get("/admin/escola").header("Authorization", "Bearer " + tokenFalso))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void escolaNaoAcessaAreaDoAdministrador() throws Exception {
        String escola = criarEscola();

        enviar(escola, "GET", "/admin/escola", null).andExpect(status().isForbidden());
        enviar(escola, "GET", "/usuario", null).andExpect(status().isForbidden());
    }

    @Test
    void escolaNaoEnxergaDadosDeOutraEscola() throws Exception {
        String escolaA = criarEscola();
        String escolaB = criarEscola();

        long profId = criarId(escolaA, "/prof", "{\"nome\":\"Maria Silva\",\"chm\":20}");

        enviar(escolaB, "GET", "/prof/" + profId, null).andExpect(status().isNotFound());
        enviar(escolaB, "DELETE", "/prof/" + profId, null).andExpect(status().isNotFound());
        enviar(escolaB, "GET", "/prof", null).andExpect(jsonPath("$.length()").value(0));
        enviar(escolaA, "GET", "/prof/" + profId, null).andExpect(jsonPath("$.nome").value("Maria Silva"));
    }

    @Test
    void validacaoRetornaMensagemComNomeDoCampo() throws Exception {
        String escola = criarEscola();

        enviar(escola, "POST", "/prof", "{\"nome\":\"Prof\",\"chm\":100}")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensagem").value(startsWith("Carga horária máxima")));

        enviar(escola, "POST", "/materia", "{\"nome\":\"   \"}").andExpect(status().isBadRequest());
        enviar(escola, "POST", "/turma", "{\"nome\":\"1 DS\",\"turno\":\"MADRUGADA\"}").andExpect(status().isBadRequest());
        enviar(escola, "POST", "/materia-prof", "{\"materia\":{\"id\":1}}").andExpect(status().isBadRequest());
    }

    @Test
    void disciplinaRepetidaRetorna409() throws Exception {
        String escola = criarEscola();

        criarId(escola, "/materia", "{\"nome\":\"Programação Web\"}");
        enviar(escola, "POST", "/materia", "{\"nome\":\"PROGRAMAÇÃO WEB\"}").andExpect(status().isConflict());
    }

    @Test
    void escolaNovaJaVemComMateriasBasicas() throws Exception {
        String escola = criarEscola();

        enviar(escola, "GET", "/materia", null)
                .andExpect(jsonPath("$.length()").value(12))
                .andExpect(jsonPath("$[?(@.nome == 'Matemática')].sigla").value("MAT"))
                .andExpect(jsonPath("$[?(@.nome == 'Língua Portuguesa')].sigla").value("PORT"));

        enviar(escola, "POST", "/materia", "{\"nome\":\"matemática\"}").andExpect(status().isConflict());
    }

    @Test
    void divisaoExigeDisciplinasCadastradasNaTurma() throws Exception {
        String escola = criarEscola();

        long turma = criarId(escola, "/turma", "{\"nome\":\"1 DS\",\"turno\":\"MANHA\"}");
        long web = criarId(escola, "/materia", "{\"nome\":\"Web\"}");
        long banco = criarId(escola, "/materia", "{\"nome\":\"Banco\"}");
        String divisao = "{\"turma\":{\"id\":" + turma + "},\"materiaA\":{\"id\":" + web + "},\"materiaB\":{\"id\":" + banco + "}}";

        enviar(escola, "POST", "/divisao", divisao).andExpect(status().isBadRequest());

        criarId(escola, "/turma-materia", "{\"turma\":{\"id\":" + turma + "},\"materia\":{\"id\":" + web + "},\"qtdAulas\":2}");
        criarId(escola, "/turma-materia", "{\"turma\":{\"id\":" + turma + "},\"materia\":{\"id\":" + banco + "},\"qtdAulas\":2}");

        enviar(escola, "POST", "/divisao", divisao).andExpect(status().isOk());
    }

    @Test
    void disponibilidadeRejeitaHorarioInvertido() throws Exception {
        String escola = criarEscola();
        long prof = criarId(escola, "/prof", "{\"nome\":\"Junior\",\"chm\":10}");

        enviar(escola, "POST", "/disp-prof/professor/" + prof, "[{\"dia\":\"SEG\",\"horaInicio\":\"10:00\",\"horaFim\":\"09:00\"}]")
                .andExpect(status().isBadRequest());

        enviar(escola, "POST", "/disp-prof/professor/" + prof, "[{\"dia\":\"SEG\",\"horaInicio\":\"07:20\",\"horaFim\":\"08:10\"}]")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void excluirProfessorApagaOsVinculos() throws Exception {
        String escola = criarEscola();
        long prof = criarId(escola, "/prof", "{\"nome\":\"Leandro\",\"chm\":10}");
        long materia = criarId(escola, "/materia", "{\"nome\":\"Lógica\"}");

        criarId(escola, "/materia-prof", "{\"prof\":{\"id\":" + prof + "},\"materia\":{\"id\":" + materia + "}}");
        enviar(escola, "POST", "/disp-prof/professor/" + prof, "[{\"dia\":\"TER\",\"horaInicio\":\"07:20\",\"horaFim\":\"08:10\"}]");

        enviar(escola, "DELETE", "/prof/" + prof, null).andExpect(status().isOk());
        enviar(escola, "GET", "/materia-prof", null).andExpect(jsonPath("$.length()").value(0));
        enviar(escola, "GET", "/disp-prof", null).andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void trocarSenhaComSenhaAtualErradaRetorna400() throws Exception {
        String escola = criarEscola();
        String usuario = enviar(escola, "GET", "/usuario/me", null).andReturn().getResponse().getContentAsString();
        Number id = JsonPath.read(usuario, "$.id");

        enviar(escola, "PUT", "/usuario/" + id + "/senha", "{\"senhaAtual\":\"errada\",\"novaSenha\":\"nova12345\"}")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensagem").value("Senha atual incorreta."));
    }

    @Test
    void respostaDoUsuarioNaoExpoeSenha() throws Exception {
        String escola = criarEscola();

        String resposta = enviar(escola, "GET", "/usuario/me", null).andReturn().getResponse().getContentAsString();
        assertFalse(resposta.contains("senha"));
    }

    @Test
    void cargaHorariaRespeitaLimiteDaJornadaSemanal() throws Exception {
        String escola = criarEscola();

        criarId(escola, "/prof", "{\"nome\":\"Andreia\",\"chm\":44,\"cor\":\"#7B1FA2\"}");

        enviar(escola, "POST", "/prof", "{\"nome\":\"Andreia\",\"chm\":45}")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensagem").value(startsWith("Carga horária máxima")));

        enviar(escola, "POST", "/prof", "{\"nome\":\"Andreia\",\"chm\":20,\"cor\":\"roxo\"}")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensagem").value(startsWith("Cor")));
    }

    @Test
    void disciplinaGuardaSiglaEmMaiusculo() throws Exception {
        String escola = criarEscola();

        long materia = criarId(escola, "/materia", "{\"nome\":\"Química Geral\",\"sigla\":\" qge \"}");

        enviar(escola, "GET", "/materia/" + materia, null).andExpect(jsonPath("$.sigla").value("QGE"));
    }

    @Test
    void escolaAceitaSabadoEModuloNoturno() throws Exception {
        String escola = criarEscola();

        enviar(escola, "PUT", "/escola", "{\"nome\":\"ETEC Teste\",\"diasLetivos\":\"Segunda,Sábado\"}")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.diasLetivos").value("Segunda,Sábado"));

        criarId(escola, "/turma", "{\"nome\":\"1 QUI\",\"turno\":\"MODULO\"}");

        enviar(escola, "POST", "/disp-prof/professor/" + criarId(escola, "/prof", "{\"nome\":\"Junior\",\"chm\":10}"),
                "[{\"dia\":\"SAB\",\"horaInicio\":\"19:00\",\"horaFim\":\"20:50\"}]")
                .andExpect(status().isOk());
    }

    @Test
    void historicoDePlanilhasFicaSoNaEscolaQueGerou() throws Exception {
        String escolaA = criarEscola();
        String escolaB = criarEscola();
        byte[] conteudo = {'P', 'K', 3, 4, 1, 2, 3};

        String resposta = mvc.perform(multipart("/planilha")
                        .file(new MockMultipartFile("arquivo", "Grade_ETEC.xlsx", "application/octet-stream", conteudo))
                        .param("completa", "false")
                        .param("aulasPendentes", "3")
                        .param("solucaoSugerida", "true")
                        .header("Authorization", "Bearer " + escolaA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nomeArquivo").value("Grade_ETEC.xlsx"))
                .andExpect(jsonPath("$.aulasPendentes").value(3))
                .andReturn().getResponse().getContentAsString();

        long id = ((Number) JsonPath.read(resposta, "$.id")).longValue();

        enviar(escolaA, "GET", "/planilha", null).andExpect(jsonPath("$.length()").value(1));
        enviar(escolaB, "GET", "/planilha", null).andExpect(jsonPath("$.length()").value(0));
        enviar(escolaB, "GET", "/planilha/" + id + "/arquivo", null).andExpect(status().isNotFound());

        byte[] baixado = enviar(escolaA, "GET", "/planilha/" + id + "/arquivo", null)
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsByteArray();
        assertArrayEquals(conteudo, baixado);

        enviar(escolaA, "DELETE", "/planilha/" + id, null).andExpect(status().isOk());
        enviar(escolaA, "GET", "/planilha", null).andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void historicoRecusaArquivoQueNaoEPlanilha() throws Exception {
        String escola = criarEscola();

        mvc.perform(multipart("/planilha")
                        .file(new MockMultipartFile("arquivo", "virus.exe", "application/octet-stream", new byte[]{'M', 'Z', 0, 0}))
                        .header("Authorization", "Bearer " + escola))
                .andExpect(status().isBadRequest());

        mvc.perform(multipart("/planilha").header("Authorization", "Bearer " + escola))
                .andExpect(status().isBadRequest());
    }
}
