package com.bank.api.tasks.application.usecase

import com.bank.api.tasks.domain.model.TaskGroupMother
import com.bank.api.tasks.domain.service.TaskGroupService
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class GetTaskGroupDetailUseCaseTest {

    private val taskGroupService: TaskGroupService = mockk()
    private val useCase = GetTaskGroupDetailUseCase(taskGroupService = taskGroupService)

    @Test
    fun `should return task group detail`() {
        // given
        val userId = "user-1"
        val taskGroup = TaskGroupMother.random(userId = userId)
        every { taskGroupService.findById(id = taskGroup.id) } returns taskGroup

        // when
        val result = useCase.execute(taskGroupId = taskGroup.id, userId = userId)

        // then
        assertEquals(taskGroup.id, result.id)
    }

    @Test
    fun `should throw when task group not found`() {
        // given
        every { taskGroupService.findById(id = "missing") } returns null

        // when / then
        assertThrows<NoSuchElementException> {
            useCase.execute(taskGroupId = "missing", userId = "user-1")
        }
    }

    @Test
    fun `should throw when task group does not belong to user`() {
        // given
        val taskGroup = TaskGroupMother.random(userId = "owner")
        every { taskGroupService.findById(id = taskGroup.id) } returns taskGroup

        // when / then
        assertThrows<IllegalArgumentException> {
            useCase.execute(taskGroupId = taskGroup.id, userId = "other-user")
        }
    }
}
