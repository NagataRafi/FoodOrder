package com.example.foodorder

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import com.example.foodorder.data.UserManager
import com.example.foodorder.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListeners()
    }

    private fun setupListeners() {
        // Clear errors as user types
        binding.etFullName.doOnTextChanged { _, _, _, _ ->
            binding.tilFullName.error = null
        }
        binding.etEmail.doOnTextChanged { _, _, _, _ ->
            binding.tilEmail.error = null
        }
        binding.etPassword.doOnTextChanged { _, _, _, _ ->
            binding.tilPassword.error = null
        }
        binding.etConfirmPassword.doOnTextChanged { _, _, _, _ ->
            binding.tilConfirmPassword.error = null
        }

        binding.btnRegister.setOnClickListener {
            handleRegister()
        }

        binding.tvBackToLogin.setOnClickListener {
            finish()
        }
    }

    private fun handleRegister() {
        val name = binding.etFullName.text?.toString()?.trim().orEmpty()
        val email = binding.etEmail.text?.toString()?.trim().orEmpty()
        val password = binding.etPassword.text?.toString().orEmpty()
        val confirmPassword = binding.etConfirmPassword.text?.toString().orEmpty()

        var hasError = false

        if (name.isEmpty()) {
            binding.tilFullName.error = getString(R.string.error_empty_name)
            hasError = true
        }

        if (email.isEmpty()) {
            binding.tilEmail.error = getString(R.string.error_empty_email)
            hasError = true
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.tilEmail.error = getString(R.string.error_invalid_email)
            hasError = true
        }

        if (password.isEmpty()) {
            binding.tilPassword.error = getString(R.string.error_empty_password)
            hasError = true
        } else if (password.length < 6) {
            binding.tilPassword.error = getString(R.string.error_password_short)
            hasError = true
        }

        if (confirmPassword.isEmpty()) {
            binding.tilConfirmPassword.error = "Konfirmasi kata sandi wajib diisi"
            hasError = true
        } else if (password != confirmPassword) {
            binding.tilConfirmPassword.error = getString(R.string.error_password_mismatch)
            hasError = true
        }

        if (hasError) {
            return
        }

        when (val result = UserManager.registerUser(name, email, password)) {
            is UserManager.RegistrationResult.Success -> {
                Toast.makeText(this, getString(R.string.register_success), Toast.LENGTH_LONG).show()
                finish()
            }
            is UserManager.RegistrationResult.Error -> {
                if (result.message.contains("email", ignoreCase = true)) {
                    binding.tilEmail.error = result.message
                } else {
                    binding.tilFullName.error = result.message
                }
                Toast.makeText(this, result.message, Toast.LENGTH_SHORT).show()
            }
        }
    }
}
