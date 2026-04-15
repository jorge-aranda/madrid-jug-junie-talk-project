package com.bank.api.tasks.domain.repository

import com.bank.api.tasks.domain.model.Task

/**
 * Domain repository interface for [Task] persistence.
 * Infrastructure layer must provide the implementation.
 */
interface TaskRepository {

    fun save(task: Task): Task

    fun findById(id: String): Task?

    fun findByUserIdAndArchivedFalse(userId: String): List<Task>

    fun deleteById(id: String)
}
