package com.bank.api.tasks.domain.model

import java.time.Instant

/**
 * Domain entity representing a group of tasks owned by a user.
 */
data class TaskGroup(
    val id: String,
    val userId: String,
    val name: String,
    val description: String = "",
    val taskIds: List<String> = emptyList(),
    val archived: Boolean = false,
    val createdAt: Instant = Instant.now(),
    val updatedAt: Instant = Instant.now()
)
