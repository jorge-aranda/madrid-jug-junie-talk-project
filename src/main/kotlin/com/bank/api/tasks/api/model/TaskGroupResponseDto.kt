package com.bank.api.tasks.api.model

import com.bank.api.tasks.domain.model.TaskGroup
import java.time.Instant

/**
 * DTO returned to the client representing a task group.
 */
data class TaskGroupResponseDto(
    val id: String,
    val userId: String,
    val name: String,
    val description: String,
    val taskIds: List<String>,
    val archived: Boolean,
    val createdAt: Instant,
    val updatedAt: Instant
) {
    companion object {
        fun from(taskGroup: TaskGroup) = TaskGroupResponseDto(
            id = taskGroup.id,
            userId = taskGroup.userId,
            name = taskGroup.name,
            description = taskGroup.description,
            taskIds = taskGroup.taskIds,
            archived = taskGroup.archived,
            createdAt = taskGroup.createdAt,
            updatedAt = taskGroup.updatedAt
        )
    }
}
