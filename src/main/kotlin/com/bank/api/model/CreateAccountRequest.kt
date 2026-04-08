package com.bank.api.model

import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.NotBlank
import java.math.BigDecimal

data class CreateAccountRequest(
    @field:NotBlank
    val owner: String,
    @field:DecimalMin(value = "0.00")
    val initialBalance: BigDecimal = BigDecimal.ZERO
)
