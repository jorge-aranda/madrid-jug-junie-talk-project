package com.bank.api.tasks.api.controller

import com.bank.api.tasks.api.model.TaskRequestDto
import com.bank.api.tasks.api.model.TaskResponseDto
import com.bank.api.tasks.application.model.TaskRequest
import com.bank.api.tasks.application.usecase.ArchiveTaskUseCase
import com.bank.api.tasks.application.usecase.CompleteTaskUseCase
import com.bank.api.tasks.application.usecase.CreateTaskUseCase
import com.bank.api.tasks.application.usecase.GetTaskDetailUseCase
import com.bank.api.tasks.application.usecase.ListUserTasksUseCase
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
 * REST controller exposing task operations for authenticated users.
 *
 * The user is identified via Spring Security's authenticated principal.
 */
@RestController
@RequestMapping("/api/tasks")
class TaskController(
    private val createTaskUseCase: CreateTaskUseCase,
    private val listUserTasksUseCase: ListUserTasksUseCase,
    private val completeTaskUseCase: CompleteTaskUseCase,
    private val archiveTaskUseCase: ArchiveTaskUseCase,
    private val getTaskDetailUseCase: GetTaskDetailUseCase
) {

    /**
     * Creates a new task owned by the requesting user.
     */
    @PutMapping
    fun create(
        principal: Principal,
        @Valid @RequestBody dto: TaskRequestDto
    ): ResponseEntity<TaskResponseDto> {
        val request = TaskRequest(
            userId = principal.name,
            title = dto.title,
            description = dto.description
        )
        val task = createTaskUseCase.execute(request = request)
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(TaskResponseDto.from(task = task))
    }

    /**
     * Lists all non-archived tasks for the requesting user.
     */
    @GetMapping
    fun list(
        principal: Principal
    ): ResponseEntity<List<TaskResponseDto>> {
        val tasks = listUserTasksUseCase.execute(userId = principal.name)
        return ResponseEntity.ok(
            tasks.map { TaskResponseDto.from(task = it) }
        )
    }

    /**
     * Returns the detail of a specific task.
     */
    @GetMapping("/{taskId}")
    fun detail(
        principal: Principal,
        @PathVariable taskId: String
    ): ResponseEntity<TaskResponseDto> {
        val task = getTaskDetailUseCase.execute(
            taskId = taskId,
            userId = principal.name
        )
        return ResponseEntity.ok(
            TaskResponseDto.from(task = task)
        )
    }

    /**
     * Marks a task as completed (partial update).
     */
    @PatchMapping("/{taskId}/complete")
    fun complete(
        principal: Principal,
        @PathVariable taskId: String
    ): ResponseEntity<TaskResponseDto> {
        val task = completeTaskUseCase.execute(
            taskId = taskId,
            userId = principal.name
        )
        return ResponseEntity.ok(
            TaskResponseDto.from(task = task)
        )
    }

    /**
     * Archives (soft-deletes) a task.
     */
    @DeleteMapping("/{taskId}")
    fun archive(
        principal: Principal,
        @PathVariable taskId: String
    ): ResponseEntity<TaskResponseDto> {
        val task = archiveTaskUseCase.execute(
            taskId = taskId,
            userId = principal.name
        )
        return ResponseEntity.ok(
            TaskResponseDto.from(task = task)
        )
    }
}
