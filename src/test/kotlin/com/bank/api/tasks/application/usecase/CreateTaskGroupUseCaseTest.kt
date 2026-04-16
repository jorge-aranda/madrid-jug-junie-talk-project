package com.bank.api.tasks.application.usecase

import com.bank.api.tasks.application.model.TaskGroupRequestMother
import com.bank.api.tasks.domain.model.TaskGroup
import com.bank.api.tasks.domain.service.TaskGroupService
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test

class CreateTaskGroupUseCaseTest {

    private val taskGroupService: TaskGroupService = mockk()
    private val useCase = CreateTaskGroupUseCase(taskGroupService = taskGroupService)

    @Test
    fun `should create a task group from request`() {
        // given
        val request = TaskGroupRequestMother.random(userId = "user-1", name = "My group", description = "desc")
        val taskGroupSlot = slot<TaskGroup>()
        every { taskGroupService.create(taskGroup = capture(taskGroupSlot)) } answers { taskGroupSlot.captured }

        // when
        val result = useCase.execute(request = request)

        // then
        assertEquals("user-1", result.userId)
        assertEquals("My group", result.name)
        assertEquals("desc", result.description)
        assertNotNull(result.id)
    }
}
