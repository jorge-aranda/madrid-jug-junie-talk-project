package com.bank.api.controller

import com.bank.api.model.Account
import com.bank.api.model.CreateAccountRequest
import com.bank.api.model.TransferRequest
import com.bank.api.service.AccountService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/accounts")
@Tag(name = "Accounts", description = "Bank account management operations")
class AccountController(private val accountService: AccountService) {

    @PostMapping
    @Operation(summary = "Create a new bank account")
    fun createAccount(@Valid @RequestBody request: CreateAccountRequest): ResponseEntity<Account> {
        return ResponseEntity.status(HttpStatus.CREATED).body(accountService.createAccount(request))
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get account by ID")
    fun getAccount(@PathVariable id: String): ResponseEntity<Account> {
        return ResponseEntity.ok(accountService.getAccount(id))
    }

    @GetMapping
    @Operation(summary = "Get all accounts, optionally filtered by owner")
    fun getAccounts(@RequestParam(required = false) owner: String?): ResponseEntity<List<Account>> {
        val accounts = if (owner != null) accountService.getAccountsByOwner(owner) else accountService.getAllAccounts()
        return ResponseEntity.ok(accounts)
    }

    @PostMapping("/transfer")
    @Operation(summary = "Transfer funds between two accounts")
    fun transfer(@Valid @RequestBody request: TransferRequest): ResponseEntity<Map<String, Account>> {
        val (from, to) = accountService.transfer(request)
        return ResponseEntity.ok(mapOf("from" to from, "to" to to))
    }
}
