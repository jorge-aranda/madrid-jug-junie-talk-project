package com.bank.api.model

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class RegisterRequest(
    @field:NotBlank
    @field:Size(min = 3, max = 50)
    val username: String,
    @field:NotBlank
    @field:Size(min = 6)
    val password: String
)
