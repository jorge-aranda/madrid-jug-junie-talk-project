package com.bank.api.tasks.infrastructure.repository

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.repository.MongoRepository
import java.time.Instant

/**
 * MongoDB document representing a persisted task group.
 */
@Document(collection = "task_groups")
data class TaskGroupDocument(
    @Id
    val id: String,
    val userId: String,
    val name: String,
    val description: String = "",
    val taskIds: List<String> = emptyList(),
    val archived: Boolean = false,
    val createdAt: Instant = Instant.now(),
    val updatedAt: Instant = Instant.now()
)

/**
 * Spring Data MongoDB repository for [TaskGroupDocument].
 */
interface TaskGroupRepositoryDbo : MongoRepository<TaskGroupDocument, String> {

    fun findByUserIdAndArchivedFalse(userId: String): List<TaskGroupDocument>
}
