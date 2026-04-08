package com.bank.api.service

import com.bank.api.model.Account
import com.bank.api.model.CreateAccountRequest
import com.bank.api.model.TransferRequest
import com.bank.api.repository.AccountRepository
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class AccountService(private val accountRepository: AccountRepository) {

    fun createAccount(request: CreateAccountRequest, owner: String): Account {
        val account = Account(
            owner = owner,
            balance = request.initialBalance
        )
        return accountRepository.save(account)
    }

    fun getAccountForOwner(id: String, owner: String): Account {
        val account = accountRepository.findById(id)
            .orElseThrow { IllegalArgumentException("Account not found: $id") }
        if (account.owner != owner) {
            throw SecurityException("Access denied: you do not own this account")
        }
        return account
    }

    fun getAccountsByOwner(owner: String): List<Account> {
        return accountRepository.findByOwner(owner)
    }

    @Synchronized
    fun transfer(request: TransferRequest, owner: String): Pair<Account, Account> {
        val from = accountRepository.findById(request.fromAccountId)
            .orElseThrow { IllegalArgumentException("Source account not found: ${request.fromAccountId}") }

        if (from.owner != owner) {
            throw SecurityException("Access denied: you can only transfer from your own accounts")
        }

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
