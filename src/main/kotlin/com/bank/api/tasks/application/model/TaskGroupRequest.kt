package com.bank.api.tasks.application.model

/**
 * Application-level model representing a request to create a task group.
 */
data class TaskGroupRequest(
    val userId: String,
    val name: String,
    val description: String = ""
)
