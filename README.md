# Java Web Server

Servidor web HTTP implementado do zero em Java puro, sem o uso de nenhum
framework ou biblioteca externa. O objetivo é entender como funciona a
comunicação HTTP na camada mais baixa possível, simulando o que ferramentas
como Tomcat, Jetty e o próprio Spring Boot fazem internamente.

---

## Funcionalidades

- Aceita conexões TCP de clientes (navegadores, curl, Postman)
- Lê e interpreta requisições HTTP/1.1 (método, path, headers, body)
- Serve arquivos estáticos da pasta `/public` (HTML, CSS, JS, imagens)
- Detecta automaticamente o `Content-Type` pelo tipo do arquivo
- Retorna `404 Not Found` para arquivos inexistentes
- Suporta rotas dinâmicas para `GET` e `POST`
- Atende múltiplos clientes simultaneamente com ThreadPool de 10 threads
- Trata erros com respostas HTTP adequadas (400, 404, 500)
- Registra logs estruturados com timestamp para cada requisição

---

## Arquitetura

O projeto segue o princípio de **Single Responsibility** do SOLID —
cada classe tem uma única responsabilidade bem definida.

### Fluxo de uma requisição
```
Navegador digita http://localhost:8080
        │
        ▼
    Server.java
    (porta 8080 aberta, loop aguardando conexões)
        │
        │ conexão aceita → nova thread do pool
        ▼
    HttpRequestParser.java
    (lê os bytes do socket e interpreta a requisição)
        │
        │ retorna objeto HttpRequest
        ▼
    Router.java
    (verifica se existe uma rota dinâmica para o path)
        │
        ├── rota encontrada → Handler executa e monta HttpResponse
        │
        └── rota não encontrada → StaticFileHandler.java
                                  (busca arquivo na pasta /public)
                                        │
                                        ├── arquivo existe → lê e serve
                                        └── não existe → 404 Not Found
        │
        ▼
    HttpResponse.java
    (escreve a resposta HTTP no socket)
        │
        ▼
    Navegador recebe e renderiza
```

---

## Estrutura do Projeto
```
java-web-server/
├── src/
│   ├── main/
│   │   └── java/
│   │       └── server/
│   │           ├── Server.java             ← ponto de entrada, orquestra o fluxo
│   │           ├── HttpRequestParser.java  ← interpreta a requisição HTTP
│   │           ├── HttpRequest.java        ← representa a requisição
│   │           ├── HttpResponse.java       ← representa e envia a resposta
│   │           ├── Router.java             ← mapeia URLs para handlers
│   │           ├── Handler.java            ← interface para handlers de rota
│   │           ├── StaticFileHandler.java  ← serve arquivos estáticos
│   │           ├── ErrorHandler.java       ← monta respostas de erro
│   │           └── Logger.java             ← logs com timestamp
│   └── test/
│       └── java/
│           └── server/
│               ├── HttpRequestParserTest.java
│               └── RouterTest.java
├── public/
│   ├── index.html    ← página principal
│   └── style.css     ← estilos
├── pom.xml
├── .gitignore
└── README.md
```

---

## Descrição das Classes

### `Server.java`
Ponto de entrada do projeto. Abre um `ServerSocket` na porta 8080 e fica
em loop infinito aguardando conexões. Para cada conexão aceita, delega o
processamento para uma thread do `ExecutorService`, permitindo atender
múltiplos clientes simultaneamente sem bloquear o loop principal.
Também é responsável por registrar as rotas dinâmicas no `Router`.

### `HttpRequestParser.java`
Recebe o `Socket` do cliente e lê os bytes brutos que chegam pelo
`InputStream`. Interpreta a primeira linha como a request line
(método, path, versão), lê os headers linha a linha separando chave
e valor, e lê o body quando o header `Content-Length` estiver presente.
Retorna um objeto `HttpRequest` com todos os dados estruturados.

### `HttpRequest.java`
POJO que representa uma requisição HTTP. Armazena método, path, versão,
headers e body. Os atributos são `final` — o objeto é imutável após
criado, pois o que o cliente enviou não deve ser alterado.

### `HttpResponse.java`
Representa uma resposta HTTP e sabe como se enviar pelo socket. Armazena
status code, status message, headers e body em bytes. O método `send()`
monta a resposta no formato correto do protocolo HTTP — status line,
headers, linha em branco e body — e escreve diretamente no `OutputStream`
do socket usando UTF-8.

### `Router.java`
Mantém um `Map` de rotas registradas onde a chave é a combinação de
método e path (ex: `GET:/hello`). O método `register()` adiciona uma
nova rota e o método `resolve()` busca o handler correspondente para
uma requisição recebida. Retorna `null` quando nenhuma rota é encontrada,
sinalizando ao servidor que deve tentar servir um arquivo estático.

### `Handler.java`
Interface funcional com um único método `handle(request, response)`.
Define o contrato que todo handler de rota deve seguir. Por ser uma
interface funcional, permite o uso de lambdas ao registrar rotas,
tornando o código mais conciso e legível.

### `StaticFileHandler.java`
Responsável por servir arquivos estáticos da pasta `/public`. Mapeia
o path da URL para o caminho do arquivo no disco, lê o conteúdo com
`Files.readAllBytes()` e detecta o `Content-Type` correto pela extensão
do arquivo usando um mapa de MIME types. O path `/` é automaticamente
mapeado para `/index.html`.

### `ErrorHandler.java`
Centraliza a criação de respostas de erro. Possui métodos estáticos para
montar respostas `400 Bad Request`, `404 Not Found` e
`500 Internal Server Error`, cada uma com uma página HTML simples
explicando o erro. Garante que o servidor sempre retorna uma resposta
HTTP válida, nunca deixando o cliente sem resposta.

### `Logger.java`
Sistema de logs estruturado com timestamp. Possui três métodos estáticos:
`info()` para mensagens gerais, `error()` para erros, e `request()` para
logar cada requisição com método, path, status code e tempo de
processamento em milissegundos. Formato:
`[yyyy-MM-dd HH:mm:ss] MÉTODO path → status (Xms)`.

---

## Teste de Carga

Executado com Apache Bench:
```bash
ab -n 100 -c 10 http://localhost:8080/
```

| Métrica | Resultado |
|---------|-----------|
| Total de requisições | 100 |
| Failed requests | 0 |
| Requests per second | 2061 req/s |
| Time per request | 4.852ms |

---

## Aprendizados

### Socket e ServerSocket
Um `Socket` é um canal de comunicação bidirecional entre dois pontos na
rede. O `ServerSocket` é o "porteiro" que fica aguardando conexões em
uma porta específica. Quando um cliente conecta, o `accept()` retorna
um `Socket` com aquele cliente específico.

### InputStream e OutputStream
Todo `Socket` tem dois canais: o `InputStream` por onde chegam os dados
do cliente e o `OutputStream` por onde enviamos dados de volta. O
`BufferedReader` e o `PrintWriter` são facilitadores que envolvem esses
canais para facilitar leitura e escrita de texto.

### Protocolo HTTP/1.1
Uma requisição HTTP é composta por uma request line (método, path,
versão), headers no formato `Chave: Valor`, uma linha em branco
obrigatória e um body opcional. Uma resposta segue o mesmo padrão com
uma status line (versão, código, mensagem) no lugar da request line.

### MIME Types
São identificadores que informam ao navegador como interpretar o conteúdo
recebido. Sem o `Content-Type` correto, o navegador não sabe se deve
renderizar HTML, aplicar CSS ou exibir uma imagem. Exemplos:
`text/html`, `text/css`, `application/javascript`, `image/png`.

### Concorrência com ThreadPool
Criar uma thread por conexão é insustentável — 1000 conexões gerariam
1000 threads consumindo toda a memória. O `ExecutorService` com
`newFixedThreadPool(10)` limita o número máximo de threads simultâneas.
Conexões excedentes ficam numa fila e são atendidas conforme as threads
ficam livres.