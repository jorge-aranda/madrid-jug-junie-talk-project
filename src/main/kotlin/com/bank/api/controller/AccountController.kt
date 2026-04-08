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
import java.security.Principal

@RestController
@RequestMapping("/api/accounts")
@Tag(name = "Accounts", description = "Bank account management operations")
class AccountController(private val accountService: AccountService) {

    @PostMapping
    @Operation(summary = "Create a new bank account for the authenticated user")
    fun createAccount(@Valid @RequestBody request: CreateAccountRequest, principal: Principal): ResponseEntity<Account> {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(accountService.createAccount(request, principal.name))
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get account by ID (only own accounts)")
    fun getAccount(@PathVariable id: String, principal: Principal): ResponseEntity<Account> {
        return ResponseEntity.ok(accountService.getAccountForOwner(id, principal.name))
    }

    @GetMapping
    @Operation(summary = "Get all accounts owned by the authenticated user")
    fun getAccounts(principal: Principal): ResponseEntity<List<Account>> {
        return ResponseEntity.ok(accountService.getAccountsByOwner(principal.name))
    }

    @PostMapping("/transfer")
    @Operation(summary = "Transfer funds from own account to any account")
    fun transfer(@Valid @RequestBody request: TransferRequest, principal: Principal): ResponseEntity<Map<String, Account>> {
        val (from, to) = accountService.transfer(request, principal.name)
        return ResponseEntity.ok(mapOf("from" to from, "to" to to))
    }
}
