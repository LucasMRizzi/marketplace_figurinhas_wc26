# Marketplace de Figurinhas da Copa do Mundo 2026

Sistema web para negociação de figurinhas da Copa do Mundo FIFA 2026, permitindo que usuários criem anúncios de venda e troca, gerenciem seus álbuns, inventário, listas de desejos e realizem negociações entre si.

O projeto foi desenvolvido utilizando arquitetura cliente-servidor, com backend em **Spring Boot** e frontend em **React**, utilizando **PostgreSQL** como banco de dados e **RabbitMQ** para processamento assíncrono de pagamentos.

---

# Tecnologias

## Backend

- Java 21
- Spring Boot
- Spring Web
- Spring Data JPA
- Spring Security
- Hibernate
- PostgreSQL
- RabbitMQ
- Maven
- Lombok
- Swagger / OpenAPI

## Frontend

- React
- Vite
- React Router

## Banco de dados

- PostgreSQL

## Mensageria

- RabbitMQ

## Containerização

- Docker
- Docker Compose

---

# Funcionalidades

## Usuários

- Cadastro
- Login
- Atualização de perfil
- Avaliações
- Controle de saldo

## Inventário

- Adicionar figurinhas
- Remover figurinhas
- Atualizar quantidade
- Consulta do inventário

## Álbuns

- Criar álbum
- Colar figurinhas
- Remover figurinhas
- Cálculo automático da completude

## Ofertas

- Criar venda
- Criar troca
- Adicionar itens ofertados
- Adicionar itens solicitados
- Expirar ofertas
- Consultar ofertas

## Negociações

- Aceitar oferta
- Pagamento assíncrono
- Concretização automática
- Avaliação após negociação

---

# Arquitetura

```
Frontend (React)
        │
        │ HTTP
        ▼
Backend (Spring Boot)
        │
        ├── PostgreSQL
        │
        └── RabbitMQ
                │
                ▼
      Processamento de Pagamentos
```

---

# Estrutura do projeto

```
marketplace/
│
├── src/
├── docker/
├── pom.xml
├── Dockerfile
│
├── frontend/
│   ├── src/
│   ├── public/
│   ├── package.json
│   └── vite.config.js
│
├── database/
│   ├── 01-types.sql
│   ├── 02-tables.sql
│   ├── 03-triggers.sql
│   └── docs/
│
├── docker-compose.yml
├── .env.example
└── README.md
```

---

# Como executar

## 1) Clonar o projeto

```bash
git clone https://github.com/SEU_USUARIO/marketplace_figurinhas_wc26.git

cd marketplace_figurinhas_wc26
```

---

## 2) Configurar variáveis de ambiente

Copie o arquivo:

```text
.env.example
```

para

```text
.env
```

Exemplo:

```env
POSTGRES_DB=marketplace

POSTGRES_USER=postgres
POSTGRES_PASSWORD=postgres

RABBITMQ_DEFAULT_USER=guest
RABBITMQ_DEFAULT_PASS=guest

SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/marketplace
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=postgres

RABBITMQ_HOST=localhost
RABBITMQ_PORT=5672
```

---

## 3) Subir PostgreSQL e RabbitMQ

```bash
docker compose up -d
```

O Docker irá:

- criar o banco PostgreSQL;
- executar automaticamente os scripts SQL;
- iniciar o RabbitMQ.

---

## 4) Executar o backend

```bash
./mvnw spring-boot:run
```

ou

```bash
mvn spring-boot:run
```

---

## 5) Executar o frontend

```bash
cd frontend

npm install

npm run dev
```

---

# Banco de dados

Na primeira execução, os scripts presentes em

```
database/
```

são executados automaticamente na seguinte ordem:

```
01-types.sql
02-tables.sql
03-triggers.sql
04-inserts.sql
```

---

# Acessos

Frontend

```
http://localhost:5173
```

Backend

```
http://localhost:8081
```

Swagger

```
http://localhost:8081/swagger-ui/index.html
```

RabbitMQ Management

```
http://localhost:15672
```

---

# Modelo de domínio

O sistema possui como principais entidades:

- Usuário
- Figurinha
- Inventário
- Álbum
- Oferta
- Venda
- Troca
- Item Ofertado
- Item Solicitado
- Concretização
- Avaliação

---

# Fluxo de negociação

```text
Usuário

↓

Adiciona figurinhas ao inventário

↓

Cria uma oferta

↓

Outro usuário aceita

↓

Concretização criada

↓

Pagamento enviado ao RabbitMQ

↓

Processamento do pagamento

↓

Oferta finalizada

↓

Usuários podem se avaliar
```

---

# Segurança

A autenticação é realizada utilizando **Spring Security** com sessões HTTP.

As senhas são armazenadas utilizando **BCrypt**.

---

# API

A documentação completa da API está disponível via Swagger.

```
http://localhost:8081/swagger-ui/index.html
```

---

# Autor

Lucas Rizzi

UNESP – Universidade Estadual Paulista

Projeto desenvolvido para a disciplina de Banco de Dados.