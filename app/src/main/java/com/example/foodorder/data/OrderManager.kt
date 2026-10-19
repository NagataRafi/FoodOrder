package com.example.foodorder.data

import com.example.foodorder.model.Order

object OrderManager {

    private val orders = mutableListOf<Order>()

    /**
     * Adds a newly confirmed order to in-memory order history.
     */
    @Synchronized
    fun addOrder(order: Order) {
        // Prepend to show most recent first
        orders.add(0, order)
    }

    /**
     * Returns all orders, newest first.
     */
    @Synchronized
    fun getOrders(): List<Order> = orders.toList()

    /**
     * Returns total number of orders placed.
     */
    @Synchronized
    fun getOrderCount(): Int = orders.size

    @Synchronized
    fun clear() {
        orders.clear()
    }
}
