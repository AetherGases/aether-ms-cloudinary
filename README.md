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

Defina na `.env` as variáveis:

- `INTERNAL_AUTHENTICATION_KEY`: chave compartilhada usada pelos demais microsserviços para se autenticar neste serviço.
- `JWT_SECRET`: o mesmo segredo usado pelo `ms-auth` para assinar os tokens JWT.

Entre no VS Code, IntelliJ ou sua IDE de preferência e execute o projeto.

## Autenticação

Este microsserviço não possui conexão com banco de dados; sua única responsabilidade é a integração com o Cloudinary.

Todas as rotas `/api/**` exigem uma das duas formas de autenticação:

1. Header `authentication-key` com o valor configurado em `INTERNAL_AUTHENTICATION_KEY` (comunicação entre serviços):

```http
GET /api/cloudinary/upload-signature?fileType=IMAGE
authentication-key: <INTERNAL_AUTHENTICATION_KEY>
```

2. Quando o header `authentication-key` não é enviado, é obrigatório um access token JWT emitido pelo `ms-auth`:

```http
GET /api/cloudinary/upload-signature?fileType=IMAGE
Authorization: Bearer <ACCESS_TOKEN>
```

O JWT é validado de forma stateless: apenas a assinatura (HMAC256 com `JWT_SECRET`), a expiração e as claims são verificadas, sem nenhuma consulta a banco de dados. Refresh tokens não são aceitos.

Se o header `authentication-key` for enviado, ele tem prioridade e o JWT é ignorado. Requisições sem nenhuma das credenciais, com chave inválida ou com token inválido/expirado retornam `401`.

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