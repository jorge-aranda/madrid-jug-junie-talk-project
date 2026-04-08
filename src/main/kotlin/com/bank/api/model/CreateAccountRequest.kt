package com.bank.api.model

import jakarta.validation.constraints.DecimalMin
import java.math.BigDecimal

data class CreateAccountRequest(
    @field:DecimalMin(value = "0.00")
    val initialBalance: BigDecimal = BigDecimal.ZERO
)
