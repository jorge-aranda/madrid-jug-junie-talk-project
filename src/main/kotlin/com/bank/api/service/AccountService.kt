package com.bank.api.service

import com.bank.api.model.Account
import com.bank.api.model.CreateAccountRequest
import com.bank.api.model.TransferRequest
import com.bank.api.repository.AccountRepository
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class AccountService(private val accountRepository: AccountRepository) {

    fun createAccount(request: CreateAccountRequest): Account {
        val account = Account(
            owner = request.owner,
            balance = request.initialBalance
        )
        return accountRepository.save(account)
    }

    fun getAccount(id: String): Account {
        return accountRepository.findById(id)
            .orElseThrow { IllegalArgumentException("Account not found: $id") }
    }

    fun getAccountsByOwner(owner: String): List<Account> {
        return accountRepository.findByOwner(owner)
    }

    fun getAllAccounts(): List<Account> {
        return accountRepository.findAll()
    }

    @Synchronized
    fun transfer(request: TransferRequest): Pair<Account, Account> {
        val from = accountRepository.findById(request.fromAccountId)
            .orElseThrow { IllegalArgumentException("Source account not found: ${request.fromAccountId}") }
        val to = accountRepository.findById(request.toAccountId)
            .orElseThrow { IllegalArgumentException("Destination account not found: ${request.toAccountId}") }

        if (from.balance < request.amount) {
            throw IllegalStateException("Insufficient funds in account ${from.id}. Available: ${from.balance}, requested: ${request.amount}")
        }

        from.balance = from.balance - request.amount
        from.updatedAt = Instant.now()
        to.balance = to.balance + request.amount
        to.updatedAt = Instant.now()

        val savedFrom = accountRepository.save(from)
        val savedTo = accountRepository.save(to)

        return Pair(savedFrom, savedTo)
    }
}
