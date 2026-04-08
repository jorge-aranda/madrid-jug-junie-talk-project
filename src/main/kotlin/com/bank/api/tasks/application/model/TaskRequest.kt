package com.bank.api.tasks.application.model

/**
 * Application-level model representing a request to create a task.
 */
data class TaskRequest(
    val userId: String,
    val title: String,
    val description: String = ""
)
