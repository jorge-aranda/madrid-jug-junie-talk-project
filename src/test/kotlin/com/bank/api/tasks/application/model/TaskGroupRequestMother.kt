package com.bank.api.tasks.application.model

import java.util.UUID

/**
 * ObjectMother for [TaskGroupRequest] application model.
 */
object TaskGroupRequestMother {

    fun random(
        userId: String = UUID.randomUUID().toString(),
        name: String = "Group name",
        description: String = "Group description"
    ): TaskGroupRequest = TaskGroupRequest(
        userId = userId,
        name = name,
        description = description
    )
}
