package com.bank.api.tasks.application.usecase

import com.bank.api.tasks.domain.model.Task
import com.bank.api.tasks.domain.service.TaskService
import org.springframework.stereotype.Component

/**
 * Use case: the user lists their non-archived tasks.
 */
@Component
class ListUserTasksUseCase(
    private val taskService: TaskService
) {

    fun execute(userId: String): List<Task> =
        taskService.findByUser(userId = userId)
}
