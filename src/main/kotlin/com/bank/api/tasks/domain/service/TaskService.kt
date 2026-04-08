package com.bank.api.tasks.domain.service

import com.bank.api.tasks.domain.model.Task
import com.bank.api.tasks.domain.repository.TaskRepository
import org.springframework.stereotype.Service
import java.time.Instant

/**
 * Domain service that encapsulates core business rules for tasks.
 */
@Service
class TaskService(
    private val taskRepository: TaskRepository
) {

    fun create(task: Task): Task = taskRepository.save(task)

    fun findByUser(userId: String): List<Task> =
        taskRepository.findByUserIdAndArchivedFalse(userId = userId)

    fun findById(id: String): Task? = taskRepository.findById(id = id)

    fun complete(task: Task): Task {
        val updated = task.copy(
            completed = true,
            updatedAt = Instant.now()
        )
        return taskRepository.save(task = updated)
    }

    fun archive(task: Task): Task {
        val updated = task.copy(
            archived = true,
            updatedAt = Instant.now()
        )
        return taskRepository.save(task = updated)
    }
}
