package com.bank.api.tasks.domain.service

import com.bank.api.tasks.domain.model.TaskGroup
import com.bank.api.tasks.domain.repository.TaskGroupRepository
import org.springframework.stereotype.Service
import java.time.Instant

/**
 * Domain service that encapsulates core business rules for task groups.
 */
@Service
class TaskGroupService(
    private val taskGroupRepository: TaskGroupRepository
) {

    fun create(taskGroup: TaskGroup): TaskGroup =
        taskGroupRepository.save(taskGroup = taskGroup)

    fun findByUser(userId: String): List<TaskGroup> =
        taskGroupRepository.findByUserIdAndArchivedFalse(userId = userId)

    fun findById(id: String): TaskGroup? =
        taskGroupRepository.findById(id = id)

    fun addTask(taskGroup: TaskGroup, taskId: String): TaskGroup {
        if (taskId in taskGroup.taskIds) return taskGroup
        val updated = taskGroup.copy(
            taskIds = taskGroup.taskIds + taskId,
            updatedAt = Instant.now()
        )
        return taskGroupRepository.save(taskGroup = updated)
    }

    fun removeTask(taskGroup: TaskGroup, taskId: String): TaskGroup {
        val updated = taskGroup.copy(
            taskIds = taskGroup.taskIds - taskId,
            updatedAt = Instant.now()
        )
        return taskGroupRepository.save(taskGroup = updated)
    }

    fun archive(taskGroup: TaskGroup): TaskGroup {
        val updated = taskGroup.copy(
            archived = true,
            updatedAt = Instant.now()
        )
        return taskGroupRepository.save(taskGroup = updated)
    }
}
