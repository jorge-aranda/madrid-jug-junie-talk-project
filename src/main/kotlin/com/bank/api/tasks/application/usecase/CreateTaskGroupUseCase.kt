package com.bank.api.tasks.application.usecase

import com.bank.api.tasks.application.model.TaskGroupRequest
import com.bank.api.tasks.domain.model.TaskGroup
import com.bank.api.tasks.domain.service.TaskGroupService
import org.springframework.stereotype.Component
import java.util.UUID

/**
 * Use case: the user creates a task group.
 */
@Component
class CreateTaskGroupUseCase(
    private val taskGroupService: TaskGroupService
) {

    fun execute(request: TaskGroupRequest): TaskGroup {
        val taskGroup = TaskGroup(
            id = UUID.randomUUID().toString(),
            userId = request.userId,
            name = request.name,
            description = request.description
        )
        return taskGroupService.create(taskGroup = taskGroup)
    }
}
