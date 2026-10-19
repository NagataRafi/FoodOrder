package com.example.foodorder

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.foodorder.data.CartManager
import com.example.foodorder.data.FoodRepository
import com.example.foodorder.databinding.ActivityDetailMenuBinding
import com.example.foodorder.model.Food
import com.example.foodorder.util.CurrencyUtils

class DetailMenuActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_FOOD_ID = "EXTRA_FOOD_ID"
    }

    private lateinit var binding: ActivityDetailMenuBinding
    private var food: Food? = null
    private var quantity = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val foodId = intent.getStringExtra(EXTRA_FOOD_ID)
        food = foodId?.let { FoodRepository.getFoodById(it) }

        if (food == null) {
            Toast.makeText(this, "Makanan tidak ditemukan", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        setupToolbar()
        populateViews()
        setupListeners()
    }

    private fun setupToolbar() {
        binding.toolbarDetail.setNavigationOnClickListener {
            finish()
        }
    }

    private fun populateViews() {
        val currentFood = food ?: return

        binding.tvDetailFoodName.text = currentFood.name
        binding.tvDetailCategory.text = currentFood.category
        binding.tvDetailDescription.text = currentFood.description
        binding.tvDetailFoodPrice.text = CurrencyUtils.formatRupiah(currentFood.price)

        try {
            if (currentFood.imageResId != 0) {
                binding.ivDetailFoodImage.setImageResource(currentFood.imageResId)
            } else {
                binding.ivDetailFoodImage.setImageResource(R.drawable.ic_placeholder_food)
            }
        } catch (e: Exception) {
            binding.ivDetailFoodImage.setImageResource(R.drawable.ic_placeholder_food)
        }

        updateQuantityViews()
    }

    private fun setupListeners() {
        binding.btnMinus.setOnClickListener {
            if (quantity > 1) {
                quantity--
                updateQuantityViews()
            }
        }

        binding.btnPlus.setOnClickListener {
            quantity++
            updateQuantityViews()
        }

        binding.btnAddToCart.setOnClickListener {
            val currentFood = food ?: return@setOnClickListener
            CartManager.addItem(currentFood, quantity)
            Toast.makeText(
                this,
                getString(R.string.added_to_cart_success, currentFood.name),
                Toast.LENGTH_SHORT
            ).show()
            finish()
        }
    }

    private fun updateQuantityViews() {
        val currentFood = food ?: return
        binding.tvQuantity.text = quantity.toString()
        val subtotal = currentFood.price * quantity
        binding.tvSubtotal.text = CurrencyUtils.formatRupiah(subtotal)
        binding.btnMinus.isEnabled = (quantity > 1)
    }
}
