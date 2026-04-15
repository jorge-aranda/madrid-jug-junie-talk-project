package com.bank.api.tasks.api.model

import jakarta.validation.constraints.NotBlank

/**
 * DTO for task creation requests coming from the API layer.
 */
data class TaskRequestDto(
    @field:NotBlank(message = "title is required")
    val title: String = "",
    val description: String = ""
)
