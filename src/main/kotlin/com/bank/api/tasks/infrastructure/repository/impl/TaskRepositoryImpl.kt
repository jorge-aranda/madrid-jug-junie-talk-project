package com.bank.api.tasks.infrastructure.repository.impl

import com.bank.api.tasks.domain.model.Task
import com.bank.api.tasks.domain.repository.TaskRepository
import com.bank.api.tasks.infrastructure.repository.TaskDocument
import com.bank.api.tasks.infrastructure.repository.TaskRepositoryDbo
import org.springframework.stereotype.Repository

/**
 * Infrastructure implementation of [TaskRepository] backed by MongoDB.
 */
@Repository
class TaskRepositoryImpl(
    private val taskRepositoryDbo: TaskRepositoryDbo
) : TaskRepository {

    override fun save(task: Task): Task {
        val document = toDocument(task = task)
        val saved = taskRepositoryDbo.save(document)
        return toDomain(document = saved)
    }

    override fun findById(id: String): Task? =
        taskRepositoryDbo.findById(id)
            .map { toDomain(document = it) }
            .orElse(null)

    override fun findByUserIdAndArchivedFalse(
        userId: String
    ): List<Task> =
        taskRepositoryDbo
            .findByUserIdAndArchivedFalse(userId = userId)
            .map { toDomain(document = it) }

    override fun deleteById(id: String) =
        taskRepositoryDbo.deleteById(id)

    private fun toDocument(task: Task) = TaskDocument(
        id = task.id,
        userId = task.userId,
        title = task.title,
        description = task.description,
        completed = task.completed,
        archived = task.archived,
        createdAt = task.createdAt,
        updatedAt = task.updatedAt
    )

    private fun toDomain(document: TaskDocument) = Task(
        id = document.id,
        userId = document.userId,
        title = document.title,
        description = document.description,
        completed = document.completed,
        archived = document.archived,
        createdAt = document.createdAt,
        updatedAt = document.updatedAt
    )
}
