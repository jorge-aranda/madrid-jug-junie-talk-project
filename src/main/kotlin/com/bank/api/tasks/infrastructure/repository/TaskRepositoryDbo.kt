package com.bank.api.tasks.infrastructure.repository

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.repository.MongoRepository
import java.time.Instant

/**
 * MongoDB document representing a persisted task.
 */
@Document(collection = "tasks")
data class TaskDocument(
    @Id
    val id: String,
    val userId: String,
    val title: String,
    val description: String = "",
    val completed: Boolean = false,
    val archived: Boolean = false,
    val createdAt: Instant = Instant.now(),
    val updatedAt: Instant = Instant.now()
)

/**
 * Spring Data MongoDB repository for [TaskDocument].
 */
interface TaskRepositoryDbo : MongoRepository<TaskDocument, String> {

    fun findByUserIdAndArchivedFalse(userId: String): List<TaskDocument>
}
