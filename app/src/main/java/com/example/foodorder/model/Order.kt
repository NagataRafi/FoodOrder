package com.example.foodorder.model

import java.io.Serializable

data class OrderItem(
    val foodId: String,
    val foodName: String,
    val price: Int,
    val quantity: Int
) : Serializable {
    val subtotal: Int
        get() = price * quantity
}

data class Order(
    val orderId: String,
    val items: List<OrderItem>,
    val totalAmount: Int,
    val paymentMethod: String,
    val orderTime: String,
    val status: String = "Berhasil (Simulasi)"
) : Serializable
