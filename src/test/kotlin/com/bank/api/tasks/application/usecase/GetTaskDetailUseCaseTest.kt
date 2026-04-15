package com.bank.api.tasks.application.usecase

import com.bank.api.tasks.domain.model.TaskMother
import com.bank.api.tasks.domain.service.TaskService
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class GetTaskDetailUseCaseTest {

    private val taskService: TaskService = mockk()
    private val useCase = GetTaskDetailUseCase(taskService = taskService)

    @Test
    fun `should return task detail when owned by user`() {
        // given
        val userId = "user-1"
        val task = TaskMother.random(userId = userId)
        every { taskService.findById(id = task.id) } returns task

        // when
        val result = useCase.execute(taskId = task.id, userId = userId)

        // then
        assertEquals(task, result)
    }

    @Test
    fun `should throw when task not found`() {
        // given
        every { taskService.findById(id = "missing") } returns null

        // when / then
        assertThrows<NoSuchElementException> {
            useCase.execute(taskId = "missing", userId = "user-1")
        }
    }

    @Test
    fun `should throw when task does not belong to user`() {
        // given
        val task = TaskMother.random(userId = "owner")
        every { taskService.findById(id = task.id) } returns task

        // when / then
        assertThrows<IllegalArgumentException> {
            useCase.execute(taskId = task.id, userId = "other-user")
        }
    }
}
