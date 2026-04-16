package com.bank.api.tasks.application.usecase

import com.bank.api.tasks.domain.model.TaskGroup
import com.bank.api.tasks.domain.service.TaskGroupService
import org.springframework.stereotype.Component

/**
 * Use case: the user archives (soft-deletes) one of their task groups.
 */
@Component
class ArchiveTaskGroupUseCase(
    private val taskGroupService: TaskGroupService
) {

    fun execute(taskGroupId: String, userId: String): TaskGroup {
        val taskGroup = taskGroupService.findById(id = taskGroupId)
            ?: throw NoSuchElementException(
                "Task group not found: $taskGroupId"
            )
        require(taskGroup.userId == userId) {
            "Task group does not belong to user: $userId"
        }
        return taskGroupService.archive(taskGroup = taskGroup)
    }
}
