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

**Live demo:** https://ecommerce-backend-bzwc.onrender.com/swagger-ui.html

The demo runs on a free instance that sleeps after 15 minutes without
traffic: the first request may take about a minute while it wakes up.

## Tech Stack

- Java 21
- Spring Boot
- Spring Data JPA / Hibernate
- Spring Security
- PostgreSQL
- Maven
- Docker
- Render (application hosting), Neon (managed PostgreSQL)

## API Endpoints

| Method | Path                       | Description                              | Status          |
|--------|----------------------------|------------------------------------------|-----------------|
| GET    | /api/products              | Get all products                         | 200             |
| GET    | /api/products/{id}         | Get a product by id                      | 200/404         |
| POST   | /api/products              | Create a new product                     | 201             |
| PUT    | /api/products/{id}         | Update a product by id                   | 200/404         |
| DELETE | /api/products/{id}         | Delete a product by id                   | 204/404         |
| GET    | /api/orders                | Get orders: all for ADMIN, own for USER  | 200             |
| GET    | /api/orders/{id}           | Get an order by id                       | 200/404         |
| POST   | /api/orders                | Create a new order                       | 201/400/404/409 |
| PATCH  | /api/orders/{id}/status    | Update order status (ADMIN only)         | 200/400/403/404 |
| POST   | /api/customers             | Create a new customer                    | 201/400         |
| GET    | /api/customers/{id}        | Get a customer by id                     | 200/404         |
| POST   | /api/auth/register         | Register a new user                      | 201/400/409     |

## Authentication

The API uses HTTP Basic authentication, backed by Spring Security.
Users are stored in the `users` table. Passwords are hashed with BCrypt
and are never returned by the API.

| Request                                  | Access                                   |
|------------------------------------------|------------------------------------------|
| GET /api/products, GET /api/products/{id} | Public                                   |
| POST /api/auth/register                  | Public                                   |
| Swagger UI and OpenAPI docs              | Public                                   |
| POST, PUT, DELETE /api/products          | ADMIN only                               |
| PATCH /api/orders/{id}/status            | ADMIN only                               |
| GET /api/orders, GET /api/orders/{id}    | Authenticated: USER sees own orders only |
| All other endpoints                      | Any authenticated user                   |

A request without valid credentials receives 401 Unauthorized. An
authenticated user without the required role receives 403 Forbidden.

### Registration

    curl -X POST http://localhost:8080/api/auth/register -H "Content-Type: application/json" -d "{\"username\": \"mario\", \"password\": \"password123\"}"

Response — 201 Created:

    {"id":3,"username":"mario","role":"USER"}

Registration always assigns the USER role. `RegisterRequest` has no
role field, so a client cannot register itself as ADMIN. The password
must be at least 8 characters long. An existing username returns
409 Conflict.

### Initial users

On startup, `DataInitializer` creates two users if they do not exist
yet: `admin` (role ADMIN) and `user` (role USER). Their passwords are
not in the source code: they are read from the `ADMIN_PASSWORD` and
`USER_PASSWORD` environment variables. If either variable is missing,
the application does not start.

The passwords of the public deployment are not published. To try the
live API, register your own user: it gets the USER role.

### Authenticated request

    curl -u admin:<admin-password> -X DELETE http://localhost:8080/api/products/3

### Order ownership and visibility

The owner of an order is taken from the credentials of the request,
never from the request body: a client cannot create an order in
someone else's name.

A USER sees only their own orders; an ADMIN sees all of them. The
service receives the username and whether the caller is an ADMIN, not
the security objects themselves, so the rule can be unit tested
without Spring Security.

Requesting another user's order by id returns 404, not 403. A 403
would confirm that the order exists, and by trying consecutive ids a
user could work out how many orders the shop has. With 404, an order
that belongs to someone else is indistinguishable from one that does
not exist.

Changing an order status is reserved to ADMIN. A status records a
fact of the process (payment received, parcel shipped), so it cannot
be set by the customer, not even on their own order. This rule depends
only on the role, so it lives in `SecurityConfig` and returns 403 for
any id.

## API Documentation

Interactive API documentation is available at `/swagger-ui.html`,
both locally (`http://localhost:8080/swagger-ui.html`) and on the
live demo. The documentation is public; the endpoints keep their own
access rules.

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

3. Set these environment variables:

   - `DB_PASSWORD` — your local PostgreSQL password
   - `ADMIN_PASSWORD` — password for the initial admin user
   - `USER_PASSWORD` — password for the initial regular user

4. Start the application:

   ./mvnw spring-boot:run

   (on Windows: mvnw.cmd spring-boot:run)

The API runs on http://localhost:8080

## Running with Docker

The only prerequisites are Docker and Docker Compose. No JDK, Maven or
PostgreSQL installation is required: the application is built and run
inside containers.

Compose passes `ADMIN_PASSWORD` and `USER_PASSWORD` from your shell to
the application container, so set both before starting. Then:

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

## Deployment

The live demo runs the same Docker image used locally, on Render, with
the database on Neon.

**Application — Render.** Render builds the image from the repository's
Dockerfile and redeploys automatically on every push to `main`.

**Database — Neon.** A managed PostgreSQL instance on Neon's free plan.
Render also offers managed PostgreSQL, but its free databases expire
after 30 days, which does not suit a portfolio project that has to stay
reachable.

**Configuration.** Nothing environment-specific lives in the code. The
Render service receives these environment variables:

| Variable                     | Purpose                                         |
|------------------------------|-------------------------------------------------|
| `SPRING_DATASOURCE_URL`      | JDBC URL of the Neon database, with `sslmode=require` |
| `SPRING_DATASOURCE_USERNAME` | Database user                                   |
| `SPRING_DATASOURCE_PASSWORD` | Database password                               |
| `ADMIN_PASSWORD`             | Password of the initial admin user              |
| `USER_PASSWORD`              | Password of the initial regular user            |
| `PORT`                       | Port Render routes traffic to (8080)            |

The `SPRING_DATASOURCE_*` variables override `application.properties`,
exactly as in Docker Compose. On the first startup against the empty
Neon database, Hibernate creates the schema and `DataInitializer`
creates the two initial users.

### Try the live API

    curl https://ecommerce-backend-bzwc.onrender.com/api/products

    curl -X POST https://ecommerce-backend-bzwc.onrender.com/api/auth/register -H "Content-Type: application/json" -d "{\"username\": \"yourname\", \"password\": \"password123\"}"

A registered user can read their own orders and customers, but creating a
product returns 403 Forbidden: product changes are reserved to ADMIN.

## Example Requests

### Create a product

    curl -u admin:<admin-password> -X POST http://localhost:8080/api/products -H "Content-Type: application/json" -d "{\"name\": \"Felpa Adidas\", \"description\": \"Felpa con cappuccio nera\", \"price\": 39.99, \"stockQuantity\": 5}"

Response — 201 Created:

    {"id":3,"name":"Felpa Adidas","description":"Felpa con cappuccio nera","price":39.99,"stockQuantity":5}

### Update a product

    curl -u admin:<admin-password> -X PUT http://localhost:8080/api/products/3 -H "Content-Type: application/json" -d "{\"name\": \"Felpa Adidas\", \"description\": \"Felpa con cappuccio blu\", \"price\": 34.99, \"stockQuantity\": 8}"

Response — 200 OK:

    {"id":3,"name":"Felpa Adidas","description":"Felpa con cappuccio blu","price":34.99,"stockQuantity":8}

The id comes from the URL, not from the request body.

### Invalid input

    curl -u admin:<admin-password> -X POST http://localhost:8080/api/products -H "Content-Type: application/json" -d "{\"name\": \"\", \"description\": \"test\", \"price\": -5, \"stockQuantity\": -1}"

Response — 400 Bad Request:

    {"price":"must be greater than 0","name":"must not be blank","stockQuantity":"must be greater than or equal to 0"}

Validation runs before the controller method is invoked. Field errors are
collected by a @RestControllerAdvice handler and returned as a
field-to-message map.

## Order creation and stock

Creating an order decreases the stock of every product it contains.
If a product does not have enough stock, the request is rejected with
409 Conflict:

    {"stock":"Insufficient stock for product 3: requested 50, available 5"}

409 rather than 400: the request is well-formed and passes validation,
but it conflicts with the current state of the product.

`createOrder` runs in a single transaction (`@Transactional`). Creating
an order touches several rows: the order itself, one row per item, and
the stock of each product. Without a transaction every save is
committed immediately, so a failure on the second item left a
half-created order in the database and the stock of the first product
already decreased, while the client received an error. With the
transaction, an exception rolls back every change: the order is saved
completely or not at all.

## Request and response models

The API does not expose the JPA entity. Incoming JSON is bound to a
ProductRequest, which carries the validation constraints and has no id
field: an id sent in the request body has nowhere to be mapped and is
discarded. Outgoing JSON is built from a ProductResponse, so changes to
the persistence model do not silently change the public API.

## Testing

    ./mvnw test

The project is tested at two levels, with no database and no running
server.

**Unit tests — `OrderServiceTest`**
Pure unit tests of `OrderService`, running on plain JUnit 5 and Mockito
with no Spring context. The repositories are mocks, so nothing touches
the database. They cover the domain rules of orders:

- an unknown status is rejected, a valid one is persisted and returned;
- an order with a product that does not exist is not created;
- a USER can read their own order, but not someone else's;
- an order that exceeds the available stock is rejected, and no
  product stock is saved.

**Web layer slice tests — `CustomerControllerTest`, `OrderControllerTest`, `AuthControllerTest`**
Slice tests using `@WebMvcTest`, which loads only the target controller
plus JSON serialization, validation, exception handling and security —
no repositories, no database. Services are mocked. They assert the HTTP
contract:

- a valid customer returns 201 with the expected JSON body; an invalid
  email returns 400 and the service is never called;
- a request without credentials to `/api/orders` returns 401;
- a USER trying to change an order status returns 403, and the service
  is never called;
- registering an existing username returns 409.

**Security in slice tests.** `@WebMvcTest` does not pick up
`SecurityConfig` on its own, so each controller test imports it with
`@Import(SecurityConfig.class)`: the tests run against the real access
rules, not the defaults. The `spring-boot-starter-security-test`
dependency is required for this; without it the security filters are
not applied and a protected endpoint answers as if it were public.
Authenticated users are simulated with `@WithMockUser`, optionally with
a role.

**Why there is no `contextLoads` test.** The default test generated by
Spring Initializr starts the whole application, including the database
connection and `DataInitializer`. It failed without a running
PostgreSQL and the `ADMIN_PASSWORD` variable, so it tested the
environment rather than the code, and it was removed. A full
integration test against a disposable database is a possible next
step.

**Why two levels.** The two levels answer different questions. The
service tests tell me whether a domain rule is correct, regardless of
how it is exposed; the controller tests tell me whether the HTTP
contract is correct, regardless of whether the logic behind it is
right. Breaking the stock rule turns a service test red and leaves the
controller tests green; changing a response code does the opposite. A
single test covering both would still tell me that something is
broken, but not where.

## NOTE

PUT and DELETE are not available for orders. An order is a historical
record: once placed, it must remain traceable. The only permitted change
is its status (NEW, PAID, SHIPPED), exposed through a dedicated PATCH
endpoint reserved to ADMIN. Cancelling an order is a status change, 
not a deletion.

Valid statuses are defined by an OrderStatus enum, not by free-form
strings. The check lives in the service layer rather than in the request
DTO: it is a domain rule, so every path that changes an order status
goes through it, not just HTTP requests. An invalid value is rejected
with 400 and a message listing the accepted values.