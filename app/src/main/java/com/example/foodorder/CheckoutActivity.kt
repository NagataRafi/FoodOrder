package com.example.foodorder

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Window
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.foodorder.data.CartManager
import com.example.foodorder.data.OrderManager
import com.example.foodorder.databinding.ActivityCheckoutBinding
import com.example.foodorder.databinding.DialogOrderConfirmationBinding
import com.example.foodorder.databinding.DialogValidationNoticeBinding
import com.example.foodorder.databinding.ItemCheckoutFoodBinding
import com.example.foodorder.model.Order
import com.example.foodorder.model.OrderItem
import com.example.foodorder.util.CurrencyUtils
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CheckoutActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCheckoutBinding
    private var isOrderProcessing = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCheckoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (CartManager.isEmpty()) {
            Toast.makeText(
                this,
                getString(R.string.error_empty_cart_checkout),
                Toast.LENGTH_SHORT
            ).show()
            finish()
            return
        }

        setupToolbar()
        populateOrderSummary()
        setupListeners()
    }

    private fun setupToolbar() {
        binding.toolbarCheckout.setNavigationOnClickListener {
            finish()
        }
    }

    private fun populateOrderSummary() {
        binding.layoutCheckoutItems.removeAllViews()

        val items = CartManager.getItems()
        val inflater = layoutInflater

        for (item in items) {
            val itemBinding = ItemCheckoutFoodBinding.inflate(inflater, binding.layoutCheckoutItems, false)
            itemBinding.tvCheckoutFoodName.text = item.food.name
            itemBinding.tvCheckoutQuantityAndPrice.text =
                "${item.quantity}x @ ${CurrencyUtils.formatRupiah(item.food.price)}"
            itemBinding.tvCheckoutSubtotal.text = CurrencyUtils.formatRupiah(item.subtotal)
            binding.layoutCheckoutItems.addView(itemBinding.root)
        }

        val totalAmount = CartManager.getTotalAmount()
        binding.tvCheckoutSubtotalAmount.text = CurrencyUtils.formatRupiah(totalAmount)
        binding.tvCheckoutTotalAmount.text = CurrencyUtils.formatRupiah(totalAmount)
    }

    private fun setupListeners() {
        binding.btnConfirmOrder.setOnClickListener {
            handleConfirmOrder()
        }
    }

    private fun handleConfirmOrder() {
        if (isOrderProcessing) return

        if (CartManager.isEmpty()) {
            Toast.makeText(this, getString(R.string.error_empty_cart_checkout), Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val selectedRadioId = binding.rgPaymentMethod.checkedRadioButtonId
        if (selectedRadioId == -1) {
            // Show redesigned payment validation dialog without modifying cart or creating order
            showPaymentValidationDialog()
            return
        }

        val selectedRadioButton = findViewById<RadioButton>(selectedRadioId)
        val paymentMethod = selectedRadioButton?.text?.toString() ?: getString(R.string.payment_cash)

        // Prevent duplicate processing
        isOrderProcessing = true
        binding.btnConfirmOrder.isEnabled = false

        // Create Order snapshot
        val totalAmount = CartManager.getTotalAmount()
        val orderItems = CartManager.getItems().map { cartItem ->
            OrderItem(
                foodId = cartItem.food.id,
                foodName = cartItem.food.name,
                price = cartItem.food.price,
                quantity = cartItem.quantity
            )
        }

        val orderId = String.format("ORD-%06d", (System.currentTimeMillis() % 1000000).toInt())
        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.forLanguageTag("id-ID"))
        val formattedTime = dateFormat.format(Date())

        val order = Order(
            orderId = orderId,
            items = orderItems,
            totalAmount = totalAmount,
            paymentMethod = paymentMethod,
            orderTime = formattedTime,
            status = getString(R.string.order_status_success)
        )

        // Save order and clear cart
        OrderManager.addOrder(order)
        CartManager.clear()

        // Show Confirmation Dialog
        showConfirmationDialog(order)
    }

    private fun showPaymentValidationDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val dialogBinding = DialogValidationNoticeBinding.inflate(layoutInflater)
        dialog.setContentView(dialogBinding.root)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialogBinding.btnValidationDismiss.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun showConfirmationDialog(order: Order) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val dialogBinding = DialogOrderConfirmationBinding.inflate(layoutInflater)
        dialog.setContentView(dialogBinding.root)
        dialog.setCancelable(false)

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialogBinding.tvDialogOrderId.text = order.orderId
        dialogBinding.tvDialogPaymentMethod.text = order.paymentMethod
        dialogBinding.tvDialogOrderTime.text = order.orderTime
        dialogBinding.tvDialogTotalAmount.text = CurrencyUtils.formatRupiah(order.totalAmount)

        dialogBinding.btnDialogViewHistory.setOnClickListener {
            dialog.dismiss()
            val historyIntent = Intent(this, OrderHistoryActivity::class.java)
            startActivity(historyIntent)
            finish()
        }

        dialogBinding.btnDialogBackToCatalog.setOnClickListener {
            dialog.dismiss()
            val catalogIntent = Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            }
            startActivity(catalogIntent)
            finish()
        }

        dialog.show()
    }
}
