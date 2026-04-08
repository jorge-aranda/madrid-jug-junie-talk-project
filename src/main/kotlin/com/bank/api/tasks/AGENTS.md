# Tasks Domain — Agent Guidelines

> This file contains guidelines specific to the **tasks** domain.
> For global project rules, see the root [`AGENTS.md`](../../../../../../AGENTS.md).

---

## Overview

The `tasks` domain is the **reference implementation** for all new domains in this project.
It manages personal tasks for authenticated users.

- **Package:** `com.bank.api.tasks`
- **Architecture:** Hexagonal (ports & adapters)
- **Database:** MongoDB (collection: `tasks`)

---

## Package Structure

```
com.bank.api.tasks
├── api
│   ├── controller/TaskController.kt        # REST endpoints
│   └── model/
│       ├── TaskRequestDto.kt               # Inbound DTO
│       └── TaskResponseDto.kt              # Outbound DTO
├── application
│   ├── usecase/
│   │   ├── CreateTaskUseCase.kt
│   │   ├── ListUserTasksUseCase.kt
│   │   ├── GetTaskDetailUseCase.kt
│   │   ├── CompleteTaskUseCase.kt
│   │   └── ArchiveTaskUseCase.kt
│   └── model/TaskRequest.kt               # Application-level command
├── domain
│   ├── model/Task.kt                      # Domain entity
│   ├── service/TaskService.kt             # Domain logic
│   └── repository/TaskRepository.kt       # Port (interface only)
└── infrastructure
    └── repository/
        ├── TaskRepositoryDbo.kt            # MongoRepository + TaskDocument
        └── impl/TaskRepositoryImpl.kt      # Adapter (implements TaskRepository)
```

---

## Domain Rules

- Every task belongs to a single user (identified by `userId` from Spring Security `Principal`).
- Task IDs are `UUIDv4`, generated at creation time.
- Tasks can be **completed** (status change) and **archived** (soft-delete).
- Archived tasks are not returned in list queries.

---

## API Conventions

| Method | Path | Description |
|---|---|---|
| `PUT` | `/api/tasks` | Create a task (idempotent) |
| `GET` | `/api/tasks` | List current user's tasks |
| `GET` | `/api/tasks/{taskId}` | Get task detail |
| `PATCH` | `/api/tasks/{taskId}/complete` | Mark as completed |
| `DELETE` | `/api/tasks/{taskId}` | Archive (soft-delete) |

- All endpoints require authentication (HTTP Basic via Spring Security).
- User identification comes from `java.security.Principal` — never from custom headers.

---

## Extending This Domain

When adding new features to the tasks domain:

1. Add a new use case in `application.usecase` with a single `execute(...)` method.
2. If the use case needs new persistence operations, add them to the `TaskRepository` interface
   first, then implement in `TaskRepositoryImpl`.
3. Add or update DTOs in `api.model` as needed.
4. Wire the new use case in `TaskController`.
5. Keep the visibility rules: domain sees nothing external, application sees only domain,
   infrastructure sees only domain, API sees application.
