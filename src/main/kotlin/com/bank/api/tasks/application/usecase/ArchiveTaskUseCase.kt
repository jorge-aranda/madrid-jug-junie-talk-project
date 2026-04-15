package com.bank.api.tasks.application.usecase

import com.bank.api.tasks.domain.model.Task
import com.bank.api.tasks.domain.service.TaskService
import org.springframework.stereotype.Component

/**
 * Use case: the user archives (soft-deletes) one of their tasks.
 */
@Component
class ArchiveTaskUseCase(
    private val taskService: TaskService
) {

    fun execute(taskId: String, userId: String): Task {
        val task = taskService.findById(id = taskId)
            ?: throw NoSuchElementException(
                "Task not found: $taskId"
            )
        require(task.userId == userId) {
            "Task does not belong to user: $userId"
        }
        return taskService.archive(task = task)
    }
}
