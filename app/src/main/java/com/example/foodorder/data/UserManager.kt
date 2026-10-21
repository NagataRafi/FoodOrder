package com.example.foodorder.data

import com.example.foodorder.model.User
import com.example.foodorder.model.UserRole

object UserManager {

    const val ADMIN_EMAIL = "admin@foodorder.local"
    const val ADMIN_PASSWORD = "Admin123!"
    const val ADMIN_NAME = "Administrator"

    // Default dummy user for convenience
    private val defaultUsers = listOf(
        User(name = "Budi Santoso", email = "budi@example.com", passwordHashOrPlain = "password123", role = UserRole.USER),
        User(name = "Pengguna Demo", email = "user@foodorder.local", passwordHashOrPlain = "User123!", role = UserRole.USER)
    )

    private val registeredUsers = mutableListOf<User>().apply {
        addAll(defaultUsers)
    }

    fun registerUser(name: String, email: String, password: String): RegistrationResult {
        val trimmedName = name.trim()
        val trimmedEmail = email.trim()

        if (trimmedName.isEmpty()) {
            return RegistrationResult.Error("Nama lengkap wajib diisi")
        }
        if (trimmedEmail.isEmpty()) {
            return RegistrationResult.Error("Email wajib diisi")
        }
        if (!isValidEmail(trimmedEmail)) {
            return RegistrationResult.Error("Format email tidak valid")
        }
        if (password.isEmpty()) {
            return RegistrationResult.Error("Kata sandi wajib diisi")
        }
        if (password.length < 6) {
            return RegistrationResult.Error("Kata sandi minimal 6 karakter")
        }

        // Rule: Admin account cannot be created through registration
        if (trimmedEmail.equals(ADMIN_EMAIL, ignoreCase = true)) {
            return RegistrationResult.Error("Email admin tidak dapat didaftarkan sebagai user")
        }

        // Duplicate email check
        val exists = registeredUsers.any { it.email.equals(trimmedEmail, ignoreCase = true) }
        if (exists) {
            return RegistrationResult.Error("Email sudah terdaftar. Silakan gunakan email lain atau masuk.")
        }

        val newUser = User(
            name = trimmedName,
            email = trimmedEmail,
            passwordHashOrPlain = password,
            role = UserRole.USER
        )
        registeredUsers.add(newUser)
        return RegistrationResult.Success(newUser)
    }

    fun authenticate(usernameOrEmail: String, password: String): AuthResult {
        val identifier = usernameOrEmail.trim()
        if (identifier.isEmpty() || password.isEmpty()) {
            return AuthResult.InvalidCredentials("Nama/email dan kata sandi wajib diisi")
        }

        // Check Admin credentials explicitly
        if (identifier.equals(ADMIN_EMAIL, ignoreCase = true)) {
            return if (password == ADMIN_PASSWORD) {
                AuthResult.Success(
                    User(
                        name = ADMIN_NAME,
                        email = ADMIN_EMAIL,
                        passwordHashOrPlain = "",
                        role = UserRole.ADMIN
                    )
                )
            } else {
                AuthResult.InvalidCredentials("Kata sandi admin salah")
            }
        }

        // Check registered regular users (matches either email or name case-insensitively)
        val user = registeredUsers.find {
            (it.email.equals(identifier, ignoreCase = true) || it.name.equals(identifier, ignoreCase = true))
        }

        return if (user != null && user.passwordHashOrPlain == password) {
            AuthResult.Success(user)
        } else {
            AuthResult.InvalidCredentials("Email atau kata sandi tidak sesuai")
        }
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS?.matcher(email)?.matches()
            ?: (email.contains("@") && email.contains(".") && !email.startsWith("@"))
    }

    fun clearUsers() {
        registeredUsers.clear()
        registeredUsers.addAll(defaultUsers)
    }

    sealed class RegistrationResult {
        data class Success(val user: User) : RegistrationResult()
        data class Error(val message: String) : RegistrationResult()
    }

    sealed class AuthResult {
        data class Success(val user: User) : AuthResult()
        data class InvalidCredentials(val message: String) : AuthResult()
    }
}
