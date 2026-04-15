# Domains

This document lists all bounded contexts (domains) in the project.

---

## Domain Registry

| Domain | Package | Architecture | Description |
|---|---|---|---|
| **Bank (legacy)** | `com.bank.api.*` | Layered | Accounts, users, transfers, authentication |
| **Tasks** | `com.bank.api.tasks.*` | Hexagonal | Task management per user |

---

## Tasks

- **Status:** Active
- **Package:** `com.bank.api.tasks`
- **Architecture:** Hexagonal (reference implementation for new domains)
- **Domain-specific guidelines:** [`src/main/kotlin/com/bank/api/tasks/AGENTS.md`](../src/main/kotlin/com/bank/api/tasks/AGENTS.md)

### Capabilities

| Use Case | HTTP | Endpoint |
|---|---|---|
| Create a task | `PUT` | `/api/tasks` |
| List user tasks | `GET` | `/api/tasks` |
| Get task detail | `GET` | `/api/tasks/{taskId}` |
| Complete a task | `PATCH` | `/api/tasks/{taskId}/complete` |
| Archive a task | `DELETE` | `/api/tasks/{taskId}` |

### Key Decisions

- Uses `UUIDv4` for all identifiers.
- User identification via Spring Security `Principal`.
- Soft-delete (archive) instead of hard delete.
- Idempotent creation via `PUT`.

---

## Adding a New Domain

1. Create the package `com.bank.api.<domain>` following the hexagonal structure defined in
   [docs/ARCHITECTURE.md](ARCHITECTURE.md).
2. Use the `tasks` domain as the reference implementation.
3. Add a domain-specific `AGENTS.md` inside the domain's source directory.
4. Register the new domain in this file.
5. Update `README.md` with the new endpoints.
