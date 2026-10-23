# Manual de Inicializacao do Sistema de Horas

## 1. Pre-requisitos

Antes de iniciar o sistema, confirme que estes itens estao instalados e funcionando:

- Java JDK 21
- MySQL Server
- Python 3
- Um navegador, como Chrome ou Edge

O banco usado pelo backend e `bd_crono`. Conforme a configuracao atual, o usuario do MySQL e `root` e a senha esta vazia.

## 2. Iniciar o MySQL

Abra o MySQL pelo servico instalado no Windows ou pelo painel do XAMPP/WampServer. O servidor deve estar ativo na porta `3306`.

O Spring Boot cria o banco `bd_crono` automaticamente quando necessario.

## 3. Iniciar o backend

Abra um terminal PowerShell e execute:

```powershell
cd "C:\Users\Aluno\Downloads\Sistema-de-horas\backend\cronosystem"

$env:JAVA_HOME="C:\Program Files\Java\jdk-21"
$env:Path="$env:JAVA_HOME\bin;$env:Path"

.\mvnw.cmd spring-boot:run
```

Mantenha esse terminal aberto. O backend ficara disponivel em:

`http://localhost:8080`

Na primeira vez, o Flyway cria todas as tabelas e o usuario administrador:

- E-mail: `admin@cronos.com`
- Senha: `admin123`

Se o banco `bd_crono` ja existia de uma versao antiga do sistema, apague-o antes de iniciar o backend:

```sql
DROP DATABASE bd_crono;
```

## 3.1. Iniciar o gerador de planilhas (Python)

Abra outro terminal PowerShell e execute:

```powershell
cd "C:\Users\Aluno\Downloads\Sistema-de-horas\gerador"

py -m pip install -r requirements.txt
py app.py
```

Mantenha esse terminal aberto. O gerador ficara disponivel em:

`http://localhost:5000`

Ele busca os dados da escola logada no backend e devolve a planilha em Excel para a pagina Planilhas.

## 4. Iniciar o frontend

Abra um segundo terminal PowerShell e execute:

```powershell
cd "C:\Users\Aluno\Downloads"
py -m http.server 5500
```

Mantenha esse terminal aberto. O servidor do frontend ficara disponivel em:

`http://localhost:5500`

## 5. Abrir o sistema

Abra este endereco no navegador:

`http://localhost:5500/Sistema-de-horas/`

O arquivo `index.html` redireciona automaticamente para a tela de login.

1. Entre com o administrador e cadastre a escola e o usuario de acesso dela.
2. Saia e entre com o usuario da escola.
3. Cadastre as disciplinas, os professores (com disciplinas e disponibilidade) e as turmas (com as aulas por semana de cada disciplina).
4. Na pagina Planilhas, clique em "Gerar planilha".

## 6. Ordem correta

1. Inicie o MySQL.
2. Inicie o backend na porta `8080`.
3. Inicie o gerador de planilhas na porta `5000`.
4. Inicie o frontend na porta `5500`.
5. Abra `http://localhost:5500/Sistema-de-horas/`.

## 7. Inserir dados pela API

Todas as rotas, menos o login, exigem o token. Primeiro faca o login:

```powershell
$login = @{
    email = "secretaria@etec.sp.gov.br"
    senha = "sua-senha"
} | ConvertTo-Json

$token = (Invoke-RestMethod `
    -Uri "http://localhost:8080/auth/login" `
    -Method Post `
    -ContentType "application/json" `
    -Body $login).token

$headers = @{ Authorization = "Bearer $token" }
```

Exemplo de cadastro de professor no PowerShell:

```powershell
$body = @{
    nome = "Maria Silva"
    chm = 20
} | ConvertTo-Json

Invoke-RestMethod `
    -Uri "http://localhost:8080/prof" `
    -Method Post `
    -Headers $headers `
    -ContentType "application/json" `
    -Body $body
```

Exemplo de cadastro de disciplina:

```powershell
$body = @{ nome = "Matematica" } | ConvertTo-Json

Invoke-RestMethod `
    -Uri "http://localhost:8080/materia" `
    -Method Post `
    -Headers $headers `
    -ContentType "application/json" `
    -Body $body
```

Nao envie o campo `id`: ele e gerado automaticamente pelo banco.

## 8. Problemas comuns

### JAVA_HOME nao configurado

Execute novamente:

```powershell
$env:JAVA_HOME="C:\Program Files\Java\jdk-21"
$env:Path="$env:JAVA_HOME\bin;$env:Path"
```

### Porta 8080 ocupada

Feche outro backend que esteja rodando ou altere a porta no arquivo `application.properties`.

### Pagina nao abre ou aparece sem estilos

Confirme que o terminal do frontend esta executando o servidor na pasta `C:\Users\Aluno\Downloads` e abra a URL completa do sistema.

### Contadores aparecem como zero

Confirme que o backend esta rodando e atualize a Home com `Ctrl + F5`. A Home consulta `http://localhost:8080/dashboard/estatisticas`.

### "Nao foi possivel conectar ao gerador de planilhas"

Confirme que o terminal do gerador esta executando `py app.py` na pasta `gerador`.

### A planilha veio com aulas pendentes

Abra a aba "Erros" da planilha: ela mostra a turma, a disciplina, o motivo e como resolver. As abas "Solucao sugerida" e "Ajustes da solucao" mostram uma grade possivel e quais horarios os professores precisariam liberar.

## 9. Rodar os testes automatizados

Back-end (usa banco H2 em memoria, nao precisa do MySQL):

```powershell
cd "C:\Users\Aluno\Downloads\Sistema-de-horas\backend\cronosystem"
.\mvnw.cmd test
```

Gerador de planilhas:

```powershell
cd "C:\Users\Aluno\Downloads\Sistema-de-horas\gerador"
py -m unittest discover -s tests
```

## 10. Parar os servidores

No terminal de cada servidor, pressione `Ctrl + C`.
