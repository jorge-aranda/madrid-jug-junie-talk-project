package com.bank.api.tasks.application.usecase

import com.bank.api.tasks.domain.model.TaskMother
import com.bank.api.tasks.domain.service.TaskService
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ListUserTasksUseCaseTest {

    private val taskService: TaskService = mockk()
    private val useCase = ListUserTasksUseCase(taskService = taskService)

    @Test
    fun `should return non-archived tasks for user`() {
        // given
        val userId = "user-1"
        val tasks = listOf(
            TaskMother.random(userId = userId),
            TaskMother.random(userId = userId)
        )
        every { taskService.findByUser(userId = userId) } returns tasks

        // when
        val result = useCase.execute(userId = userId)

        // then
        assertEquals(2, result.size)
    }

    @Test
    fun `should return empty list when user has no tasks`() {
        // given
        val userId = "user-1"
        every { taskService.findByUser(userId = userId) } returns emptyList()

        // when
        val result = useCase.execute(userId = userId)

        // then
        assertEquals(0, result.size)
    }
}
