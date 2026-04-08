package com.bank.api.service

import com.bank.api.model.RegisterRequest
import com.bank.api.model.User
import com.bank.api.repository.UserRepository
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) : UserDetailsService {

    fun register(request: RegisterRequest): User {
        if (userRepository.existsByUsername(request.username)) {
            throw IllegalStateException("Username '${request.username}' is already taken")
        }
        val user = User(
            username = request.username,
            password = passwordEncoder.encode(request.password)
        )
        return userRepository.save(user)
    }

    override fun loadUserByUsername(username: String): UserDetails {
        val user = userRepository.findByUsername(username)
            .orElseThrow { UsernameNotFoundException("User not found: $username") }
        return org.springframework.security.core.userdetails.User
            .withUsername(user.username)
            .password(user.password)
            .authorities("ROLE_USER")
            .build()
    }
}
