package com.example.foodorder

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.foodorder.adapter.CartAdapter
import com.example.foodorder.data.CartManager
import com.example.foodorder.databinding.ActivityCartBinding
import com.example.foodorder.model.Food
import com.example.foodorder.util.CurrencyUtils

class CartActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCartBinding
    private lateinit var cartAdapter: CartAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupRecyclerView()
        setupListeners()
    }

    override fun onResume() {
        super.onResume()
        updateCartState()
    }

    private fun setupToolbar() {
        binding.toolbarCart.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setupRecyclerView() {
        cartAdapter = CartAdapter(
            onQuantityChanged = { food, newQuantity ->
                CartManager.updateQuantity(food.id, newQuantity)
                updateCartState()
            },
            onItemRemoved = { food ->
                removeItem(food)
            }
        )

        binding.rvCartItems.apply {
            layoutManager = LinearLayoutManager(this@CartActivity)
            adapter = cartAdapter
        }
    }

    private fun setupListeners() {
        binding.btnProceedCheckout.setOnClickListener {
            if (CartManager.isEmpty()) {
                Toast.makeText(
                    this,
                    getString(R.string.error_empty_cart_checkout),
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }
            val intent = Intent(this, CheckoutActivity::class.java)
            startActivity(intent)
        }

        binding.btnExploreMenu.setOnClickListener {
            finish()
        }
    }

    private fun removeItem(food: Food) {
        CartManager.removeItem(food.id)
        Toast.makeText(
            this,
            getString(R.string.cart_item_removed, food.name),
            Toast.LENGTH_SHORT
        ).show()
        updateCartState()
    }

    private fun updateCartState() {
        val items = CartManager.getItems()
        if (items.isEmpty()) {
            binding.layoutCartContent.visibility = View.GONE
            binding.layoutEmptyCart.visibility = View.VISIBLE
        } else {
            binding.layoutEmptyCart.visibility = View.GONE
            binding.layoutCartContent.visibility = View.VISIBLE
            cartAdapter.submitList(items)

            val totalItems = CartManager.getTotalItemCount()
            val totalAmount = CartManager.getTotalAmount()

            binding.tvTotalItemCount.text = "Total Pembayaran ($totalItems item):"
            binding.tvCartTotalAmount.text = CurrencyUtils.formatRupiah(totalAmount)
        }
    }
}
