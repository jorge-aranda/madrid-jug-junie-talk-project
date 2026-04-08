package com.bank.api.repository

import com.bank.api.model.Account
import org.springframework.data.mongodb.repository.MongoRepository

interface AccountRepository : MongoRepository<Account, String> {
    fun findByOwner(owner: String): List<Account>
}
