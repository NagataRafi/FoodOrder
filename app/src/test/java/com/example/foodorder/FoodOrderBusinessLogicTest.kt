package com.example.foodorder

import com.example.foodorder.data.CartManager
import com.example.foodorder.data.FoodRepository
import com.example.foodorder.data.OrderManager
import com.example.foodorder.data.SessionManager
import com.example.foodorder.model.Order
import com.example.foodorder.model.OrderItem
import com.example.foodorder.util.CurrencyUtils
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class FoodOrderBusinessLogicTest {

    @Before
    fun setUp() {
        CartManager.clear()
        OrderManager.clear()
        SessionManager.logout()
    }

    @Test
    fun testFoodRepositoryHasExactlyTenFoods() {
        val foods = FoodRepository.getAllFoods()
        assertEquals("Repository must have exactly 10 foods as defined in PRD", 10, foods.size)

        for (food in foods) {
            assertTrue("Food ID must not be empty", food.id.isNotBlank())
            assertTrue("Food name must not be empty", food.name.isNotBlank())
            assertTrue("Food price must be positive", food.price > 0)
            assertTrue("Food category must be valid", food.category in listOf("Makanan utama", "Minuman"))
            assertTrue("Food description must not be empty", food.description.isNotBlank())
        }

        // Test specific foods
        val nasiGoreng = FoodRepository.getFoodById("F001")
        assertNotNull(nasiGoreng)
        assertEquals("Nasi Goreng Spesial", nasiGoreng?.name)
        assertEquals(25000, nasiGoreng?.price)

        val jusAlpukat = FoodRepository.getFoodById("F010")
        assertNotNull(jusAlpukat)
        assertEquals("Jus Alpukat", jusAlpukat?.name)
        assertEquals(12000, jusAlpukat?.price)
    }

    @Test
    fun testCurrencyFormatting() {
        assertEquals("Rp25.000", CurrencyUtils.formatRupiah(25000))
        assertEquals("Rp80.000", CurrencyUtils.formatRupiah(80000))
        assertEquals("Rp0", CurrencyUtils.formatRupiah(0))
        assertEquals("Rp125.500", CurrencyUtils.formatRupiah(125500))
    }

    @Test
    fun testCartManagerAddDuplicateIncrementsQuantity() {
        val f1 = FoodRepository.getFoodById("F001")!!
        val f2 = FoodRepository.getFoodById("F003")!!

        CartManager.addItem(f1, 2)
        assertEquals(1, CartManager.getItems().size)
        assertEquals(2, CartManager.getTotalItemCount())
        assertEquals(50000, CartManager.getTotalAmount())

        // Add duplicate item
        CartManager.addItem(f1, 1)
        assertEquals("Adding duplicate food must not create new item", 1, CartManager.getItems().size)
        assertEquals(3, CartManager.getTotalItemCount())
        assertEquals(75000, CartManager.getTotalAmount())

        // Add another food
        CartManager.addItem(f2, 1)
        assertEquals(2, CartManager.getItems().size)
        assertEquals(4, CartManager.getTotalItemCount())
        assertEquals(95000, CartManager.getTotalAmount())
    }

    @Test
    fun testCartManagerUpdateAndRemove() {
        val f1 = FoodRepository.getFoodById("F001")!! // Rp25.000
        val f2 = FoodRepository.getFoodById("F009")!! // Rp5.000

        CartManager.addItem(f1, 2) // Rp50.000
        CartManager.addItem(f2, 2) // Rp10.000

        assertEquals(60000, CartManager.getTotalAmount())

        // Update quantity
        CartManager.updateQuantity("F001", 1)
        assertEquals(1, CartManager.getItems().find { it.food.id == "F001" }?.quantity)
        assertEquals(35000, CartManager.getTotalAmount())

        // Decrementing to 0 removes the item
        CartManager.updateQuantity("F001", 0)
        assertEquals(1, CartManager.getItems().size)
        assertFalse(CartManager.getItems().any { it.food.id == "F001" })

        // Explicit remove
        CartManager.removeItem("F009")
        assertTrue(CartManager.isEmpty())
        assertEquals(0, CartManager.getTotalAmount())
    }

    @Test
    fun testOrderManagerPreservesSnapshotAndOrder() {
        val f1 = FoodRepository.getFoodById("F001")!!
        CartManager.addItem(f1, 2)

        val orderItems = CartManager.getItems().map {
            OrderItem(
                foodId = it.food.id,
                foodName = it.food.name,
                price = it.food.price,
                quantity = it.quantity
            )
        }

        val order1 = Order(
            orderId = "ORD-001",
            items = orderItems,
            totalAmount = CartManager.getTotalAmount(),
            paymentMethod = "Tunai (Cash)",
            orderTime = "19/10/2026 12:00:00"
        )

        OrderManager.addOrder(order1)
        assertEquals(1, OrderManager.getOrderCount())

        // Now clear cart as would happen after checkout
        CartManager.clear()
        assertTrue(CartManager.isEmpty())

        // Verify order in OrderManager retains its items and total
        val retrievedOrders = OrderManager.getOrders()
        assertEquals(1, retrievedOrders.size)
        assertEquals(1, retrievedOrders[0].items.size)
        assertEquals("Nasi Goreng Spesial", retrievedOrders[0].items[0].foodName)
        assertEquals(50000, retrievedOrders[0].totalAmount)

        // Add second order, should appear first (most recent)
        val order2 = Order(
            orderId = "ORD-002",
            items = emptyList(),
            totalAmount = 20000,
            paymentMethod = "E-Wallet",
            orderTime = "19/10/2026 12:05:00"
        )
        OrderManager.addOrder(order2)
        assertEquals("ORD-002", OrderManager.getOrders()[0].orderId)
        assertEquals("ORD-001", OrderManager.getOrders()[1].orderId)
    }

    @Test
    fun testSessionManager() {
        assertFalse(SessionManager.isLoggedIn())
        SessionManager.login("Budi Santoso")
        assertTrue(SessionManager.isLoggedIn())
        assertEquals("Budi Santoso", SessionManager.getCurrentUser())
        SessionManager.logout()
        assertFalse(SessionManager.isLoggedIn())
    }
}
