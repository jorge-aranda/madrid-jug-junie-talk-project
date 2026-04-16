package com.bank.api.tasks.infrastructure.repository.impl

import com.bank.api.tasks.domain.model.TaskGroup
import com.bank.api.tasks.domain.repository.TaskGroupRepository
import com.bank.api.tasks.infrastructure.repository.TaskGroupDocument
import com.bank.api.tasks.infrastructure.repository.TaskGroupRepositoryDbo
import org.springframework.stereotype.Repository

/**
 * Infrastructure implementation of [TaskGroupRepository] backed by MongoDB.
 */
@Repository
class TaskGroupRepositoryImpl(
    private val taskGroupRepositoryDbo: TaskGroupRepositoryDbo
) : TaskGroupRepository {

    override fun save(taskGroup: TaskGroup): TaskGroup {
        val document = toDocument(taskGroup = taskGroup)
        val saved = taskGroupRepositoryDbo.save(document)
        return toDomain(document = saved)
    }

    override fun findById(id: String): TaskGroup? =
        taskGroupRepositoryDbo.findById(id)
            .map { toDomain(document = it) }
            .orElse(null)

    override fun findByUserIdAndArchivedFalse(
        userId: String
    ): List<TaskGroup> =
        taskGroupRepositoryDbo
            .findByUserIdAndArchivedFalse(userId = userId)
            .map { toDomain(document = it) }

    override fun deleteById(id: String) =
        taskGroupRepositoryDbo.deleteById(id)

    private fun toDocument(taskGroup: TaskGroup) = TaskGroupDocument(
        id = taskGroup.id,
        userId = taskGroup.userId,
        name = taskGroup.name,
        description = taskGroup.description,
        taskIds = taskGroup.taskIds,
        archived = taskGroup.archived,
        createdAt = taskGroup.createdAt,
        updatedAt = taskGroup.updatedAt
    )

    private fun toDomain(document: TaskGroupDocument) = TaskGroup(
        id = document.id,
        userId = document.userId,
        name = document.name,
        description = document.description,
        taskIds = document.taskIds,
        archived = document.archived,
        createdAt = document.createdAt,
        updatedAt = document.updatedAt
    )
}
