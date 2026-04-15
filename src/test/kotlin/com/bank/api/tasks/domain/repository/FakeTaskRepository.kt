package com.bank.api.tasks.domain.repository

import com.bank.api.tasks.domain.model.Task

/**
 * In-memory fake implementation of [TaskRepository] for domain-layer unit tests.
 */
class FakeTaskRepository : TaskRepository {

    private val store = mutableMapOf<String, Task>()

    override fun save(task: Task): Task {
        store[task.id] = task
        return task
    }

    override fun findById(id: String): Task? = store[id]

    override fun findByUserIdAndArchivedFalse(userId: String): List<Task> =
        store.values.filter { it.userId == userId && !it.archived }

    override fun deleteById(id: String) {
        store.remove(id)
    }

    fun clear() {
        store.clear()
    }
}
