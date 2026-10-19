package com.example.foodorder.data

import com.example.foodorder.model.CartItem
import com.example.foodorder.model.Food

object CartManager {

    private val cartItems = mutableListOf<CartItem>()

    /**
     * Adds food to cart with given quantity.
     * If food already exists in cart, updates quantity without duplicating entry.
     */
    @Synchronized
    fun addItem(food: Food, quantity: Int = 1) {
        if (quantity <= 0) return
        val existingIndex = cartItems.indexOfFirst { it.food.id == food.id }
        if (existingIndex >= 0) {
            cartItems[existingIndex].quantity += quantity
        } else {
            cartItems.add(CartItem(food = food, quantity = quantity))
        }
    }

    /**
     * Updates quantity of specified food.
     * If newQuantity <= 0, the item is removed from cart.
     */
    @Synchronized
    fun updateQuantity(foodId: String, newQuantity: Int) {
        val existingIndex = cartItems.indexOfFirst { it.food.id == foodId }
        if (existingIndex >= 0) {
            if (newQuantity <= 0) {
                cartItems.removeAt(existingIndex)
            } else {
                cartItems[existingIndex].quantity = newQuantity
            }
        }
    }

    /**
     * Removes an item from the cart.
     */
    @Synchronized
    fun removeItem(foodId: String) {
        cartItems.removeAll { it.food.id == foodId }
    }

    /**
     * Returns an unmodifiable snapshot of current cart items.
     */
    @Synchronized
    fun getItems(): List<CartItem> = cartItems.toList()

    /**
     * Returns total count of all food portions in cart.
     */
    @Synchronized
    fun getTotalItemCount(): Int = cartItems.sumOf { it.quantity }

    /**
     * Returns total Rupiah amount of all items in cart.
     */
    @Synchronized
    fun getTotalAmount(): Int = cartItems.sumOf { it.subtotal }

    /**
     * Returns true if cart has no items.
     */
    @Synchronized
    fun isEmpty(): Boolean = cartItems.isEmpty()

    /**
     * Clears all items in the cart (e.g. after successful checkout).
     */
    @Synchronized
    fun clear() {
        cartItems.clear()
    }
}
