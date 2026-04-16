package com.bank.api.tasks.domain.repository

import com.bank.api.tasks.domain.model.TaskGroup

/**
 * Domain repository interface for [TaskGroup] persistence.
 * Infrastructure layer must provide the implementation.
 */
interface TaskGroupRepository {

    fun save(taskGroup: TaskGroup): TaskGroup

    fun findById(id: String): TaskGroup?

    fun findByUserIdAndArchivedFalse(userId: String): List<TaskGroup>

    fun deleteById(id: String)
}
