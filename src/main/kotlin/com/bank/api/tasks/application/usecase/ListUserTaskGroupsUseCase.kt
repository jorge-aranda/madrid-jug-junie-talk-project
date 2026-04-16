package com.bank.api.tasks.application.usecase

import com.bank.api.tasks.domain.model.TaskGroup
import com.bank.api.tasks.domain.service.TaskGroupService
import org.springframework.stereotype.Component

/**
 * Use case: the user lists their non-archived task groups.
 */
@Component
class ListUserTaskGroupsUseCase(
    private val taskGroupService: TaskGroupService
) {

    fun execute(userId: String): List<TaskGroup> =
        taskGroupService.findByUser(userId = userId)
}
