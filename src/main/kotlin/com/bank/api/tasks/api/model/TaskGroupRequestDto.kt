package com.bank.api.tasks.api.model

import jakarta.validation.constraints.NotBlank

/**
 * DTO for task group creation requests coming from the API layer.
 */
data class TaskGroupRequestDto(
    @field:NotBlank(message = "name is required")
    val name: String = "",
    val description: String = ""
)
