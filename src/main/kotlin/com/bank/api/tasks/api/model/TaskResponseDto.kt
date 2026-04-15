package com.bank.api.tasks.api.model

import com.bank.api.tasks.domain.model.Task
import java.time.Instant

/**
 * DTO returned to the client representing a task.
 */
data class TaskResponseDto(
    val id: String,
    val userId: String,
    val title: String,
    val description: String,
    val completed: Boolean,
    val archived: Boolean,
    val createdAt: Instant,
    val updatedAt: Instant
) {
    companion object {
        fun from(task: Task) = TaskResponseDto(
            id = task.id,
            userId = task.userId,
            title = task.title,
            description = task.description,
            completed = task.completed,
            archived = task.archived,
            createdAt = task.createdAt,
            updatedAt = task.updatedAt
        )
    }
}
