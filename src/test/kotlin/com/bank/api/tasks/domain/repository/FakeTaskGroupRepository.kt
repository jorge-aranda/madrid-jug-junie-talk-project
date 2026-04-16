package com.bank.api.tasks.domain.repository

import com.bank.api.tasks.domain.model.TaskGroup

/**
 * In-memory fake implementation of [TaskGroupRepository] for domain-layer unit tests.
 */
class FakeTaskGroupRepository : TaskGroupRepository {

    private val store = mutableMapOf<String, TaskGroup>()

    override fun save(taskGroup: TaskGroup): TaskGroup {
        store[taskGroup.id] = taskGroup
        return taskGroup
    }

    override fun findById(id: String): TaskGroup? = store[id]

    override fun findByUserIdAndArchivedFalse(userId: String): List<TaskGroup> =
        store.values.filter { it.userId == userId && !it.archived }

    override fun deleteById(id: String) {
        store.remove(id)
    }

    fun clear() {
        store.clear()
    }
}
