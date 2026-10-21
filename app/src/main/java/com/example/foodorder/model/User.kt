package com.example.foodorder.model

enum class UserRole {
    USER,
    ADMIN
}

data class User(
    val name: String,
    val email: String,
    val passwordHashOrPlain: String,
    val role: UserRole = UserRole.USER
)
