# <p align="center">SISTEMA DE GESTÃO DE TICKETS E NOTIFICAÇÕES</p>

Este projeto é uma API de gerenciamento de tickets de suporte, agentes, clientes, notificações e histórico de atividades. O sistema utiliza RabbitMQ para comunicação assíncrona entre os serviços, garantindo 
rastreamento completo de eventos e visibilidade sobre o histórico de tickets.

## Arquitetura

Neste projeto é utilizada a arquitetura em camadas, onde a Repository Layer é responsável pelas operações de banco de dados, a Service Layer concentra a lógica de negócio e a Controller Layer atua como interface REST. Essa abordagem facilita a execução de testes e a realização de manutenções futuras, pois cada camada possui um papel bem definido.

## Tecnologias

- Java 21;
- Spring Boot 3.5.4;
- Spring Security;
- JWT 0.11.5;
- RabbitMQ;
- Docker;
- Lombok;
- PostgreSQL;
- JUnit e Mockito;
- Swagger OpenAPI.

## Funcionalidades

**Autenticação via Usuário/Senha e geração de token JWT com Spring Security:** A escolha desta forma de autenticação se deu por ser simples para os usuários com a parte de login, mas ainda assim fornecer uma camada de segurança através da geração de um token JWT para validar a sessão. Quando o usuário insere as credenciais corretas, é gerado um token que expirará após um certo período de tempo.

<img width="1370" height="500" alt="Geração token" src="https://github.com/user-attachments/assets/cdfe99f5-9d75-4415-b8bf-476fc60ff702" />

**Proteção de endpoints com Spring Security:** Defini que a autorização será baseada em 4 roles distintas (ADMIN, SUPERVISOR, ATENDENTE E CLIENTE) e organizei o de acesso aos endpoints baseados nelas. Também utilizei o @PreAutorize para implementar segurança em métodos em que apenas um usuário com uma role específica pode ter acesso.

<img width="1069" height="268" alt="PreAuthorize" src="https://github.com/user-attachments/assets/2c4245b4-0499-4bd3-b96a-1309654b6d26" />

**DTOs de Request/Response:** Adotei o padrão DTO para garantir que nenhuma informação sensível, como por exemplo a senha de um usuário, ficasse acessível para um terceiro. Além disso, é uma boa prática do Spring utilizar DTOs para manipulação de dados ao invés das entidades para garantir uma manutenção mais fácil da aplicação. Para as validações, utilizei a biblioteca Jakarta Validation em conjunto com expressões regulares (regex).

<img width="1122" height="237" alt="regex" src="https://github.com/user-attachments/assets/ff914d20-a0b7-4f4b-a13f-ae083284076d" />

**Mappers para conversão de dados:** Juntamente ao padrão DTO implementei classes de mappers, com o objetivo de facilitar a conversão entidade/DTO e vice-versa.

<img width="948" height="600" alt="Mapper" src="https://github.com/user-attachments/assets/1d78d5f4-459e-4531-a09e-f33fefc331d8" />

**Tratamento global de exceções:** O tratamento de exceções está centralizado em um @ControllerAdvice GlobalExceptionHandler, que captura os erros mais comuns e retorna respostas mais amigáveis ao usuário.

<img width="1099" height="792" alt="exception" src="https://github.com/user-attachments/assets/a2db7526-bc9d-4758-807c-ea650190b699" />

**Comunicação assíncrona com RabbitMQ:** O projeto utiliza RabbitMQ para permitir que serviços diferentes troquem mensagens de forma assíncrona, garantindo desacoplamento e alta escalabilidade. Entre as filas implementadas estão as seguintes:

  **1.** Ticket History Queue (ticket.history.queue):

    Registra automaticamente eventos de tickets (criação, alteração de status, prioridade, designação de agente);
    Mensagens enviadas são consumidas pelo TicketHistoryService, que persiste os eventos no banco;
    Possui DLQ configurada para mensagens que não puderam ser processadas corretamente.

<img width="1158" height="651" alt="ticketHistoryQueue" src="https://github.com/user-attachments/assets/92d2e1e1-1a9c-492d-90d5-5fca8d08e73e" />

  **2.** Notification Queue (notification.queue):

  Recebe eventos relacionados a notificações de tickets (envio por e-mail ou SMS);
  Mensagens são processadas pelo NotificationService e persistidas na tabela notifications;
  Também configurada com DLQ para tratamento de falhas.

<img width="1197" height="786" alt="notification QUeue" src="https://github.com/user-attachments/assets/84b1eff7-8ec2-4fa0-bf29-df870bf99342" />

  **3.** Dead Letter Queue (DLQ)

  Filas DLQ recebem mensagens que não puderam ser processadas pelo consumidor original;
  Permite análise de falhas e reprocessamento, garantindo confiabilidade na comunicação assíncrona.
      
<img width="927" height="372" alt="dlq" src="https://github.com/user-attachments/assets/687c68ec-e981-4203-8257-1c8e0c16aaf0" />

**Docker e Docker Compose:** O projeto utiliza Docker e Docker Compose para facilitar a configuração e execução de todo o ecossistema, eliminando a necessidade de instalar manualmente o PostgreSQL e o RabbitMQ na máquina local.

<img width="723" height="417" alt="docker" src="https://github.com/user-attachments/assets/ba14bf58-42cf-4e74-b727-76e17d78e7eb" />

**Documentação dos endpoints:** A documentação dos endpoints foi realizada através do Swagger, com a possibilidade de testar as requisições diretamente no navegador e com exemplos já estruturados para maior facilidade de entendimento.

<img width="1862" height="654" alt="Swagger" src="https://github.com/user-attachments/assets/24b5bebf-9f28-4004-a958-f18239946e5b" />

## Regras de Negócio

**AuthenticationService**

Atualização de Senha:

Autenticação obrigatória: Somente usuários autenticados podem alterar a senha;
Conta ativa: A conta do usuário deve estar ativa (isActive = true). Caso contrário, a operação é bloqueada;
Confirmação da senha atual: A senha atual fornecida deve corresponder à senha armazenada (criptografada);
Nova senha diferente: A nova senha não pode ser igual à senha atual;
Criptografia: A nova senha deve ser criptografada antes de ser salva no banco de dados.

Atualização de E-mail (Username):

Autenticação obrigatória: Somente usuários autenticados podem alterar o e-mail;
Conta ativa: A conta do usuário deve estar ativa. Caso contrário, a operação é bloqueada;
E-mail diferente: O novo e-mail deve ser diferente do e-mail atual;
E-mail único: O novo e-mail não pode estar cadastrado para outro usuário no sistema;
Atualização do username: O campo username (usado como identificador de login) é atualizado com o novo e-mail.

Registro de Novo Usuário:

E-mail único: O e-mail (username) fornecido no cadastro não pode estar em uso por outro usuário;
Perfil define permissão: O perfil do usuário (ADMIN, ATENDENTE, SUPERVISOR, CLIENTE) determina a autoridade (role) atribuída;
Autoridade obrigatória: A autoridade correspondente ao perfil deve existir no banco de dados; caso contrário, o registro falha;
Conta ativa por padrão: Todo novo usuário é criado com isActive = true;
Senha criptografada: A senha fornecida no cadastro é criptografada antes de ser persistida;
Data de criação: A data de criação (createdAt) é definida automaticamente como a data atual (LocalDate.now()).

Validações Gerais de Segurança e Acesso:

Usuário autenticado obrigatório: Operações de atualização (senha/e-mail) exigem que o usuário esteja autenticado via Spring Security;
Recuperação segura do usuário logado: O usuário logado é obtido a partir do SecurityContext e validado contra o banco de dados;


**UserService**

Listagem de Todos os Usuários:

Acesso restrito a administradores: Somente usuários com a role ADMIN podem listar todos os usuários do sistema;
Paginação obrigatória: A listagem é sempre paginada, respeitando os parâmetros de Pageable (página, tamanho, ordenação).

Consulta de Usuário por ID:

Acesso permitido a administradores e atendentes: Usuários com roles ADMIN ou ATENDENTE podem visualizar detalhes de qualquer usuário;
Tratamento de usuário inexistente: Se o ID informado não corresponder a nenhum usuário, lança-se uma exceção ResourceNotFoundException.

Exclusão de Usuário:

Acesso exclusivo a administradores: Apenas usuários com a role ADMIN podem excluir outros usuários;
Usuário deve estar inativo para ser excluído: Não é permitido excluir um usuário cujo status seja ativo (isActive = true);
Tratamento de usuário inexistente: Se o ID não for encontrado, lança-se ResourceNotFoundException.



**TicketService**

Listagem de Todos os Tickets:

Acesso restrito a administradores e supervisores: Somente usuários com as roles ADMIN ou SUPERVISOR podem listar todos os tickets do sistema;
Paginação obrigatória: A listagem é sempre paginada, respeitando os parâmetros de Pageable (página, tamanho, ordenação).

Consulta de Ticket por ID:

Acesso restrito a administradores, supervisores e atendentes: Somente usuários com as roles ADMIN, SUPERVISOR ou ATENDENTE podem consultar um ticket por ID;
Tratamento de ticket inexistente: Se o ID informado não corresponder a nenhum ticket, lança-se uma exceção ResourceNotFoundException.

Criação de Ticket:

Acesso permitido a clientes e atendentes: Usuários com roles CLIENTE ou ATENDENTE podem criar novos tickets;
Status inicial fixo: Todo ticket criado inicia com o status ABERTO;
Data de abertura automática: O campo openedAt é preenchido com a data e hora atuais no momento da criação;
Atribuição automática de agente: O sistema seleciona um agente com role ATENDENTE que tenha o menor número de tickets abertos;
Validação de disponibilidade de agentes: A criação falha com BusinessException se não houver agentes disponíveis.

Atualização de Status do Ticket:
   
Acesso permitido a atendentes e supervisores: Somente usuários com roles ATENDENTE ou SUPERVISOR podem atualizar o status de um ticket;
Proibição em tickets fechados: Não é permitido alterar o status de um ticket cujo status atual seja FECHADO;
Registro automático de data de fechamento: Se o novo status for FECHADO, o campo closedAt é definido com a data e hora atuais.

Alteração de Prioridade do Ticket:

Acesso permitido a atendentes e supervisores: Somente usuários com roles ATENDENTE ou SUPERVISOR podem alterar a prioridade de um ticket;
Proibição em tickets fechados: Não é permitido modificar a prioridade de um ticket com status FECHADO.

Exclusão de Ticket:

Acesso exclusivo a administradores: Apenas usuários com a role ADMIN podem excluir tickets;
Exclusão permanente: O ticket é removido fisicamente do banco de dados;
Tratamento de ticket inexistente: Se o ID não for encontrado, lança-se uma exceção ResourceNotFoundException.



**CustomerService**

Listagem de Clientes: 

Acesso restrito: Apenas usuários com roles ADMIN ou SUPERVISOR podem listar clientes;
Paginação suportada: A listagem retorna os resultados em páginas, conforme parâmetros do Pageable.

Consulta de Cliente por ID:

Acesso restrito: Permitido para usuários com roles ADMIN, SUPERVISOR ou ATENDENTE;
Validação de existência: Caso o cliente não seja encontrado pelo id, é lançada uma ResourceNotFoundException.

Criação de Cliente:

Validação de e-mail único: O cadastro falha com BusinessException se já existir um usuário com o mesmo e-mail;
Ativação automática: O cliente é criado com o campo isActive = true;
Senha protegida: A senha recebida no DTO é criptografada com PasswordEncoder antes de ser persistida;
Data de criação automática: O campo createdAt é preenchido com a data atual (LocalDate.now());
Persistência de cliente e usuário associado: O cliente é salvo na base de dados. Em seguida, é criado um User associado ao cliente.

Exclusão de Cliente:

Acesso restrito: Apenas usuários com role ADMIN podem excluir clientes;
Validação de existência: Caso o cliente não seja encontrado, é lançada uma ResourceNotFoundException.



**SupportAgentService**

Listagem de Atendentes:

Acesso restrito à listagem: Somente usuários com perfil ADMIN ou SUPERVISOR podem listar todos os atendentes;
Retorno completo sem paginação: A listagem retorna todos os atendentes cadastrados (sem paginação).

Consulta de Atendente por ID:

Acesso permitido a múltiplos perfis: Usuários com perfil ADMIN, SUPERVISOR ou ATENDENTE podem consultar um atendente pelo ID;
Atendente inexistente gera exceção: Se o atendente com o ID informado não existir, deve ser lançada uma ResourceNotFoundException.

Criação de Atendente:

Validação de e-mail único: O cadastro falha com BusinessException se já existir um usuário com o mesmo e-mail;
Status inicial definido como OFFLINE: Todo novo atendente é criado com o status AgentStatus.OFFLINE;
Ativação automática: O atendente é criado com o campo isActive = true;
Senha protegida: A senha recebida no DTO é criptografada com PasswordEncoder antes de ser persistida;
Data de criação automática: O campo createdAt é preenchido com a data atual (LocalDate.now());
Persistência de atendente e usuário associado: Após salvar o atendente, um usuário do tipo "ATENDENTE" é criado e persistido na base, vinculado ao atendente.

Atualização de Status do Atendente:

Acesso restrito à atualização de status: Apenas usuários com perfil ATENDENTE podem atualizar seu próprio status;
Atendente inexistente gera exceção: Se o atendente com o ID informado não existir, deve ser lançada uma ResourceNotFoundException;
Impedimento de atualização para atendentes inativos: Não é permitido alterar o status de um atendente cujo campo isActive seja false; Impedimento de atualização para status idêntico: Se o novo status for igual ao status atual do atendente, a operação é rejeitada com BusinessException;
Atualização atômica do status: O status do atendente é atualizado e persistido na base de dados dentro de uma transação.

Exclusão de Atendente:

Acesso restrito à exclusão: Somente usuários com perfil ADMIN podem excluir um atendente;
Atendente inexistente gera exceção: Se o atendente com o ID informado não existir, deve ser lançada uma ResourceNotFoundException;
Impedimento de exclusão de atendente ativo: Não é permitido excluir um atendente cujo campo isActive seja true. Nesse caso, lança-se BusinessException.

## Diagrama de fluxo

```mermaid
flowchart TD
    %% Usuario e Autenticacao
    A[Cliente/Usuario] -->|Login| B["Autenticacao - AuthService"]
    B -->|Credenciais validas| C["JWT Token"]
    B -->|Invalido| X["Erro 401 - Nao Autorizado"]

    %% Operacoes no Sistema
    C --> D["Operacoes no Sistema"]

    %% Tickets
    D -->|Criar Ticket| E["TicketService"]
    D -->|Atualizar Ticket| E
    D -->|Visualizar Ticket| E

    %% Agentes
    D -->|Gerenciar Agente| F["SupportAgentService"]

    %% Clientes
    D -->|Visualizar Cliente| G["CustomerService"]

    %% Persistencia
    E -->|Salvar Ticket| H["PostgreSQL"]
    F -->|Salvar Agente| H
    G -->|Salvar Cliente| H

    %% Historico de Tickets
    E -->|Registrar Evento| I["TicketHistoryService"]
    I -->|Mensagem JSON| J["Ticket History Queue"]
    J -->|Processamento| K["TicketHistoryService Consumer"]
    K -->|Persistir Evento| H

    %% Notificacoes
    E -->|Enviar Notificacao| L["NotificationService"]
    L -->|Mensagem JSON| M["Notification Queue"]
    M -->|Processamento| N["NotificationService Consumer"]
    N -->|Persistir Notificacao| H

    %% DLQs
    J -->|Falha no processamento| O["Ticket History DLQ"]
    M -->|Falha no processamento| P["Notification DLQ"]

    %% Estilo para destacar elementos
    style X fill:#f77,stroke:#333,stroke-width:2px
    style C fill:#7f7,stroke:#333,stroke-width:2px
    style H fill:#7af,stroke:#333,stroke-width:2px
    style O fill:#faa,stroke:#333,stroke-width:2px
    style P fill:#faa,stroke:#333,stroke-width:2px
