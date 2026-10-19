package com.example.foodorder

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import com.example.foodorder.data.SessionManager
import com.example.foodorder.databinding.ActivityLoginBinding

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
    }

    private fun handleLogin() {
        val username = binding.etUsername.text?.toString()?.trim().orEmpty()
        val password = binding.etPassword.text?.toString()?.trim().orEmpty()

        var hasError = false

        if (username.isEmpty()) {
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

        // Dummy login success
        SessionManager.login(username)
        Toast.makeText(this, getString(R.string.login_success), Toast.LENGTH_SHORT).show()

        // Navigate to MainActivity via Explicit Intent
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}
