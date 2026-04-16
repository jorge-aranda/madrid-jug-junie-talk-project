package com.bank.api.tasks.api.controller

import com.bank.api.tasks.api.model.TaskGroupRequestDto
import com.bank.api.tasks.api.model.TaskGroupResponseDto
import com.bank.api.tasks.application.model.TaskGroupRequest
import com.bank.api.tasks.application.usecase.AddTaskToGroupUseCase
import com.bank.api.tasks.application.usecase.ArchiveTaskGroupUseCase
import com.bank.api.tasks.application.usecase.CreateTaskGroupUseCase
import com.bank.api.tasks.application.usecase.GetTaskGroupDetailUseCase
import com.bank.api.tasks.application.usecase.ListUserTaskGroupsUseCase
import com.bank.api.tasks.application.usecase.RemoveTaskFromGroupUseCase
import jakarta.validation.Valid
import java.security.Principal
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * REST controller exposing task group operations for authenticated users.
 *
 * The user is identified via Spring Security's authenticated principal.
 */
@RestController
@RequestMapping("/api/task-groups")
class TaskGroupController(
    private val createTaskGroupUseCase: CreateTaskGroupUseCase,
    private val listUserTaskGroupsUseCase: ListUserTaskGroupsUseCase,
    private val getTaskGroupDetailUseCase: GetTaskGroupDetailUseCase,
    private val addTaskToGroupUseCase: AddTaskToGroupUseCase,
    private val removeTaskFromGroupUseCase: RemoveTaskFromGroupUseCase,
    private val archiveTaskGroupUseCase: ArchiveTaskGroupUseCase
) {

    /**
     * Creates a new task group owned by the requesting user.
     */
    @PutMapping
    fun create(
        principal: Principal,
        @Valid @RequestBody dto: TaskGroupRequestDto
    ): ResponseEntity<TaskGroupResponseDto> {
        val request = TaskGroupRequest(
            userId = principal.name,
            name = dto.name,
            description = dto.description
        )
        val taskGroup = createTaskGroupUseCase.execute(request = request)
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(TaskGroupResponseDto.from(taskGroup = taskGroup))
    }

    /**
     * Lists all non-archived task groups for the requesting user.
     */
    @GetMapping
    fun list(
        principal: Principal
    ): ResponseEntity<List<TaskGroupResponseDto>> {
        val groups = listUserTaskGroupsUseCase.execute(userId = principal.name)
        return ResponseEntity.ok(
            groups.map { TaskGroupResponseDto.from(taskGroup = it) }
        )
    }

    /**
     * Returns the detail of a specific task group.
     */
    @GetMapping("/{taskGroupId}")
    fun detail(
        principal: Principal,
        @PathVariable taskGroupId: String
    ): ResponseEntity<TaskGroupResponseDto> {
        val taskGroup = getTaskGroupDetailUseCase.execute(
            taskGroupId = taskGroupId,
            userId = principal.name
        )
        return ResponseEntity.ok(
            TaskGroupResponseDto.from(taskGroup = taskGroup)
        )
    }

    /**
     * Adds a task to a task group (partial update).
     */
    @PatchMapping("/{taskGroupId}/tasks/{taskId}")
    fun addTask(
        principal: Principal,
        @PathVariable taskGroupId: String,
        @PathVariable taskId: String
    ): ResponseEntity<TaskGroupResponseDto> {
        val taskGroup = addTaskToGroupUseCase.execute(
            taskGroupId = taskGroupId,
            taskId = taskId,
            userId = principal.name
        )
        return ResponseEntity.ok(
            TaskGroupResponseDto.from(taskGroup = taskGroup)
        )
    }

    /**
     * Removes a task from a task group.
     */
    @DeleteMapping("/{taskGroupId}/tasks/{taskId}")
    fun removeTask(
        principal: Principal,
        @PathVariable taskGroupId: String,
        @PathVariable taskId: String
    ): ResponseEntity<TaskGroupResponseDto> {
        val taskGroup = removeTaskFromGroupUseCase.execute(
            taskGroupId = taskGroupId,
            taskId = taskId,
            userId = principal.name
        )
        return ResponseEntity.ok(
            TaskGroupResponseDto.from(taskGroup = taskGroup)
        )
    }

    /**
     * Archives (soft-deletes) a task group.
     */
    @DeleteMapping("/{taskGroupId}")
    fun archive(
        principal: Principal,
        @PathVariable taskGroupId: String
    ): ResponseEntity<TaskGroupResponseDto> {
        val taskGroup = archiveTaskGroupUseCase.execute(
            taskGroupId = taskGroupId,
            userId = principal.name
        )
        return ResponseEntity.ok(
            TaskGroupResponseDto.from(taskGroup = taskGroup)
        )
    }
}
