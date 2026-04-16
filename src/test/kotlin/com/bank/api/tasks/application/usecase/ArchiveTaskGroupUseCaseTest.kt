package com.bank.api.tasks.application.usecase

import com.bank.api.tasks.domain.model.TaskGroupMother
import com.bank.api.tasks.domain.service.TaskGroupService
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class ArchiveTaskGroupUseCaseTest {

    private val taskGroupService: TaskGroupService = mockk()
    private val useCase = ArchiveTaskGroupUseCase(taskGroupService = taskGroupService)

    @Test
    fun `should archive a task group owned by the user`() {
        // given
        val userId = "user-1"
        val taskGroup = TaskGroupMother.random(userId = userId)
        every { taskGroupService.findById(id = taskGroup.id) } returns taskGroup
        every { taskGroupService.archive(taskGroup = taskGroup) } returns taskGroup.copy(archived = true)

        // when
        val result = useCase.execute(taskGroupId = taskGroup.id, userId = userId)

        // then
        assertTrue(result.archived)
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
