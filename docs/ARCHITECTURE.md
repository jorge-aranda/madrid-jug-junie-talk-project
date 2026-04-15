# Architecture

This document describes the architecture patterns used in the Bank API project.

---

## Overview

The project contains two architectural styles that coexist in the same codebase:

1. **Legacy layered architecture** — for the original bank domain (accounts, users, transfers).
2. **Hexagonal architecture** — for all new domains (starting with `tasks`).

Both share the same Spring Boot application entry point (`com.bank.api.BankApiApplication`) and
the same MongoDB instance.

---

## Legacy Architecture

The legacy code follows a traditional **Spring Boot layered pattern**:

```
Controller → Service → Repository → MongoDB
```

### Package layout (`com.bank.api`)

| Package | Responsibility |
|---|---|
| `controller` | REST controllers (`@RestController`) |
| `model` | Entities / documents (`@Document`) and request DTOs |
| `repository` | Spring Data MongoDB repositories |
| `service` | Business logic (`@Service`) |
| `config` | Spring configuration (Security, etc.) |

### Key characteristics

- Entities are annotated with `@Document` and use MongoDB auto-generated IDs.
- Repositories extend `MongoRepository` directly.
- Services contain all business logic and are injected into controllers.
- No explicit separation between domain and infrastructure concerns.

---

## Hexagonal Architecture (New Domains)

All new domains **must** follow hexagonal architecture with four clearly separated layers.

### Package layout (`com.bank.api.<domain>`)

```
com.bank.api.<domain>
├── api
│   ├── controller      # REST controllers
│   └── model           # DTOs (request/response)
├── application
│   ├── usecase          # Application use cases
│   └── model            # Application-level models
├── domain
│   ├── model            # Domain entities
│   ├── service          # Domain services
│   └── repository       # Domain repository interfaces (ports)
└── infrastructure
    └── repository
        ├── <Dbo>        # Spring Data MongoDB repositories + documents
        └── impl         # Domain repository implementations (adapters)
```

### Layer Responsibilities

#### Domain Layer (`domain`)

The **core** of the bounded context. Contains:

- **Models** (`domain.model`): Pure domain entities with business attributes. Use `UUIDv4` for
  identifiers. No framework annotations.
- **Repository interfaces** (`domain.repository`): Ports that define persistence operations.
  These are **interfaces only** — no implementation here.
- **Services** (`domain.service`): Domain business logic. Annotated with `@Service`. Depend only
  on domain repository interfaces.

> ⚠️ The domain layer **must not** import anything from `application`, `api` or `infrastructure`.

#### Application Layer (`application`)

Orchestrates use cases by coordinating domain services. Contains:

- **Use cases** (`application.usecase`): One class per use case, annotated with `@Component`.
  Each use case has a single public `execute(...)` method.
- **Models** (`application.model`): Application-level request/command objects that are
  independent of the API layer DTOs.

> ⚠️ The application layer **must not** import anything from `api` or `infrastructure`.

#### API Layer (`api`)

The inbound adapter — exposes REST endpoints. Contains:

- **Controllers** (`api.controller`): `@RestController` classes that receive HTTP requests,
  convert DTOs to application models, invoke use cases, and return response DTOs.
- **DTOs** (`api.model`): Request and response data transfer objects. Decoupled from domain
  models.

> The API layer depends on `application` (use cases). Exceptionally, for simple CRUDs, it may
> access `domain.service` or `domain.repository` directly.

#### Infrastructure Layer (`infrastructure`)

The outbound adapter — implements persistence. Contains:

- **Spring Data repositories** (`infrastructure.repository`): Interfaces extending
  `MongoRepository` with their corresponding document classes (e.g. `TaskDocument`).
- **Domain repository implementations** (`infrastructure.repository.impl`): Classes annotated
  with `@Component` that implement the domain repository interface by delegating to the Spring
  Data repository. They handle mapping between domain models and database documents.

> ⚠️ The infrastructure layer **must not** import anything from `application` or `api`.
> It depends only on `domain`.

### Visibility Matrix

| Layer | Domain | Application | API | Infrastructure |
|---|---|---|---|---|
| **Domain** | ✅ self | ❌ | ❌ | ❌ |
| **Application** | ✅ | ✅ self | ❌ | ❌ |
| **API** | ⚠️ limited | ✅ | ✅ self | ❌ |
| **Infrastructure** | ✅ | ❌ | ❌ | ✅ self |

---

## Why New Domains Live Under `com.bank.api`

Spring Boot's `@SpringBootApplication` (in `com.bank.api.BankApiApplication`) triggers component
scanning from `com.bank.api` downward. Placing new domains under `com.bank.api.<domain>` ensures
all beans are detected automatically without modifying the application class or adding explicit
`@ComponentScan` / `@EnableMongoRepositories` annotations.

> This is a pragmatic trade-off: the `api` in the package path is a legacy artifact, not an
> architectural statement. Each domain under `com.bank.api.<domain>` is an independent bounded
> context.

---

## Creating a New Domain

1. Create the package `com.bank.api.<domain>` with the full hexagonal structure shown above.
2. Follow the `tasks` domain as the reference implementation.
3. Add a domain-specific `AGENTS.md` inside the domain's source directory.
4. Register the domain in [docs/DOMAINS.md](DOMAINS.md).
5. Update `README.md` with the new endpoints.
