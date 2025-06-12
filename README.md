# Projeto Full Stack: Site para Gerenciamento de Reservas 🏨 (back-end)

![Java](https://img.shields.io/badge/java-FF5722.svg?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)
![Hibernate](https://img.shields.io/badge/Hibernate-F57F17?style=for-the-badge&logo=Hibernate&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-003B6F?style=for-the-badge&logo=postgresql&logoColor=white)
![PgAdmin](https://img.shields.io/badge/PgAdmin-316192?style=for-the-badge&logo=postgresql&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white)
![SendGrid](https://img.shields.io/badge/SendGrid-00BFFF?style=for-the-badge&logo=maildotru&logoColor=white)
![Spring Security](https://img.shields.io/badge/Spring%20Security-6DB33F?style=for-the-badge&logo=springsecurity&logoColor=white)
![JWT](https://img.shields.io/badge/JWT-000000?style=for-the-badge&logo=jsonwebtokens&logoColor=white)
![OAuth2](https://img.shields.io/badge/OAuth2-F80000?style=for-the-badge&logo=openid&logoColor=white)
![Thymeleaf](https://img.shields.io/badge/Thymeleaf-05a77a?style=for-the-badge&logo=thymeleaf&logoColor=white)
![Flyway](https://img.shields.io/badge/Flyway-CC0202?style=for-the-badge&logo=flyway&logoColor=white)
![GitHub Actions](https://img.shields.io/badge/github%20actions-%232671E5.svg?style=for-the-badge&logo=githubactions&logoColor=white)
![Testcontainers](https://img.shields.io/badge/Testcontainers-%2300BCD4?style=for-the-badge&logo=Docker&logoColor=white)
![JUnit5](https://img.shields.io/badge/JUnit5-%2325A162?style=for-the-badge&logo=JUnit5&logoColor=white)
![JaCoCo](https://img.shields.io/badge/JaCoCo-%238C4C00?style=for-the-badge&logo=Codecov&logoColor=white)
![Oracle](https://img.shields.io/badge/Oracle-F80000?style=for-the-badge&logo=oracle&logoColor=white)

## Modelo de Domínio
![DiagramaClasses](https://github.com/user-attachments/assets/fcad9ab6-08e2-462e-8c6e-d5e1eeb7d6b7)

## O que é o projeto? 🤔

Este repositório é o back-end de um projeto Full Stack para um site de gerenciamento de reservas, que possui as seguintes funcionalidades: Login e Cadastro de Usuários, bem como Recuperação de Senha. Visualização de Quartos em grid com opção de filtro com várias opções, e Visualização de Quarto por Id. Visualização de Hotéis em grid e Visualização de Hotel por Id. Criar uma reserva em um quarto, com 4 formas de pagamento: Dinheiro, Pix, Cartão de Crédito e Boleto, sendo possível imprimir o boleto no momento de criação da reserva em formato PDF, criado com Jasper Reports. O usuário pode alterar seus dados pessoais e sua senha, adicionar e remover cartões de crédito, visualizar suas reservas, bem como adicionar, visualizar, atualizar e remover avaliações.

A parte de Gerenciamento conta com um CRUD de quartos, hotéis, reservas e usuários, e uma tela de dashboard que contém estatísticas sobre reservas, dependendo de quem está acessando a parte de gerenciamento. Caso seja um administrador/gerente, ele tem acesso total as funcionalidades de gerenciamento, podendo visualizar dados de todos os hotéis e quartos e gerar relatórios em formato PDF e Excel para cada domínio, e as estatísticas de seu Dashboard serão as estatísticas gerais de todos os hoteís. Caso seja um funcionário, todos os dados serão relativos ao hotel em que trabalha (quartos e reservas), e ele não poderá gerar relatórios.

O back-end foi feito com Java e Spring Boot, com o banco de dados PostgreSQL instanciado no Azure. A questão de autenticação e autorização foi feita com OAuth2 e JWT, o envio de e-mails com o Sendgrid, a migração SQL com Flyway, Docker para ajudar no desenvolvimento local e para a criação da imagem utilizada em produção, e a geração de relatórios PDF com Jasper Reports e JasperSoft Studio. O projeto conta também com Testes Unitários com Mockito e JUnit 5, e Testes de Integração com MockMVC e Test Containers. O deploy do back-end foi feito em uma Máquina Virtual no Oracle Cloud Infrastructure (OCI), que utiliza uma pipeline de CI/CD em Github Actions para automatizar o deploy e o software Traefik para gerar o certificado SSL gratuito para possibilitar o HTTPS.

## Tecnologias 💻
 
- [Spring Boot](https://spring.io/projects/spring-boot)
- [Spring Data JPA](https://spring.io/projects/spring-data-jpa)
- [Spring Security](https://spring.io/projects/spring-security)
- [PostgreSQL](https://www.postgresql.org/)
- [PgAdmin](https://www.pgadmin.org/)
- [Docker](https://www.docker.com/)
- [SendGrid](https://sendgrid.com/en-us)
- [GithubActions](https://docs.github.com/pt/actions)
- [Bean Validation](https://docs.spring.io/spring-framework/reference/core/validation/beanvalidation.html)
- [Thymeleaf](https://www.thymeleaf.org/)
- [Flyway](https://www.red-gate.com/products/flyway/community/)
- [JasperReports](https://community.jaspersoft.com/)
- [JUnit5](https://junit.org/junit5/)
- [Mockito](https://site.mockito.org/)
- [MockMvc](https://docs.spring.io/spring-framework/reference/testing/spring-mvc-test-framework.html)
- [Jacoco](https://www.eclemma.org/jacoco/)
- [TestContainers](https://testcontainers.com/)


## Como executar 🎉

1.Clonar repositório git:

```text
git clone https://github.com/FernandoCanabarroAhnert/booking-app-backend.git
```

2.Instalar dependências.

```text
mvn clean install
```

3.Executar a aplicação Spring Boot.

### Usando Docker 🐳

- Clonar repositório git
- Construir o projeto:
```
./mvnw clean package
```
- Construir a imagem:
```
./mvnw spring-boot:build-image
```
- Executar o container:
```
docker run --name booking-app-backend -p 8080:8080  -d booking-app-backend:0.0.1-SNAPSHOT
```
## API Endpoints 📚

Para fazer as requisições HTTP abaixo, foi utilizada a ferramenta [Postman](https://www.postman.com/):
- Collection do Postman completa: [PostmanCollection](https://github.com/user-attachments/files/20712365/Booking.API.postman_collection.json)

- Cadastrar Reserva
```
$ http POST http://localhost:8080/api/v1/bookings/self

{
  "roomId": 12,
  "checkIn": "2025-10-10",
  "checkOut": "2025-10-15",
  "guestsQuantity": 2,
  "payment": {
    "paymentType": 2,
    "isOnlinePayment": true,
    "creditCardId": 2,
    "installmentQuantity": 3
  }
}

```

- Visualizar Reservas
```
$ http GET http://localhost:8080/api/v1/bookings


{
    "content": [
        {
            "id": 1,
            "checkIn": "2025-05-02",
            "checkOut": "2025-05-07",
            "guestsQuantity": 1,
            "createdAt": "2025-05-02T09:20:10.886597",
            "totalPrice": 945.00,
            "paymentType": 2,
            "userId": 1,
            "userFullName": "Fernando Canabarro",
            "userCpf": "371.561.860-91",
            "roomId": 1,
            "hotelName": "Hotel Copacabana Palace",
            "finished": false
        },
        {
            "id": 2,
            "checkIn": "2025-05-02",
            "checkOut": "2025-05-07",
            "guestsQuantity": 1,
            "createdAt": "2025-05-02T09:20:25.992723",
            "totalPrice": 1800.00,
            "paymentType": 1,
            "userId": 1,
            "userFullName": "Fernando Canabarro",
            "userCpf": "371.561.860-91",
            "roomId": 2,
            "hotelName": "Hotel Copacabana Palace",
            "finished": false
        }
    ],
    "pageable": {
        "pageNumber": 0,
        "pageSize": 10,
        "sort": {
            "sorted": true,
            "empty": false,
            "unsorted": false
        },
        "offset": 0,
        "paged": true,
        "unpaged": false
    },
    "totalPages": 4,
    "totalElements": 32,
    "last": false,
    "size": 10,
    "number": 0,
    "sort": {
        "sorted": true,
        "empty": false,
        "unsorted": false
    },
    "numberOfElements": 10,
    "first": true,
    "empty": false
}

```

- Visualizar Quartos
```
$ http GET http://localhost:8080/api/v1/rooms

{
    "content": [
        {
            "id": 1,
            "number": "101",
            "floor": 1,
            "type": 1,
            "pricePerNight": 189.00,
            "description": "Quarto standard com cama de casal, ar-condicionado, TV a cabo e banheiro privativo. Ideal para estadias curtas.",
            "capacity": 2,
            "ratingsQuantity": 0,
            "averageRating": 0,
            "hotelName": "Hotel Copacabana Palace",
            "hotelId": 1,
            "cardDisplayImage": {
                "id": 11,
                "base64Image": "..."
            }
        },
        {
            "id": 2,
            "number": "202",
            "floor": 2,
            "type": 3,
            "pricePerNight": 360.00,
            "description": "Suíte espaçosa com área de estar, cama king-size, banheira de hidromassagem e vista para a cidade de Belém.",
            "capacity": 4,
            "ratingsQuantity": 0,
            "averageRating": 0,
            "hotelName": "Hotel Copacabana Palace",
            "hotelId": 1,
            "cardDisplayImage": {
                "id": 12,
                "base64Image": "..."
            }
        }
    ],
    "pageable": {
        "pageNumber": 0,
        "pageSize": 12,
        "sort": {
            "sorted": true,
            "empty": false,
            "unsorted": false
        },
        "offset": 0,
        "paged": true,
        "unpaged": false
    },
    "totalPages": 2,
    "totalElements": 20,
    "last": false,
    "size": 12,
    "number": 0,
    "sort": {
        "sorted": true,
        "empty": false,
        "unsorted": false
    },
    "numberOfElements": 12,
    "first": true,
    "empty": false
}
```