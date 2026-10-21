package com.example.foodorder

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import com.example.foodorder.data.SessionManager
import com.example.foodorder.data.UserManager
import com.example.foodorder.databinding.ActivityLoginBinding
import com.example.foodorder.model.UserRole

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListeners()
    }

    private fun setupListeners() {
        // Clear errors as user types
        binding.etUsername.doOnTextChanged { _, _, _, _ ->
            binding.tilUsername.error = null
        }
        binding.etPassword.doOnTextChanged { _, _, _, _ ->
            binding.tilPassword.error = null
        }

        binding.btnLogin.setOnClickListener {
            handleLogin()
        }

        binding.tvGoToRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun handleLogin() {
        val usernameOrEmail = binding.etUsername.text?.toString()?.trim().orEmpty()
        val password = binding.etPassword.text?.toString()?.trim().orEmpty()

        var hasError = false

        if (usernameOrEmail.isEmpty()) {
            binding.tilUsername.error = getString(R.string.error_empty_username)
            hasError = true
        }

        if (password.isEmpty()) {
            binding.tilPassword.error = getString(R.string.error_empty_password)
            hasError = true
        }

        if (hasError) {
            return
        }

        when (val result = UserManager.authenticate(usernameOrEmail, password)) {
            is UserManager.AuthResult.Success -> {
                val user = result.user
                SessionManager.login(user)

                if (user.role == UserRole.ADMIN) {
                    Toast.makeText(this, "Selamat datang Administrator", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, AdminDashboardActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, getString(R.string.login_success), Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
            is UserManager.AuthResult.InvalidCredentials -> {
                binding.tilPassword.error = result.message
                Toast.makeText(this, result.message, Toast.LENGTH_SHORT).show()
            }
        }
    }
}
