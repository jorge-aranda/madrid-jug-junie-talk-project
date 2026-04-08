# Bank API

A monolithic REST API built with Spring Boot, Kotlin and MongoDB for managing bank accounts and transfers.

## Prerequisites

- **Java 17** (or compatible JDK)
- **MongoDB** running locally on port `27017` (no authentication required by default)
- **Docker** (optional, for containerised execution)

## Running Locally

### 1. Start MongoDB

Make sure MongoDB is running locally:

```bash
mongod --dbpath /tmp/mongodata
```

### 2. Build the project

```bash
./gradlew build
```

### 3. Run the application

```bash
./gradlew bootRun
```

The API will be available at `http://localhost:8080`.

### Swagger UI

Open your browser and navigate to:

```
http://localhost:8080/swagger-ui.html
```

## Docker

### Build the Docker image

```bash
docker build -t bank-api .
```

### Run the container

By default the container expects MongoDB at `localhost:27017`. To point it to a different MongoDB instance, override the environment variables:

```bash
# Using default local MongoDB (host networking so the container can reach localhost)
docker run --rm -p 8080:8080 --network host bank-api

# Using a custom MongoDB
docker run --rm -p 8080:8080 \
  -e SPRING_DATA_MONGODB_HOST=my-mongo-server \
  -e SPRING_DATA_MONGODB_PORT=27017 \
  -e SPRING_DATA_MONGODB_DATABASE=bankdb \
  bank-api
```

> **Note:** The Docker image runs as a non-privileged user (`appuser`) for security.

## Authentication

The API uses **HTTP Basic Authentication** with Spring Security. You must register a user first, then use those credentials for all subsequent requests.

### Register a user (no auth required)

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username": "alice", "password": "secret123"}'
```

## API Endpoints

| Method | Path                    | Auth Required | Description                                  |
|--------|-------------------------|---------------|----------------------------------------------|
| POST   | `/api/auth/register`    | No            | Register a new user                          |
| POST   | `/api/accounts`         | Yes           | Create a new bank account (owned by you)     |
| GET    | `/api/accounts`         | Yes           | List your own accounts                       |
| GET    | `/api/accounts/{id}`    | Yes           | Get your account by ID                       |
| POST   | `/api/accounts/transfer`| Yes           | Transfer funds from your account to any other|

### Example: Create an account

```bash
curl -u alice:secret123 -X POST http://localhost:8080/api/accounts \
  -H "Content-Type: application/json" \
  -d '{"initialBalance": 1000.00}'
```

### Example: List your accounts

```bash
curl -u alice:secret123 http://localhost:8080/api/accounts
```

### Example: Transfer funds

```bash
curl -u alice:secret123 -X POST http://localhost:8080/api/accounts/transfer \
  -H "Content-Type: application/json" \
  -d '{"fromAccountId": "<ID_1>", "toAccountId": "<ID_2>", "amount": 250.00}'
```

## Configuration

The application uses `application.yml` for configuration. All MongoDB settings can be overridden via Spring Boot properties or environment variables:

| Property                         | Env Variable                    | Default     |
|----------------------------------|---------------------------------|-------------|
| `spring.data.mongodb.host`       | `SPRING_DATA_MONGODB_HOST`      | `localhost` |
| `spring.data.mongodb.port`       | `SPRING_DATA_MONGODB_PORT`      | `27017`     |
| `spring.data.mongodb.database`   | `SPRING_DATA_MONGODB_DATABASE`  | `bankdb`    |

For example, to run with a remote MongoDB:

```bash
./gradlew bootRun --args='--spring.data.mongodb.host=remote-server --spring.data.mongodb.port=27018'
```
