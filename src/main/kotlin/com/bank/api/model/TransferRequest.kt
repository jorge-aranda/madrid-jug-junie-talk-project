package com.bank.api.model

import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.NotBlank
import java.math.BigDecimal

data class TransferRequest(
    @field:NotBlank
    val fromAccountId: String,
    @field:NotBlank
    val toAccountId: String,
    @field:DecimalMin(value = "0.01")
    val amount: BigDecimal
)
