package com.bank.api.tasks.application.usecase

import com.bank.api.tasks.application.model.TaskRequest
import com.bank.api.tasks.domain.model.Task
import com.bank.api.tasks.domain.service.TaskService
import org.springframework.stereotype.Component
import java.util.UUID

/**
 * Use case: the user creates a task that belongs to them.
 */
@Component
class CreateTaskUseCase(
    private val taskService: TaskService
) {

    fun execute(request: TaskRequest): Task {
        val task = Task(
            id = UUID.randomUUID().toString(),
            userId = request.userId,
            title = request.title,
            description = request.description
        )
        return taskService.create(task = task)
    }
}
