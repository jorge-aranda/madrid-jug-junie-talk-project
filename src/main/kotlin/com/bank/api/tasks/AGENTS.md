# Tasks Domain — Agent Guidelines

> This file contains guidelines specific to the **tasks** domain.
> For global project rules, see the root [`AGENTS.md`](../../../../../../AGENTS.md).

---

## Overview

The `tasks` domain is the **reference implementation** for all new domains in this project.
It manages personal tasks for authenticated users.

- **Package:** `com.bank.api.tasks`
- **Architecture:** Hexagonal (ports & adapters)
- **Database:** MongoDB (collections: `tasks`, `task_groups`)

---

## Package Structure

```
com.bank.api.tasks
├── api
│   ├── controller/
│   │   ├── TaskController.kt              # REST endpoints for tasks
│   │   └── TaskGroupController.kt         # REST endpoints for task groups
│   └── model/
│       ├── TaskRequestDto.kt              # Inbound DTO (task)
│       ├── TaskResponseDto.kt             # Outbound DTO (task)
│       ├── TaskGroupRequestDto.kt         # Inbound DTO (task group)
│       └── TaskGroupResponseDto.kt        # Outbound DTO (task group)
├── application
│   ├── usecase/
│   │   ├── CreateTaskUseCase.kt
│   │   ├── ListUserTasksUseCase.kt
│   │   ├── GetTaskDetailUseCase.kt
│   │   ├── CompleteTaskUseCase.kt
│   │   ├── ArchiveTaskUseCase.kt
│   │   ├── CreateTaskGroupUseCase.kt
│   │   ├── ListUserTaskGroupsUseCase.kt
│   │   ├── GetTaskGroupDetailUseCase.kt
│   │   ├── AddTaskToGroupUseCase.kt
│   │   ├── RemoveTaskFromGroupUseCase.kt
│   │   └── ArchiveTaskGroupUseCase.kt
│   └── model/
│       ├── TaskRequest.kt                 # Application-level command (task)
│       └── TaskGroupRequest.kt            # Application-level command (task group)
├── domain
│   ├── model/
│   │   ├── Task.kt                        # Domain entity
│   │   └── TaskGroup.kt                   # Domain entity
│   ├── service/
│   │   ├── TaskService.kt                 # Domain logic (tasks)
│   │   └── TaskGroupService.kt            # Domain logic (task groups)
│   └── repository/
│       ├── TaskRepository.kt              # Port (interface only)
│       └── TaskGroupRepository.kt         # Port (interface only)
└── infrastructure
    └── repository/
        ├── TaskRepositoryDbo.kt            # MongoRepository + TaskDocument
        ├── TaskGroupRepositoryDbo.kt       # MongoRepository + TaskGroupDocument
        └── impl/
            ├── TaskRepositoryImpl.kt       # Adapter (implements TaskRepository)
            └── TaskGroupRepositoryImpl.kt  # Adapter (implements TaskGroupRepository)
```

---

## Domain Rules

- Every task belongs to a single user (identified by `userId` from Spring Security `Principal`).
- Task IDs are `UUIDv4`, generated at creation time.
- Tasks can be **completed** (status change) and **archived** (soft-delete).
- Archived tasks are not returned in list queries.
- Task groups allow users to organize tasks into named collections.
- Every task group belongs to a single user.
- Adding a task already in a group is a no-op (idempotent).
- Archived task groups are not returned in list queries.

---

## API Conventions

| Method | Path | Description |
|---|---|---|
| `PUT` | `/api/tasks` | Create a task (idempotent) |
| `GET` | `/api/tasks` | List current user's tasks |
| `GET` | `/api/tasks/{taskId}` | Get task detail |
| `PATCH` | `/api/tasks/{taskId}/complete` | Mark as completed |
| `DELETE` | `/api/tasks/{taskId}` | Archive (soft-delete) |
| `PUT` | `/api/task-groups` | Create a task group (idempotent) |
| `GET` | `/api/task-groups` | List current user's task groups |
| `GET` | `/api/task-groups/{taskGroupId}` | Get task group detail |
| `PATCH` | `/api/task-groups/{taskGroupId}/tasks/{taskId}` | Add task to group |
| `DELETE` | `/api/task-groups/{taskGroupId}/tasks/{taskId}` | Remove task from group |
| `DELETE` | `/api/task-groups/{taskGroupId}` | Archive (soft-delete) task group |

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
