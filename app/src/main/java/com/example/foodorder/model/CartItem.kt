package com.example.foodorder.model

data class CartItem(
    val food: Food,
    var quantity: Int
) {
    val subtotal: Int
        get() = food.price * quantity
}
