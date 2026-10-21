package com.example.foodorder

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Window
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import com.example.foodorder.data.FoodRepository
import com.example.foodorder.data.SessionManager
import com.example.foodorder.databinding.ActivityAddMenuBinding
import com.example.foodorder.databinding.DialogAddMenuSuccessBinding
import com.example.foodorder.model.Food
import com.example.foodorder.model.UserRole

class AddMenuActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddMenuBinding
    private var isSaving = false

    private val categoryOptions = arrayOf("Makanan utama", "Minuman")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Security check: Must have active session with role ADMIN
        if (!SessionManager.isLoggedIn() || SessionManager.getCurrentRole() != UserRole.ADMIN) {
            Toast.makeText(this, "Akses ditolak: Hanya admin yang diizinkan", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        binding = ActivityAddMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupCategoryDropdown()
        setupValidationListeners()
        setupActions()
    }

    private fun setupToolbar() {
        binding.toolbarAddMenu.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setupCategoryDropdown() {
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, categoryOptions)
        binding.actvCategory.setAdapter(adapter)
        // Default selection
        binding.actvCategory.setText(categoryOptions[0], false)
    }

    private fun setupValidationListeners() {
        binding.etMenuName.doOnTextChanged { _, _, _, _ ->
            binding.tilMenuName.error = null
        }
        binding.actvCategory.doOnTextChanged { _, _, _, _ ->
            binding.tilMenuCategory.error = null
        }
        binding.etMenuPrice.doOnTextChanged { _, _, _, _ ->
            binding.tilMenuPrice.error = null
        }
        binding.etMenuDesc.doOnTextChanged { _, _, _, _ ->
            binding.tilMenuDesc.error = null
        }
    }

    private fun setupActions() {
        binding.btnSaveMenu.setOnClickListener {
            handleSaveMenu()
        }

        binding.btnCancelMenu.setOnClickListener {
            finish()
        }
    }

    private fun handleSaveMenu() {
        if (isSaving) return

        val name = binding.etMenuName.text?.toString()?.trim().orEmpty()
        val category = binding.actvCategory.text?.toString()?.trim().orEmpty()
        val priceStr = binding.etMenuPrice.text?.toString()?.trim().orEmpty()
        val desc = binding.etMenuDesc.text?.toString()?.trim().orEmpty()

        var hasError = false

        if (name.isEmpty()) {
            binding.tilMenuName.error = getString(R.string.error_empty_menu_name)
            hasError = true
        }

        if (category.isEmpty() || (category != "Makanan utama" && category != "Minuman")) {
            binding.tilMenuCategory.error = getString(R.string.error_empty_category)
            hasError = true
        }

        val price = priceStr.toIntOrNull()
        if (price == null || price <= 0) {
            binding.tilMenuPrice.error = getString(R.string.error_invalid_price)
            hasError = true
        }

        if (desc.isEmpty()) {
            binding.tilMenuDesc.error = getString(R.string.error_empty_description)
            hasError = true
        }

        if (hasError) {
            return
        }

        // Prevent multi-clicks
        isSaving = true
        binding.btnSaveMenu.isEnabled = false

        when (val result = FoodRepository.addFood(name, category, desc, price!!, R.drawable.ic_placeholder_food)) {
            is FoodRepository.FoodAdditionResult.Success -> {
                showSuccessDialog(result.food)
            }
            is FoodRepository.FoodAdditionResult.Error -> {
                isSaving = false
                binding.btnSaveMenu.isEnabled = true
                Toast.makeText(this, result.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showSuccessDialog(food: Food) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val dialogBinding = DialogAddMenuSuccessBinding.inflate(layoutInflater)
        dialog.setContentView(dialogBinding.root)
        dialog.setCancelable(false)

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialogBinding.tvAddMenuSuccessMessage.text =
            getString(R.string.add_menu_success_message, food.name)

        dialogBinding.btnAddMenuOk.setOnClickListener {
            dialog.dismiss()
            finish()
        }

        dialog.show()
    }
}
