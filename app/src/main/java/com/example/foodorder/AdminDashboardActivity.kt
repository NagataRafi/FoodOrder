package com.example.foodorder

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.foodorder.adapter.AdminFoodAdapter
import com.example.foodorder.data.FoodRepository
import com.example.foodorder.data.SessionManager
import com.example.foodorder.databinding.ActivityAdminDashboardBinding
import com.example.foodorder.model.UserRole

class AdminDashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminDashboardBinding
    private lateinit var adapter: AdminFoodAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Security check: Must have active session with role ADMIN
        if (!SessionManager.isLoggedIn() || SessionManager.getCurrentRole() != UserRole.ADMIN) {
            Toast.makeText(this, "Akses ditolak: Hanya admin yang diizinkan", Toast.LENGTH_SHORT).show()
            redirectToLogin()
            return
        }

        binding = ActivityAdminDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        setupRecyclerView()
        setupListeners()
    }

    override fun onResume() {
        super.onResume()
        // Refresh catalog list and summary count when returning from AddMenuActivity
        refreshDashboardData()
    }

    private fun setupUI() {
        val adminName = SessionManager.getCurrentUser()
        val adminEmail = SessionManager.getCurrentUserEmail()

        binding.tvAdminGreeting.text = getString(R.string.admin_greeting_format, adminName)
        binding.tvAdminEmail.text = adminEmail
    }

    private fun setupRecyclerView() {
        adapter = AdminFoodAdapter()
        binding.rvAdminMenuList.layoutManager = LinearLayoutManager(this)
        binding.rvAdminMenuList.adapter = adapter
    }

    private fun refreshDashboardData() {
        val allFoods = FoodRepository.getAllFoods()
        val mainCourseCount = FoodRepository.getFoodCountByCategory("Makanan utama")
        val drinkCount = FoodRepository.getFoodCountByCategory("Minuman")

        binding.tvSummaryTotalMenu.text = getString(R.string.admin_total_menu_format, allFoods.size)
        binding.tvSummaryMainCourse.text = getString(R.string.admin_main_course_count_format, mainCourseCount)
        binding.tvSummaryDrinks.text = getString(R.string.admin_drink_count_format, drinkCount)

        adapter.submitList(allFoods)
    }

    private fun setupListeners() {
        binding.btnAddNewMenu.setOnClickListener {
            val intent = Intent(this, AddMenuActivity::class.java)
            startActivity(intent)
        }

        binding.btnAdminLogout.setOnClickListener {
            handleLogout()
        }
    }

    private fun handleLogout() {
        SessionManager.logout()
        Toast.makeText(this, "Logout berhasil", Toast.LENGTH_SHORT).show()
        redirectToLogin()
    }

    private fun redirectToLogin() {
        val intent = Intent(this, LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        finish()
    }
}
