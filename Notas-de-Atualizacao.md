# Notas de atualização — Cronos System

## Versão de 19/11/2026

### Novidades

**Professores**
- Carga horária máxima limitada a **44 aulas por semana** (jornada semanal da CLT, art. 318). O sistema avisa ao passar do limite e a API recusa valores maiores.
- O gerador nunca coloca mais de **8 aulas no mesmo dia** para o mesmo professor.
- Cada professor tem uma **cor** própria (14 opções ou automática), usada nas aulas dele na planilha.
- **Disponibilidade mais rápida**: clique e arraste para marcar vários horários, clique no dia para marcar a coluna toda ou no horário para marcar a linha toda, e use os atalhos Manhã, Tarde, Noite e Semana toda.
- Nova aba **Módulo noturno (blocos)** na disponibilidade, com os blocos 19h00–20h50 e 21h05–22h50.

**Disciplinas**
- Novo campo **sigla**, sugerido automaticamente a partir do nome e salvo em maiúsculas.
- Toda escola já começa com as **12 disciplinas da base comum** cadastradas (Português, Matemática, Inglês, Educação Física, Arte, História, Geografia, Filosofia, Sociologia, Física, Química e Biologia).
- A cor saiu da disciplina e passou a ser do professor.

**Turmas**
- Novo turno **Módulo noturno**: as aulas são contadas em blocos, e cada bloco vale 2 aulas.
- O botão **Adicionar divisão** fica azul o tempo todo, não só ao passar o mouse.

**Home e listagens**
- **Sábado** entrou nos dias letivos.
- O card "Aulas cadastradas" foi removido.
- **Barra de pesquisa** nas páginas de professores, disciplinas e turmas. A busca ignora acentos e maiúsculas.

**Planilhas**
- Cada aula mostra a **sigla da disciplina e o professor**, com a cor do professor e o texto em branco ou preto conforme o fundo.
- As turmas do módulo noturno saem numa aba própria: **Grade módulo noturno**.
- **Histórico de planilhas**: as 30 últimas planilhas geradas ficam salvas para baixar de novo ou apagar.

**Visual**
- Os pop-ups do navegador foram trocados por **avisos** no canto da tela (sucesso, erro e alerta) e por uma janela de confirmação antes de apagar.
- O menu lateral de Adicionar turma e Adicionar disciplina ficou igual ao das outras páginas.

### Segurança

Corrigido nesta versão:

| Problema encontrado | Risco | Correção |
| --- | --- | --- |
| A chave que assina o token JWT estava escrita no `application.properties`. Quem visse o repositório conseguia criar um token de **administrador** falso. | Crítico | A chave saiu do código. Agora vem da variável `JWT_SECRET` (mínimo de 32 caracteres); sem ela, a API gera uma chave aleatória a cada início. |
| O login aceitava tentativas de senha sem limite (força bruta). | Alto | Depois de 5 senhas erradas seguidas, aquele e-mail fica bloqueado por 15 minutos no mesmo computador (resposta 429, com mensagem na tela de login). |
| Um nome cadastrado começando com `=` (escola, professor, disciplina ou turma) virava fórmula ao abrir a planilha no Excel (*formula injection*). | Médio | Toda célula da planilha é salva como texto. O conteúdo aparece igual, mas nunca é executado. |

Testes de segurança feitos com a API e o gerador rodando de verdade (**154 verificações, nenhuma falha**):

- **Sem login**: as 15 rotas protegidas respondem 401.
- **Token adulterado**: token inválido, assinado com outra chave, com `alg: none`, expirado, de outro emissor ou com o e-mail trocado — todos recusados.
- **Área do administrador**: uma escola comum não acessa `/admin` nem a lista de usuários (403).
- **Usuários**: uma escola não lê, altera, troca a senha nem apaga o usuário de outra. Também não vira admin, não muda de escola e não assume o e-mail de outra pelo cadastro. A senha nunca aparece nas respostas.
- **Uma escola acessando dados de outra (IDOR)**: ler, alterar e apagar matéria, professor, vínculos, disponibilidade, turma, divisão, horário, grade e planilha usando IDs de outra escola — tudo bloqueado, e os dados originais continuam intactos.
- **Ligar registros a dados de outra escola**: criar ou alterar vínculos e disponibilidades que apontem para professor, turma ou matéria de outra escola — recusado. Enviar `escola` no corpo da requisição não muda a escola do registro.
- **Listagens e painel**: nenhuma listagem, busca ou estatística mostra dados de outra escola.
- **SQL injection**: na busca e no login, sem efeito.
- **Entradas inválidas**: JSON quebrado e IDs inválidos respondem 400, sem mostrar detalhes internos. Carga negativa, cor inválida, turno inexistente e dia inválido são recusados.
- **Upload do histórico**: recusa arquivo que não é planilha, maior que 5 MB ou vazio. Um nome de arquivo com `..\`, aspas ou quebra de linha é limpo antes de salvar, e o download não deixa injetar cabeçalhos.
- **Cabeçalhos e CORS**: as respostas incluem `X-Content-Type-Options`, `X-Frame-Options` e `Cache-Control`. A API e o gerador só aceitam o front-end local.
- **Gerador Python**: sem token ou com token falso responde 401. O administrador (sem escola) não gera planilha.
- **XSS**: nomes com `<script>` e `<img onerror>` aparecem como texto nas páginas e não executam nada.

### Testes automáticos

| Conjunto | Resultado |
| --- | --- |
| Back-end Java (JUnit + MockMvc) | 20 de 20 passaram |
| Gerador Python (unittest) | 19 de 19 passaram |
| Ponta a ponta (API + gerador + planilha) | 19 de 19 passaram |
| Segurança | 154 de 154 passaram |

Testes novos nesta versão:
- **Back-end:**
  - limite de 44 aulas;
  - sigla em maiúsculas;
  - sábado e módulo noturno;
  - histórico separado por escola;
  - histórico recusa arquivo que não é planilha;
  - escola nova com as disciplinas básicas;
  - bloqueio após 5 senhas erradas;
  - token assinado com outra chave.
- **Gerador:**
  - sábado;
  - blocos do módulo noturno;
  - choque entre módulo e noturno;
  - máximo de 8 aulas por dia;
  - sigla e cor na célula;
  - aba do módulo noturno;
  - texto começando com `=` não vira fórmula.

### Para atualizar

1. As migrations `V0005` (sigla, histórico de planilhas e remoção da cor da disciplina) e `V0006` (disciplinas básicas para escolas que já existem) rodam sozinhas quando o back-end inicia.
2. Defina a variável `JWT_SECRET` antes de iniciar o back-end; sem ela, todos precisam entrar de novo a cada reinício.
3. As cores que estavam nas disciplinas não são aproveitadas. Escolha a cor de cada professor na tela de cadastro.
