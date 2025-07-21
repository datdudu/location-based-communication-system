# Location-Based Communication System

## Descrição

Este projeto implementa um sistema de comunicação baseado em localização, desenvolvido para a disciplina de Programação Paralela e Distribuída (PPD) do IFCE. O sistema permite que usuários troquem mensagens síncronas e assíncronas, dependendo do status (online/offline) e da proximidade geográfica (raio configurável).

## Funcionalidades

- **Comunicação Síncrona:**  
  Usuários online e dentro do raio de comunicação podem trocar mensagens em tempo real via sockets.

- **Comunicação Assíncrona:**  
  Usuários offline ou fora do raio recebem mensagens via uma fila (RabbitMQ), que só são entregues quando ficam online.

- **Lista de Contatos:**  
  Cada usuário mantém sua própria lista de contatos, com informações de nome, localização, status, IP e porta.

- **Atualização de Status e Localização:**  
  Usuários podem atualizar sua localização, status (online/offline) e raio de comunicação a qualquer momento.

- **Notificações de Raio:**  
  O sistema notifica quando um contato entra ou sai do raio de comunicação.

- **Interface Gráfica:**  
  Interface intuitiva para configuração inicial, visualização de contatos, envio de mensagens e atualização de dados.

## Como Funciona

- Ao iniciar, o usuário informa seu nome, porta e dados de um contato inicial.
- O sistema abre uma janela principal onde é possível:
    - Visualizar e editar localização, status e raio.
    - Adicionar/remover contatos.
    - Enviar mensagens para contatos.
    - Visualizar mensagens recebidas (com identificação do remetente).
- A comunicação síncrona ocorre diretamente entre clientes via sockets.
- A comunicação assíncrona utiliza RabbitMQ como middleware de mensagens.

## Como Executar

### Pré-requisitos

- **Java 11+**
- **RabbitMQ** rodando localmente (localhost, porta padrão 5672)
- **Maven** (opcional, se desejar compilar via linha de comando)

### Passos

1. **Clone o repositório ou extraia o zip do projeto.**
2. **Certifique-se de que o RabbitMQ está rodando localmente.**
    - Instale o RabbitMQ: https://www.rabbitmq.com/download.html
    - Inicie o serviço: `rabbitmq-server`
3. **Compile o projeto:**
    - Via IDE (IntelliJ, Eclipse, etc): importe como projeto Maven/Java.
    - Via terminal:
      ```sh
      javac -cp ".:path/to/rabbitmq-client.jar" com/locationBasedCommunicationSystem/**/*.java
      ```
4. **Execute a aplicação:**
    - Via IDE: rode a classe `com.locationBasedCommunicationSystem.Application`
    - Via terminal:
      ```sh
      java -cp ".:path/to/rabbitmq-client.jar" com.locationBasedCommunicationSystem.Application
      ```
5. **Abra múltiplas instâncias para simular diferentes usuários.**
    - Configure nome, porta e contatos iniciais em cada janela.

## Observações

- O sistema é totalmente P2P, sem servidor central.
- As mensagens assíncronas só aparecem quando o usuário está online.
- O raio de comunicação pode ser ajustado a qualquer momento.
- O status dos contatos é atualizado automaticamente ao entrar/sair do raio.

## Autor

- Carlos Eduardo Carvalho Cardoso
- Engenharia de Computação - IFCE
- Projeto Final de Programação Paralela e Distribuída (2025.1)