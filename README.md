# ChronoFlow - Dashboard de Monitoramento de Jobs

## Visão Geral

ChronoFlow é uma aplicação web moderna projetada para o monitoramento em tempo real de jobs (processos agendados). Ele resolve o problema de falta de visibilidade sobre tarefas executadas em background, fornecendo uma interface clara e que se atualiza automaticamente.

A comunicação entre o frontend (Angular) e o backend (Spring Boot) é feita via **Server-Sent Events (SSE)**, garantindo que o dashboard reflita o estado real dos jobs sem a necessidade de atualizações manuais.

---

## Visão Geral da Arquitetura

O ChronoFlow utiliza uma arquitetura full-stack moderna, com uma clara separação de responsabilidades entre o frontend e o backend.

### Comunicação

A interação entre as duas partes da aplicação é feita através de dois mecanismos principais:

1.  **API REST:** Utilizada para todas as operações de CRUD (Criar, Ler, Atualizar e Deletar) sobre os jobs. O frontend realiza chamadas HTTP para endpoints RESTful expostos pelo backend para gerenciar os agendamentos.
2.  **Server-Sent Events (SSE):** Para a funcionalidade de tempo real, o backend empurra ativamente (`push`) atualizações de status para o frontend a cada 5 segundos. O frontend estabelece uma conexão persistente com o backend e apenas "escuta" por novos eventos (`job-update`), atualizando a interface de forma reativa.

### Tecnologias Utilizadas

#### Frontend

- **Framework:** Angular (~20.3.2) com Componentes Standalone
- **Linguagem:** TypeScript
- **Estilização:** CSS puro com variáveis para suporte a temas (Claro/Escuro)

#### Backend

- **Linguagem:** Java 21
- **Framework Principal:** Spring Boot 3
- **Banco de Dados:** MySQL / PostgreSQL
- **Acesso a Dados:** Spring Data JPA (com Hibernate)
- **Agendamento de Tarefas:** Quartz Scheduler
- **Comunicação:** Spring Web (para REST e SSE)

---

## O que é Gerado e Configurado Automaticamente (Na Primeira Execução)

Ao iniciar pela primeira vez, a aplicação realiza uma série de configurações automáticas para preparar o ambiente, garantindo que tudo funcione sem intervenção manual.

1.  **Criação do Banco de Dados:**
    - O banco de dados com o nome `chrono_flow` é criado no seu servidor MySQL. Isso acontece porque a URL de conexão no `application.properties` contém o parâmetro `?createDatabaseIfNotExist=true`.

2.  **Criação dos Diretórios de Arquivos:**
    - A estrutura de pastas para manipulação de arquivos é criada no disco. O componente `DirectoryInitializer` é executado na inicialização e cria a seguinte estrutura caso ela não exista:
      ```
      C:
      └── ChronoFlow_Files/
          ├── in/
          └── out/
              ├── erros/
              ├── pendentes/
              └── processados/
      ```

3.  **Criação das Tabelas:**
    - **Tabelas da Aplicação:** O Hibernate lê as classes de entidade do projeto (anotadas com `@Entity`) e cria automaticamente todas as tabelas necessárias para a aplicação dentro do banco `chrono_flow`. Isso é controlado pela propriedade `spring.jpa.hibernate.ddl-auto=update`.
    - **Tabelas do Quartz:** O Quartz Scheduler também cria seu próprio conjunto de tabelas (como `QRTZ_JOB_DETAILS`, `QRTZ_TRIGGERS`, etc.) no mesmo banco de dados. Essas tabelas são usadas para gerenciar e persistir o estado dos jobs agendados. Isso é controlado por `spring.quartz.jdbc.initialize-schema=always`.

Em resumo, na primeira execução, a aplicação prepara de forma autônoma todo o ambiente necessário: o banco de dados, as tabelas e a estrutura de diretórios.

---

## 3. Instalação e Configuração

Siga os passos abaixo para configurar e executar o projeto ChronoFlow em seu ambiente local.

### Pré-requisitos

Certifique-se de que você tem as seguintes ferramentas instaladas:

- **Node.js** (versão 18.x ou superior) e **npm**
- **Angular CLI** (`npm install -g @angular/cli`)
- **Java Development Kit (JDK)** (versão 21 ou superior)
- **Maven** (para gerenciamento de dependências do backend)
- **MySQL** ou **PostgreSQL** (servidor de banco de dados ativo)

### Instruções

1.  **Clone o repositório:**
    ```bash
    git clone git@github.com:CaioBahia/Techne-ChronoFlow.git
    ```

2.  **Configure e execute o Backend:**
    - Navegue até a pasta do backend: `cd /caminho/para/o/projeto/backend`
    - Configure o banco de dados no arquivo `src/main/resources/application.properties`, ajustando a URL de conexão, usuário e senha.
    - Instale as dependências e compile o projeto: `mvn clean install`
    - Execute a aplicação: `mvn spring-boot:run`
    - O servidor backend estará rodando em `http://localhost:8080`.

3.  **Configure e execute o Frontend:**
    - Em um novo terminal, navegue até a pasta do frontend: `cd /caminho/para/o/projeto/frontend/chrono_flow`
    - Instale as dependências: `npm install`
    - Execute o servidor de desenvolvimento: `ng serve`
    - A aplicação frontend estará disponível em `http://localhost:4200/`.

4.  **Acesse o ChronoFlow:**
    - Abra seu navegador e acesse `http://localhost:4200/`.
    - O dashboard deverá carregar e começar a receber as atualizações de status dos jobs em tempo real.

---

## 4. Como Rodar o Projeto

Após a instalação e configuração, você pode iniciar a aplicação com os seguintes comandos:

### Backend

Abra o projeto backend em sua IDE (IntelliJ, VS Code, etc.) e execute a classe principal `ChronoFlowApplication.java`. O Maven irá baixar as dependências automaticamente.
<br/>_Alternativamente, via linha de comando na pasta do backend:_
```bash
mvn spring-boot:run
```
O servidor estará rodando em `http://localhost:8080`.

### Frontend

Em um **novo terminal**, na pasta raiz do projeto frontend (`frontend/chrono_flow`), execute:
```bash
# O comando 'ng serve' também pode ser usado
npm start
```
