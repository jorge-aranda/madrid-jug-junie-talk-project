package com.bank.api.tasks.domain.service

import com.bank.api.tasks.domain.model.TaskGroupMother
import com.bank.api.tasks.domain.repository.FakeTaskGroupRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class TaskGroupServiceTest {

    private lateinit var repository: FakeTaskGroupRepository
    private lateinit var taskGroupService: TaskGroupService

    @BeforeEach
    fun setUp() {
        repository = FakeTaskGroupRepository()
        taskGroupService = TaskGroupService(taskGroupRepository = repository)
    }

    @Test
    fun `should create a task group`() {
        // given
        val taskGroup = TaskGroupMother.random()

        // when
        val result = taskGroupService.create(taskGroup = taskGroup)

        // then
        assertEquals(taskGroup, result)
        assertEquals(taskGroup, repository.findById(id = taskGroup.id))
    }

    @Test
    fun `should find task groups by user excluding archived`() {
        // given
        val userId = "user-1"
        val active = TaskGroupMother.random(userId = userId)
        val archived = TaskGroupMother.archived(userId = userId)
        repository.save(taskGroup = active)
        repository.save(taskGroup = archived)

        // when
        val result = taskGroupService.findByUser(userId = userId)

        // then
        assertEquals(1, result.size)
        assertEquals(active.id, result.first().id)
    }

    @Test
    fun `should find task group by id`() {
        // given
        val taskGroup = TaskGroupMother.random()
        repository.save(taskGroup = taskGroup)

        // when
        val result = taskGroupService.findById(id = taskGroup.id)

        // then
        assertEquals(taskGroup, result)
    }

    @Test
    fun `should return null when task group not found`() {
        // given
        val id = "non-existent"

        // when
        val result = taskGroupService.findById(id = id)

        // then
        assertNull(result)
    }

    @Test
    fun `should add a task to a group`() {
        // given
        val taskGroup = TaskGroupMother.random()
        repository.save(taskGroup = taskGroup)

        // when
        val result = taskGroupService.addTask(taskGroup = taskGroup, taskId = "task-1")

        // then
        assertTrue(result.taskIds.contains("task-1"))
    }

    @Test
    fun `should not duplicate task when adding existing task`() {
        // given
        val taskGroup = TaskGroupMother.random(taskIds = listOf("task-1"))
        repository.save(taskGroup = taskGroup)

        // when
        val result = taskGroupService.addTask(taskGroup = taskGroup, taskId = "task-1")

        // then
        assertEquals(1, result.taskIds.size)
    }

    @Test
    fun `should remove a task from a group`() {
        // given
        val taskGroup = TaskGroupMother.random(taskIds = listOf("task-1", "task-2"))
        repository.save(taskGroup = taskGroup)

        // when
        val result = taskGroupService.removeTask(taskGroup = taskGroup, taskId = "task-1")

        // then
        assertEquals(listOf("task-2"), result.taskIds)
    }

    @Test
    fun `should archive a task group`() {
        // given
        val taskGroup = TaskGroupMother.random()
        repository.save(taskGroup = taskGroup)

        // when
        val result = taskGroupService.archive(taskGroup = taskGroup)

        // then
        assertTrue(result.archived)
    }
}
