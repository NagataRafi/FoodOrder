package com.example.foodorder.data

object SessionManager {

    private var currentUser: String? = null

    fun login(usernameOrEmail: String) {
        currentUser = usernameOrEmail.trim()
    }

    fun getCurrentUser(): String {
        return currentUser ?: "Pengguna"
    }

    fun isLoggedIn(): Boolean {
        return !currentUser.isNullOrBlank()
    }

    fun logout() {
        currentUser = null
    }
}
