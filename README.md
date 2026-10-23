# Aether MS Cloudinary

A **Aether MS Cloudinary** é o microsserviço responsável pelos endpoints relacionados a upload e carregamento de imagens e/ou arquivos para o Cloudinary.

## Tecnologias utilizadas

- Java 21
- Spring Boot
- Spring Web MVC
- Spring Security
- Cloudinary
- Maven
- Lombok
- Bean Validation (Jakarta Validation)
- SpringDoc OpenAPI (Swagger)
- Docker e Docker Compose
- JUnit 5
- Mockito

## Arquitetura

O projeto segue uma arquitetura em camadas, separando responsabilidades em módulos como:

- Módulos com funcionalidades que contém:
  - Controllers
  - Services
  - DTOs
- Configurations
- Exceptions
- Validators
- Tests

## Documentação da API

Após iniciar a aplicação, a documentação pode ser acessada em:

```
http://localhost:${API_PORT}/swagger-ui.html
```

ou

```
http://localhost:${API_PORT}/swagger-ui/index.html
```

(dependendo da versão do SpringDoc).

## Executando o projeto

### Pré-requisitos

- Java 21
- Maven 3.9+

### Executando a aplicação

Clone o repositório pelo comado a seguir:

```bash
git clone https://github.com/AetherGases/aether-ms-cloudinary.git
cd core-api
```

Copie a .env.example para a .env
```bash
.env.example -> .env
```

Defina na `.env` a variável `INTERNAL_AUTHENTICATION_KEY`. Ela é a chave compartilhada usada pelo front-end e pelos demais microsserviços para se autenticar neste serviço.

Entre no VS Code, IntelliJ ou sua IDE de preferência e execute o projeto.

## Autenticação

Este microsserviço não possui conexão com banco de dados; sua única responsabilidade é a integração com o Cloudinary.

Todas as rotas `/api/**` exigem o header `authentication-key` com o valor configurado em `INTERNAL_AUTHENTICATION_KEY`:

```http
GET /api/cloudinary/upload-signature?fileType=IMAGE
authentication-key: <INTERNAL_AUTHENTICATION_KEY>
```

Requisições sem o header retornam `401`, assim como requisições com uma chave inválida.

## Perfis

O projeto possui configurações separadas para cada ambiente:

- `dev`
- `staging`
- `prod`

O perfil é definido pela variável e nesse caso representa a de desenvolvimento:

```properties
SPRING_PROFILES_ACTIVE=dev
```

ou pela configuração da IDE.