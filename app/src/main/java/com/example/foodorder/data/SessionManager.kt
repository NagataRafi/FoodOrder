package com.example.foodorder.data

import com.example.foodorder.model.User
import com.example.foodorder.model.UserRole

object SessionManager {

    private var currentUser: User? = null

    fun login(user: User) {
        currentUser = user
    }

    // Overload for backward compatibility with existing tests/code
    fun login(usernameOrEmail: String, role: UserRole = UserRole.USER) {
        currentUser = User(
            name = usernameOrEmail.trim(),
            email = if (usernameOrEmail.contains("@")) usernameOrEmail.trim() else "${usernameOrEmail.trim().lowercase().replace(" ", "")}@foodorder.local",
            passwordHashOrPlain = "",
            role = role
        )
    }

    fun getCurrentUser(): String {
        return currentUser?.name ?: "Pengguna"
    }

    fun getCurrentUserEmail(): String {
        return currentUser?.email ?: ""
    }

    fun getCurrentRole(): UserRole {
        return currentUser?.role ?: UserRole.USER
    }

    fun isAdmin(): Boolean {
        return currentUser?.role == UserRole.ADMIN
    }

    fun isLoggedIn(): Boolean {
        return currentUser != null
    }

    fun logout() {
        currentUser = null
    }
}
