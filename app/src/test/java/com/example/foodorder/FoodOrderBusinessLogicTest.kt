package com.example.foodorder

import com.example.foodorder.data.CartManager
import com.example.foodorder.data.FoodRepository
import com.example.foodorder.data.OrderManager
import com.example.foodorder.data.SessionManager
import com.example.foodorder.data.UserManager
import com.example.foodorder.model.Order
import com.example.foodorder.model.OrderItem
import com.example.foodorder.model.UserRole
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
        FoodRepository.resetToDefaults()
        UserManager.clearUsers()
    }

    @Test
    fun testFoodRepositoryHasInitiallyTenFoods() {
        val foods = FoodRepository.getAllFoods()
        assertEquals("Repository must initially have exactly 10 foods", 10, foods.size)

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
    fun testAdminAddFoodSuccessAndUniqueId() {
        val initialCount = FoodRepository.getAllFoods().size
        assertEquals(10, initialCount)

        // Add main course
        val result1 = FoodRepository.addFood(
            name = "Sate Padang",
            category = "Makanan utama",
            description = "Sate lidah sapi empuk berkuah kuning pedas kental gurih khas Minang",
            price = 30000
        )
        assertTrue(result1 is FoodRepository.FoodAdditionResult.Success)
        val food1 = (result1 as FoodRepository.FoodAdditionResult.Success).food
        assertEquals("F011", food1.id)
        assertEquals("Sate Padang", food1.name)
        assertEquals("Makanan utama", food1.category)
        assertEquals(30000, food1.price)

        // Add drink
        val result2 = FoodRepository.addFood(
            name = "Es Jeruk Peras",
            category = "Minuman",
            description = "Perasan jeruk segar asli dengan es batu kristal",
            price = 8000
        )
        assertTrue(result2 is FoodRepository.FoodAdditionResult.Success)
        val food2 = (result2 as FoodRepository.FoodAdditionResult.Success).food
        assertEquals("F012", food2.id)

        // Verify catalog updated dynamically
        assertEquals(12, FoodRepository.getAllFoods().size)
        assertEquals(9, FoodRepository.getFoodCountByCategory("Makanan utama"))
        assertEquals(3, FoodRepository.getFoodCountByCategory("Minuman"))

        // Verify 10 initial foods still exist intact
        assertNotNull(FoodRepository.getFoodById("F001"))
        assertNotNull(FoodRepository.getFoodById("F010"))
        assertEquals(25000, FoodRepository.getFoodById("F001")?.price)
    }

    @Test
    fun testAdminAddFoodValidationFailures() {
        // Empty name
        val res1 = FoodRepository.addFood("", "Makanan utama", "Deskripsi", 20000)
        assertTrue(res1 is FoodRepository.FoodAdditionResult.Error)

        // Invalid category
        val res2 = FoodRepository.addFood("Camilan", "Dessert", "Deskripsi", 10000)
        assertTrue(res2 is FoodRepository.FoodAdditionResult.Error)

        // Zero price
        val res3 = FoodRepository.addFood("Air Putih", "Minuman", "Deskripsi", 0)
        assertTrue(res3 is FoodRepository.FoodAdditionResult.Error)

        // Negative price
        val res4 = FoodRepository.addFood("Air Putih", "Minuman", "Deskripsi", -5000)
        assertTrue(res4 is FoodRepository.FoodAdditionResult.Error)

        // Empty description
        val res5 = FoodRepository.addFood("Air Putih", "Minuman", "", 5000)
        assertTrue(res5 is FoodRepository.FoodAdditionResult.Error)
    }

    @Test
    fun testSearchFoodsIncludesNewItems() {
        FoodRepository.addFood("Kopi Susu Gula Aren", "Minuman", "Kopi robusta berpadu susu segar dan gula aren", 15000)

        val searchResult = FoodRepository.searchFoods("Kopi", "Semua")
        assertEquals(1, searchResult.size)
        assertEquals("Kopi Susu Gula Aren", searchResult[0].name)

        val filteredDrink = FoodRepository.searchFoods("", "Minuman")
        assertTrue(filteredDrink.any { it.name == "Kopi Susu Gula Aren" })
    }

    @Test
    fun testNewFoodCanBeAddedToCartAndCalculatesCorrectCheckout() {
        FoodRepository.addFood("Soto Betawi", "Makanan utama", "Soto daging kuah santan gurih", 35000)
        val soto = FoodRepository.getFoodById("F011")!!

        CartManager.addItem(soto, 2)
        assertEquals(1, CartManager.getItems().size)
        assertEquals(2, CartManager.getTotalItemCount())
        assertEquals(70000, CartManager.getTotalAmount())

        // Add an original item
        val teh = FoodRepository.getFoodById("F009")!! // 5000
        CartManager.addItem(teh, 1)

        assertEquals(3, CartManager.getTotalItemCount())
        assertEquals(75000, CartManager.getTotalAmount())
    }

    @Test
    fun testUserRegistrationSuccessAndDuplicateRejection() {
        val regResult = UserManager.registerUser("Ahmad Dahlan", "ahmad@example.com", "secret123")
        assertTrue(regResult is UserManager.RegistrationResult.Success)
        val registeredUser = (regResult as UserManager.RegistrationResult.Success).user
        assertEquals("Ahmad Dahlan", registeredUser.name)
        assertEquals("ahmad@example.com", registeredUser.email)
        assertEquals(UserRole.USER, registeredUser.role)

        // Duplicate registration should fail
        val dupResult = UserManager.registerUser("Ahmad Lain", "ahmad@example.com", "secret999")
        assertTrue(dupResult is UserManager.RegistrationResult.Error)

        // Registration with admin email must be rejected
        val adminRegResult = UserManager.registerUser("Fake Admin", UserManager.ADMIN_EMAIL, "password")
        assertTrue(adminRegResult is UserManager.RegistrationResult.Error)

        // Newly registered user can authenticate
        val authResult = UserManager.authenticate("ahmad@example.com", "secret123")
        assertTrue(authResult is UserManager.AuthResult.Success)
        assertEquals(UserRole.USER, (authResult as UserManager.AuthResult.Success).user.role)
    }

    @Test
    fun testAdminAuthenticationSuccessAndFailure() {
        // Correct admin credentials
        val adminAuth = UserManager.authenticate("admin@foodorder.local", "Admin123!")
        assertTrue(adminAuth is UserManager.AuthResult.Success)
        val adminUser = (adminAuth as UserManager.AuthResult.Success).user
        assertEquals(UserRole.ADMIN, adminUser.role)
        assertEquals(UserManager.ADMIN_EMAIL, adminUser.email)

        // Wrong admin password
        val wrongAdmin = UserManager.authenticate("admin@foodorder.local", "WrongPassword")
        assertTrue(wrongAdmin is UserManager.AuthResult.InvalidCredentials)

        // Wrong user password
        val wrongUser = UserManager.authenticate("budi@example.com", "wrongpass")
        assertTrue(wrongUser is UserManager.AuthResult.InvalidCredentials)
    }

    @Test
    fun testSessionManagerWithRoles() {
        assertFalse(SessionManager.isLoggedIn())

        // Login as User
        val userAuth = UserManager.authenticate("budi@example.com", "password123")
        assertTrue(userAuth is UserManager.AuthResult.Success)
        SessionManager.login((userAuth as UserManager.AuthResult.Success).user)

        assertTrue(SessionManager.isLoggedIn())
        assertFalse(SessionManager.isAdmin())
        assertEquals(UserRole.USER, SessionManager.getCurrentRole())
        assertEquals("Budi Santoso", SessionManager.getCurrentUser())

        // Logout
        SessionManager.logout()
        assertFalse(SessionManager.isLoggedIn())

        // Login as Admin
        val adminAuth = UserManager.authenticate("admin@foodorder.local", "Admin123!")
        assertTrue(adminAuth is UserManager.AuthResult.Success)
        SessionManager.login((adminAuth as UserManager.AuthResult.Success).user)

        assertTrue(SessionManager.isLoggedIn())
        assertTrue(SessionManager.isAdmin())
        assertEquals(UserRole.ADMIN, SessionManager.getCurrentRole())
        assertEquals("Administrator", SessionManager.getCurrentUser())
        assertEquals("admin@foodorder.local", SessionManager.getCurrentUserEmail())

        SessionManager.logout()
        assertFalse(SessionManager.isLoggedIn())
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
}
