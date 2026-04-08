package com.bank.api.tasks.domain.model

import java.time.Instant

/**
 * Domain entity representing a task owned by a user.
 */
data class Task(
    val id: String,
    val userId: String,
    val title: String,
    val description: String = "",
    val completed: Boolean = false,
    val archived: Boolean = false,
    val createdAt: Instant = Instant.now(),
    val updatedAt: Instant = Instant.now()
)
