package com.bank.api.tasks.application.usecase

import com.bank.api.tasks.domain.model.Task
import com.bank.api.tasks.domain.model.TaskMother
import com.bank.api.tasks.domain.service.TaskService
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class CompleteTaskUseCaseTest {

    private val taskService: TaskService = mockk()
    private val useCase = CompleteTaskUseCase(taskService = taskService)

    @Test
    fun `should complete a task owned by the user`() {
        // given
        val userId = "user-1"
        val task = TaskMother.random(userId = userId)
        every { taskService.findById(id = task.id) } returns task
        every { taskService.complete(task = task) } returns task.copy(completed = true)

        // when
        val result = useCase.execute(taskId = task.id, userId = userId)

        // then
        assertTrue(result.completed)
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
