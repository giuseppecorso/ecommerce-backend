## Why this project

I spent six years working on the operations side of an e-commerce
company — catalog, orders, customers, after-sales. This project
rebuilds that same domain from the backend side, as part of my
transition into backend development. It's a learning project, built
one feature at a time: the roadmap reflects what's next, not what's
already done.

# E-commerce Backend API

REST API for an e-commerce platform, built as a structured
learning project. Currently in active development.

## Tech Stack

- Java 21
- Spring Boot
- Spring Data JPA / Hibernate
- PostgreSQL
- Maven

## API Endpoints

| Method | Path               | Description           | Status  |
|--------|--------------------|-----------------------|---------|
| GET    | /api/products      | Get all products      | 200     |
| GET    | /api/products/{id} | Get a product by id   | 200/404 |
| POST   | /api/products      | Create a new product  | 201     |
| PUT    | /api/products/{id} | Update a product by id| 200/404 |
| DELETE | /api/products/{id} | Delete a product by id| 204/404 |
| GET    | /api/orders               | Get all orders        | 200     |
| GET    | /api/orders/{id}          | Get an order by id    | 200/404 |
| POST   | /api/orders               | Create a new order    | 201/400/404 |
| PATCH  | /api/orders/{id}/status   | Update order status   | 200/404/400 |
| POST   | /api/customers            | Create a new customer | 201/400 |
| GET    | /api/customers/{id}       | Get a customer by id  | 200/404 |

## API Documentation

Interactive API documentation is available at
`http://localhost:8080/swagger-ui.html` when the application is running.

![Swagger UI](docs/swagger-ui.png)

## Getting Started

### Prerequisites

Required only when running without Docker:

- JDK 21
- PostgreSQL

### Setup

1. Clone the repository:

   git clone https://github.com/giuseppecorso/ecommerce-backend.git

2. Create the database (from psql):

   CREATE DATABASE ecommerce;

3. Set the environment variable DB_PASSWORD with your PostgreSQL password.

4. Start the application:

   ./mvnw spring-boot:run

   (on Windows: mvnw.cmd spring-boot:run)

The API runs on http://localhost:8080

## Running with Docker

The only prerequisites are Docker and Docker Compose. No JDK, Maven or
PostgreSQL installation is required: the application is built and run
inside containers.

    docker compose up --build

This starts two containers: a PostgreSQL 18 database and the application
itself. The API is available at `http://localhost:8080` and the Swagger UI
at `http://localhost:8080/swagger-ui.html`. Database data is stored in a
named volume, so it survives container restarts. To stop everything:

    docker compose down

### Notes on the setup

The image is built in two stages. The first stage uses a Maven image to
compile the project and produce the jar; the second stage starts from a
JRE-only image and copies just the jar from the first one. Maven and the
source code are left behind, which keeps the final image small.

Inside the Compose network, containers reach each other by service name,
so the application connects to `db:5432` rather than `localhost`. Inside a
container, `localhost` means the container itself. The value is passed as
the `SPRING_DATASOURCE_URL` environment variable, which overrides the one
in `application.properties`: the same artifact runs unchanged both in
Docker and from the IDE against a local PostgreSQL.

The database container declares a healthcheck, and the application waits
for it. Without it the application starts while PostgreSQL is still
initializing and fails to obtain a connection.

## Example Requests

### Create a product

    curl -X POST http://localhost:8080/api/products -H "Content-Type: application/json" -d "{\"name\": \"Felpa Adidas\", \"description\": \"Felpa con cappuccio nera\", \"price\": 39.99, \"stockQuantity\": 5}"

Response — 201 Created:

    {"id":3,"name":"Felpa Adidas","description":"Felpa con cappuccio nera","price":39.99,"stockQuantity":5}

### Update a product

    curl -X PUT http://localhost:8080/api/products/3 -H "Content-Type: application/json" -d "{\"name\": \"Felpa Adidas\", \"description\": \"Felpa con cappuccio blu\", \"price\": 34.99, \"stockQuantity\": 8}"

Response — 200 OK:

    {"id":3,"name":"Felpa Adidas","description":"Felpa con cappuccio blu","price":34.99,"stockQuantity":8}

The id comes from the URL, not from the request body.

### Invalid input

    curl -X POST http://localhost:8080/api/products -H "Content-Type: application/json" -d "{\"name\": \"\", \"description\": \"test\", \"price\": -5, \"stockQuantity\": -1}"

Response — 400 Bad Request:

    {"price":"must be greater than 0","name":"must not be blank","stockQuantity":"must be greater than or equal to 0"}

Validation runs before the controller method is invoked. Field errors are
collected by a @RestControllerAdvice handler and returned as a
field-to-message map.

## Request and response models

The API does not expose the JPA entity. Incoming JSON is bound to a
ProductRequest, which carries the validation constraints and has no id
field: an id sent in the request body has nowhere to be mapped and is
discarded. Outgoing JSON is built from a ProductResponse, so changes to
the persistence model do not silently change the public API.

## Testing

    ./mvnw test

The project is tested at two levels.

**Unit tests — `OrderServiceTest`**
Pure unit test of `OrderService`, running on plain JUnit 5 and Mockito
with no Spring context. The three repositories are mocks, so nothing
touches the database. It verifies the domain rule on order status: an
unknown value is rejected, a valid one is persisted and returned.

**Web layer slice tests — `CustomerControllerTest`**
Slice test using `@WebMvcTest`, which loads only the target controller
plus JSON serialization, validation and exception handling — no
repositories, no database, no running server. The service is mocked. It
asserts the HTTP status code and the JSON body for a valid request, and
for an invalid one it asserts the 400 response and verifies that the
service was never called.

**Why two levels.** The two levels answer different questions. The
service test tells me whether the domain rule is correct, regardless of
how it is exposed; the controller test tells me whether the HTTP
contract is correct, regardless of whether the logic behind it is right.
Breaking the status rule turns the service test red and leaves the
controller test green; changing a response code does the opposite. A
single test covering both would still tell me that something is broken,
but not where.

## Roadmap
- Payment module
- Authentication and authorization (JWT)

## NOTE
PUT and DELETE are not available for orders. An order is a historical
record: once placed, it must remain traceable. The only permitted change
is its status (NEW, PAID, SHIPPED), exposed through a dedicated PATCH
endpoint. Cancelling an order is a status change, not a deletion.

Valid statuses are defined by an OrderStatus enum, not by free-form
strings. The check lives in the service layer rather than in the request
DTO: it is a domain rule, so every path that changes an order status
goes through it, not just HTTP requests. An invalid value is rejected
with 400 and a message listing the accepted values.