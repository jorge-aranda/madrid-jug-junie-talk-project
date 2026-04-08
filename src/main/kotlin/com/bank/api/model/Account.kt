package com.bank.api.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.math.BigDecimal
import java.time.Instant

@Document(collection = "accounts")
data class Account(
    @Id
    val id: String? = null,
    val owner: String,
    var balance: BigDecimal = BigDecimal.ZERO,
    val createdAt: Instant = Instant.now(),
    var updatedAt: Instant = Instant.now()
)
