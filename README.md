# Cronos System

Sistema web para montar a grade de horários das ETECs de forma automática, respeitando a disponibilidade dos professores, a carga horária máxima e o interstício de 11 horas entre um dia de trabalho e outro.

Trabalho de Conclusão de Curso do Técnico em Desenvolvimento de Sistemas.

## Como funciona

1. O administrador cadastra cada escola e o usuário de acesso dela. A escola já nasce com as disciplinas da base comum (Língua Portuguesa, Matemática, Inglês, Educação Física, Arte, História, Geografia, Filosofia, Sociologia, Física, Química e Biologia).
2. A escola entra no sistema e cadastra disciplinas (com sigla), professores (com cor, as disciplinas que lecionam e a disponibilidade) e turmas (com as aulas por semana de cada disciplina e as aulas divididas A/B).
3. Na página **Planilhas**, o sistema monta a grade e entrega um arquivo Excel com:
   - **Grade**: a grade completa, com a sigla da disciplina e o professor em cada aula, colorida pela cor do professor, com intervalos, aulas duplas e aulas divididas;
   - **Grade módulo noturno**: aba separada para as turmas do módulo noturno, em blocos de duas aulas;
   - **Erros**: o que não coube, o motivo e como resolver;
   - **Solução sugerida** e **Ajustes da solução**: uma grade possível e os horários que os professores precisariam liberar.
4. Toda planilha gerada fica no **histórico** da página Planilhas (as 30 mais recentes) para baixar de novo.

## Arquitetura

| Parte | Tecnologia | Porta | Pasta |
| --- | --- | --- | --- |
| Front-end | HTML, CSS, JavaScript, Bootstrap 5 | 5500 | `Auth/` |
| Back-end (API) | Java 21, Spring Boot 4, Spring Security + JWT, JPA, Flyway | 8080 | `backend/cronosystem/` |
| Gerador de grade | Python 3, Flask, openpyxl | 5000 | `gerador/` |
| Banco de dados | MySQL 8 | 3306 | migrations em `backend/cronosystem/src/main/resources/db/migration/` |

- Cada escola só enxerga os próprios dados: todas as tabelas principais têm `id_escola` e o back-end filtra pela escola do usuário logado.
- O gerador Python recebe o token do usuário, busca os dados da escola na API Java e devolve a planilha.

## Como executar

O passo a passo completo está em [Manual-Inicializacao.md](Manual-Inicializacao.md). Resumo:

1. Inicie o MySQL.
2. Back-end: `cd backend/cronosystem` e `.\mvnw.cmd spring-boot:run`
3. Gerador: `cd gerador`, `py -m pip install -r requirements.txt` e `py app.py`
4. Front-end: na pasta acima do projeto, `py -m http.server 5500` e abra `http://localhost:5500/Sistema-de-horas/`

Usuário administrador criado pela migration: `admin@cronos.com` / `admin123` (troque a senha antes de usar de verdade).

Defina a variável de ambiente `JWT_SECRET` com uma chave própria de pelo menos 32 caracteres. Sem ela, a API cria uma chave aleatória toda vez que inicia, e todos precisam entrar de novo depois de reiniciar o back-end.

```powershell
$env:JWT_SECRET = "troque-por-uma-frase-grande-so-sua-com-32-ou-mais"
.\mvnw.cmd spring-boot:run
```

## Segurança

- Login com token JWT assinado (HS256, expira em 8 horas); a chave nunca fica no código.
- Depois de 5 senhas erradas seguidas, o e-mail fica bloqueado por 15 minutos naquele computador.
- Senhas guardadas com BCrypt; a API nunca devolve a senha.
- Cada escola só acessa os próprios registros, inclusive o histórico de planilhas.
- Rotas de administrador só funcionam para o usuário admin.
- O histórico só aceita planilhas `.xlsx` de até 5 MB, e o nome do arquivo é limpo antes de salvar.
- Textos cadastrados nunca viram fórmula no Excel, e as páginas mostram nomes como texto (sem executar HTML).
- O detalhe completo dos testes de segurança está em [Notas-de-Atualizacao.md](Notas-de-Atualizacao.md).

## Testes

```powershell
cd backend/cronosystem
.\mvnw.cmd test
```

```powershell
cd gerador
py -m unittest discover -s tests
```

Os testes do back-end usam um banco H2 em memória, então não precisam do MySQL rodando.

## Regras usadas na montagem da grade

- O professor só recebe aula nos horários que marcou como disponíveis.
- Um professor nunca fica em duas turmas no mesmo horário.
- Interstício de 11 horas entre a última aula de um dia e a primeira do dia seguinte.
- O professor não passa da carga horária máxima semanal cadastrada (no máximo 44 aulas, jornada semanal da CLT, art. 318) nem de 8 aulas no mesmo dia.
- A turma só tem aula no seu turno (Integral = manhã e tarde; módulo noturno = blocos de 19h00 - 20h50 e 21h05 - 22h50, cada bloco vale 2 aulas).
- Um professor não pega um bloco do módulo e uma aula do noturno comum que aconteçam ao mesmo tempo.
- Apenas os dias letivos marcados na Home são usados (de segunda a sábado).
- Aulas seguidas da mesma disciplina e professor viram aula dupla; o intervalo quebra a aula dupla.
- Divisões cadastradas na turma permitem duas disciplinas no mesmo horário (turma A e turma B), com professores diferentes.

## Suporte

WhatsApp: +55 (14) 99866-7190 · E-mail: cronossystem@gmail.com
