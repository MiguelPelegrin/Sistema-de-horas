# Cronos System

Sistema web para montar a grade de horários das ETECs de forma automática, respeitando a disponibilidade dos professores, a carga horária máxima e o interstício de 11 horas entre um dia de trabalho e outro.

Trabalho de Conclusão de Curso do Técnico em Desenvolvimento de Sistemas.

## Como funciona

1. O administrador cadastra cada escola e o usuário de acesso dela.
2. A escola entra no sistema e cadastra disciplinas, professores (com as disciplinas que lecionam e a disponibilidade) e turmas (com as aulas por semana de cada disciplina e as aulas divididas A/B).
3. Na página **Planilhas**, o sistema monta a grade e entrega um arquivo Excel com:
   - **Grade**: a grade completa, colorida por disciplina, com intervalos, aulas duplas e aulas divididas;
   - **Erros**: o que não coube, o motivo e como resolver;
   - **Solução sugerida** e **Ajustes da solução**: uma grade possível e os horários que os professores precisariam liberar.

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

Para produção, defina a variável de ambiente `JWT_SECRET` com uma chave própria de pelo menos 32 caracteres.

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
- O professor não passa da carga horária máxima semanal cadastrada.
- A turma só tem aula no seu turno (Integral = manhã e tarde).
- Apenas os dias letivos marcados na Home são usados.
- Aulas seguidas da mesma disciplina e professor viram aula dupla; o intervalo quebra a aula dupla.
- Divisões cadastradas na turma permitem duas disciplinas no mesmo horário (turma A e turma B), com professores diferentes.

## Suporte

WhatsApp: +55 (14) 99866-7190 · E-mail: cronossystem@gmail.com
