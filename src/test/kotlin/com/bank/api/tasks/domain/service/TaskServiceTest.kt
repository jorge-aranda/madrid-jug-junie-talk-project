package com.bank.api.tasks.domain.service

import com.bank.api.tasks.domain.model.TaskMother
import com.bank.api.tasks.domain.repository.FakeTaskRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class TaskServiceTest {

    private lateinit var repository: FakeTaskRepository
    private lateinit var taskService: TaskService

    @BeforeEach
    fun setUp() {
        repository = FakeTaskRepository()
        taskService = TaskService(taskRepository = repository)
    }

    @Test
    fun `should create a task`() {
        // given
        val task = TaskMother.random()

        // when
        val result = taskService.create(task = task)

        // then
        assertEquals(task, result)
        assertEquals(task, repository.findById(id = task.id))
    }

    @Test
    fun `should find tasks by user excluding archived`() {
        // given
        val userId = "user-1"
        val active = TaskMother.random(userId = userId)
        val archived = TaskMother.archived(userId = userId)
        repository.save(task = active)
        repository.save(task = archived)

        // when
        val result = taskService.findByUser(userId = userId)

        // then
        assertEquals(1, result.size)
        assertEquals(active.id, result.first().id)
    }

    @Test
    fun `should find task by id`() {
        // given
        val task = TaskMother.random()
        repository.save(task = task)

        // when
        val result = taskService.findById(id = task.id)

        // then
        assertEquals(task, result)
    }

    @Test
    fun `should return null when task not found`() {
        // given
        val id = "non-existent"

        // when
        val result = taskService.findById(id = id)

        // then
        assertNull(result)
    }

    @Test
    fun `should complete a task`() {
        // given
        val task = TaskMother.random()
        repository.save(task = task)

        // when
        val result = taskService.complete(task = task)

        // then
        assertTrue(result.completed)
    }

    @Test
    fun `should archive a task`() {
        // given
        val task = TaskMother.random()
        repository.save(task = task)

        // when
        val result = taskService.archive(task = task)

        // then
        assertTrue(result.archived)
    }
}
