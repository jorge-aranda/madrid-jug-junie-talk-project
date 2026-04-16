package com.bank.api.tasks.domain.model

import java.time.Instant
import java.util.UUID

/**
 * ObjectMother for [TaskGroup] domain entity.
 */
object TaskGroupMother {

    fun random(
        id: String = UUID.randomUUID().toString(),
        userId: String = UUID.randomUUID().toString(),
        name: String = "Group name",
        description: String = "Group description",
        taskIds: List<String> = emptyList(),
        archived: Boolean = false,
        createdAt: Instant = Instant.parse("2025-01-01T00:00:00Z"),
        updatedAt: Instant = Instant.parse("2025-01-01T00:00:00Z")
    ): TaskGroup = TaskGroup(
        id = id,
        userId = userId,
        name = name,
        description = description,
        taskIds = taskIds,
        archived = archived,
        createdAt = createdAt,
        updatedAt = updatedAt
    )

    fun withTasks(
        id: String = UUID.randomUUID().toString(),
        userId: String = UUID.randomUUID().toString(),
        taskIds: List<String> = emptyList()
    ): TaskGroup = random(id = id, userId = userId, taskIds = taskIds)

    fun archived(
        id: String = UUID.randomUUID().toString(),
        userId: String = UUID.randomUUID().toString()
    ): TaskGroup = random(id = id, userId = userId, archived = true)
}
