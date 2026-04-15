# AGENTS.md — AI Agent Guidelines

This document defines the rules, conventions and architecture guidelines that any AI coding agent
(Junie, GitHub Copilot, Claude Code, Codex, etc.) **must** follow when working on this project.

> **Platform-specific files** (`.junie/guidelines.md`, `.github/copilot-instructions.md`,
> `CLAUDE.md`, `codex.md`) all point back to this file as the single source of truth.

---

## Table of Contents

1. [Project Overview](#project-overview)
2. [Legacy vs New Domains](#legacy-vs-new-domains)
3. [Architecture](#architecture)
4. [Coding Rules](#coding-rules)
5. [Git & Commits](#git--commits)
6. [Documentation](#documentation)
7. [Reference Documents](#reference-documents)

---

## Project Overview

- **Language:** Kotlin
- **Framework:** Spring Boot 4 (Spring Security, Spring Data MongoDB)
- **Build tool:** Gradle (Kotlin DSL)
- **Database:** MongoDB
- **Java version:** 17
- **API documentation:** SpringDoc OpenAPI (Swagger UI)

---

## Legacy vs New Domains

The codebase contains two clearly separated areas:

### Legacy (`com.bank.api.*` — excluding `com.bank.api.tasks` and future domains)

The legacy code covers **bank accounts, users, transfers and authentication**.
It follows a traditional Spring Boot layered pattern:

| Layer | Package |
|---|---|
| Controllers | `com.bank.api.controller` |
| Models / Entities | `com.bank.api.model` |
| Repositories | `com.bank.api.repository` |
| Services | `com.bank.api.service` |
| Configuration | `com.bank.api.config` |

**Rules for legacy code:**

- Modifications to accounts, users or transfers **must** follow the existing legacy patterns.
- Do not introduce hexagonal architecture into the legacy code.
- Keep changes consistent with the current style (e.g. entities annotated with `@Document`,
  repositories extending Spring Data interfaces directly, services as `@Service` beans).

### New Domains (`com.bank.api.<domain>.*`)

Every **new** domain must follow the hexagonal architecture pattern established by the `tasks`
domain. New domains live under `com.bank.api.<domain>` so that Spring Boot's default component
scan (rooted at `com.bank.api`) detects all beans automatically.

> **Reference implementation:** `com.bank.api.tasks` — see
> [`src/main/kotlin/com/bank/api/tasks/AGENTS.md`](src/main/kotlin/com/bank/api/tasks/AGENTS.md)
> for domain-specific guidelines.

New domains **must** replicate the same package structure and visibility rules as `tasks`.
See [docs/ARCHITECTURE.md](docs/ARCHITECTURE.md) for full details.

---

## Architecture

Full architecture documentation is available at **[docs/ARCHITECTURE.md](docs/ARCHITECTURE.md)**.

### Hexagonal Architecture (new domains) — Summary

```
api.controller / api.model          ← API layer (REST)
        │
application.usecase / application.model  ← Application layer (use cases)
        │
domain.model / domain.service / domain.repository  ← Domain layer (core)
        │
infrastructure.repository / infrastructure.repository.impl  ← Infrastructure layer
```

### Layer Visibility Rules

| Layer | Can see |
|---|---|
| **Domain** | Nothing else — no `infrastructure`, `application` or `api` |
| **Application** | `domain` only — no `infrastructure` or `api` |
| **API** | `application` (and exceptionally `domain.service` / `domain.repository` for simple CRUDs) |
| **Infrastructure** | `domain` only |

---

## Coding Rules

1. **Language:** Kotlin — always use **named parameters** when possible.
2. **Documentation & code:** Always in **English**.
3. **Max line length:** **120 characters**.
4. **IDs:** Use **UUIDv4** across all layers.
5. **HTTP methods:**
   - `PUT` for creation (idempotent).
   - `PATCH` for partial updates (e.g. completing a task).
   - `DELETE` for archiving / soft-deleting.
   - `GET` for reads.
6. **RESTful design:** Follow REST conventions for URL naming and HTTP semantics.
7. **Authentication:** Use Spring Security's `Principal` (from the authenticated user) — never
   custom headers like `X-User-Id`.
8. **Domain repositories** are **interfaces only** — implementations live in
   `infrastructure.repository.impl`.
9. **Code style:** Follow the existing style in the module/file you are editing.

---

## Git & Commits

- Use **Conventional Commits** format: `type(scope): description`.
  - `scope` should be the **domain** or area affected (e.g. `tasks`, `accounts`, `auth`).
  - Types:
    - `feat`: A new feature.
    - `fix`: A bug fix.
    - `docs`: Documentation only changes.
    - `style`: Changes that do not affect the meaning of the code (white-space, formatting, etc.).
    - `refactor`: A code change that neither fixes a bug nor adds a feature.
    - `perf`: A code change that improves performance.
    - `test`: Adding missing tests or correcting existing tests.
    - `build`: Changes that affect the build system or external dependencies (e.g. Gradle, Docker).
    - `ci`: Changes to CI configuration files and scripts.
    - `chore`: Other changes that don't modify src or test files.
    - `revert`: Reverts a previous commit.
  - Examples:
    ```
    feat(tasks): add archive task use case
    fix(accounts): correct balance calculation on transfer
    docs(agents.md): add AGENTS.md guidelines and architecture documentation
    refactor(tasks): extract validation logic to domain service
    ```
- Every commit made by an AI agent **must** include a co-author trailer:
  ```
  --trailer "Co-authored-by: <AgentName> <<agent-email>>"
  ```
  Examples:
  ```
  --trailer "Co-authored-by: Junie <junie@jetbrains.com>"
  --trailer "Co-authored-by: GitHub Copilot <copilot@github.com>"
  --trailer "Co-authored-by: Claude <claude@anthropic.com>"
  ```
- Use **git flow** branching model (`feature/`, `release/`, `hotfix/`).

---

## Documentation

- Keep `README.md` up to date with any new endpoints or configuration changes.
- Domain-specific documentation goes in each domain's own `AGENTS.md`.
- Global domain registry: [docs/DOMAINS.md](docs/DOMAINS.md).
- Architecture details: [docs/ARCHITECTURE.md](docs/ARCHITECTURE.md).

---

## Reference Documents

| Document | Purpose |
|---|---|
| [`AGENTS.md`](AGENTS.md) | This file — global agent guidelines |
| [`docs/ARCHITECTURE.md`](docs/ARCHITECTURE.md) | Detailed architecture documentation |
| [`docs/DOMAINS.md`](docs/DOMAINS.md) | Domain registry and descriptions |
| [`src/main/kotlin/com/bank/api/tasks/AGENTS.md`](src/main/kotlin/com/bank/api/tasks/AGENTS.md) | Tasks domain-specific guidelines |
