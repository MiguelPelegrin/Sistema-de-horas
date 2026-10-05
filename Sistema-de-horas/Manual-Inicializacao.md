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

A API de estatisticas usada pela Home fica em:

`http://localhost:8080/dashboard/estatisticas`

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

O arquivo `index.html` redireciona automaticamente para a Home.

## 6. Ordem correta

1. Inicie o MySQL.
2. Inicie o backend na porta `8080`.
3. Inicie o frontend na porta `5500`.
4. Abra `http://localhost:5500/Sistema-de-horas/`.

## 7. Inserir dados pela API

Exemplo de cadastro de professor no PowerShell:

```powershell
$body = @{
    nome = "Maria Silva"
    chm = 20
} | ConvertTo-Json

Invoke-RestMethod `
    -Uri "http://localhost:8080/prof" `
    -Method Post `
    -ContentType "application/json" `
    -Body $body
```

Exemplo de cadastro de disciplina:

```powershell
$body = @{ nome = "Matematica" } | ConvertTo-Json

Invoke-RestMethod `
    -Uri "http://localhost:8080/materia" `
    -Method Post `
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

## 9. Parar os servidores

No terminal de cada servidor, pressione `Ctrl + C`.
