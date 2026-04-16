package com.bank.api.tasks.application.usecase

import com.bank.api.tasks.domain.model.TaskGroupMother
import com.bank.api.tasks.domain.service.TaskGroupService
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ListUserTaskGroupsUseCaseTest {

    private val taskGroupService: TaskGroupService = mockk()
    private val useCase = ListUserTaskGroupsUseCase(taskGroupService = taskGroupService)

    @Test
    fun `should list task groups for user`() {
        // given
        val userId = "user-1"
        val groups = listOf(TaskGroupMother.random(userId = userId))
        every { taskGroupService.findByUser(userId = userId) } returns groups

        // when
        val result = useCase.execute(userId = userId)

        // then
        assertEquals(1, result.size)
        assertEquals(userId, result.first().userId)
    }
}
