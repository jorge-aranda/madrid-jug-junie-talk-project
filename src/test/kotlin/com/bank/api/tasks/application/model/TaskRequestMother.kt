package com.bank.api.tasks.application.model

import java.util.UUID

/**
 * ObjectMother for [TaskRequest] application model.
 */
object TaskRequestMother {

    fun random(
        userId: String = UUID.randomUUID().toString(),
        title: String = "Task title",
        description: String = "Task description"
    ): TaskRequest = TaskRequest(
        userId = userId,
        title = title,
        description = description
    )
}
