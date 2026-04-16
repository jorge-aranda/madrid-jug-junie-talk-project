package com.bank.api.tasks.application.usecase

import com.bank.api.tasks.domain.model.TaskGroupMother
import com.bank.api.tasks.domain.service.TaskGroupService
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class AddTaskToGroupUseCaseTest {

    private val taskGroupService: TaskGroupService = mockk()
    private val useCase = AddTaskToGroupUseCase(taskGroupService = taskGroupService)

    @Test
    fun `should add a task to a group owned by the user`() {
        // given
        val userId = "user-1"
        val taskGroup = TaskGroupMother.random(userId = userId)
        val taskId = "task-1"
        every { taskGroupService.findById(id = taskGroup.id) } returns taskGroup
        every {
            taskGroupService.addTask(taskGroup = taskGroup, taskId = taskId)
        } returns taskGroup.copy(taskIds = listOf(taskId))

        // when
        val result = useCase.execute(taskGroupId = taskGroup.id, taskId = taskId, userId = userId)

        // then
        assertTrue(result.taskIds.contains(taskId))
    }

    @Test
    fun `should throw when task group not found`() {
        // given
        every { taskGroupService.findById(id = "missing") } returns null

        // when / then
        assertThrows<NoSuchElementException> {
            useCase.execute(taskGroupId = "missing", taskId = "task-1", userId = "user-1")
        }
    }

    @Test
    fun `should throw when task group does not belong to user`() {
        // given
        val taskGroup = TaskGroupMother.random(userId = "owner")
        every { taskGroupService.findById(id = taskGroup.id) } returns taskGroup

        // when / then
        assertThrows<IllegalArgumentException> {
            useCase.execute(taskGroupId = taskGroup.id, taskId = "task-1", userId = "other-user")
        }
    }
}
