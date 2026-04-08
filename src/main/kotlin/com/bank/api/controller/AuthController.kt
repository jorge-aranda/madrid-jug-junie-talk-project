package com.bank.api.controller

import com.bank.api.model.RegisterRequest
import com.bank.api.service.UserService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "User registration")
class AuthController(private val userService: UserService) {

    @PostMapping("/register")
    @Operation(summary = "Register a new user")
    fun register(@Valid @RequestBody request: RegisterRequest): ResponseEntity<Map<String, String>> {
        val user = userService.register(request)
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(mapOf("message" to "User '${user.username}' registered successfully"))
    }
}
