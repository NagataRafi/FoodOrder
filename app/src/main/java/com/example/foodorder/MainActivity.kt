package com.example.foodorder

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.GridLayoutManager
import com.example.foodorder.adapter.FoodAdapter
import com.example.foodorder.data.CartManager
import com.example.foodorder.data.FoodRepository
import com.example.foodorder.data.SessionManager
import com.example.foodorder.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var foodAdapter: FoodAdapter
    private var selectedCategory: String = "Semua"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupGreeting()
        setupRecyclerView()
        setupSearchAndFilters()
        setupActionButtons()
        setupBottomNavigation()
    }

    override fun onResume() {
        super.onResume()
        updateCartBadge()
        filterFoodList()
        // Reset bottom navigation selection to catalog
        binding.bottomNavigation.selectedItemId = R.id.nav_catalog
    }

    private fun setupGreeting() {
        val userName = SessionManager.getCurrentUser()
        binding.tvGreeting.text = getString(R.string.greeting_format, userName)
    }

    private fun setupRecyclerView() {
        foodAdapter = FoodAdapter { food ->
            val intent = Intent(this, DetailMenuActivity::class.java).apply {
                putExtra(DetailMenuActivity.EXTRA_FOOD_ID, food.id)
            }
            startActivity(intent)
        }

        binding.rvFoodCatalog.apply {
            layoutManager = GridLayoutManager(this@MainActivity, 2)
            adapter = foodAdapter
        }

        filterFoodList()
    }

    private fun setupSearchAndFilters() {
        binding.etSearch.doOnTextChanged { _, _, _, _ ->
            filterFoodList()
        }

        binding.chipGroupCategories.setOnCheckedStateChangeListener { _, checkedIds ->
            selectedCategory = when {
                checkedIds.contains(R.id.chipMainCourse) -> "Makanan utama"
                checkedIds.contains(R.id.chipDrink) -> "Minuman"
                else -> "Semua"
            }
            filterFoodList()
        }
    }

    private fun filterFoodList() {
        val query = binding.etSearch.text?.toString()?.trim().orEmpty()
        val filteredList = FoodRepository.searchFoods(query, selectedCategory)

        foodAdapter.submitList(filteredList)

        if (filteredList.isEmpty()) {
            binding.rvFoodCatalog.visibility = View.GONE
            binding.layoutEmptySearch.visibility = View.VISIBLE
        } else {
            binding.layoutEmptySearch.visibility = View.GONE
            binding.rvFoodCatalog.visibility = View.VISIBLE
        }
    }

    private fun setupActionButtons() {
        // Cart button
        binding.btnCart.setOnClickListener {
            openCartActivity()
        }

        // History button
        binding.btnHistory.setOnClickListener {
            openHistoryActivity()
        }

        // Implicit Intent: Open restaurant search in browser
        binding.btnBrowseOnline.setOnClickListener {
            openBrowserImplicitIntent()
        }

        // Implicit Intent: Open restaurant location in Maps
        binding.btnViewMap.setOnClickListener {
            openMapsImplicitIntent()
        }
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_catalog -> {
                    // Already on catalog, scroll to top
                    binding.rvFoodCatalog.smoothScrollToPosition(0)
                    true
                }
                R.id.nav_cart -> {
                    openCartActivity()
                    false
                }
                R.id.nav_history -> {
                    openHistoryActivity()
                    false
                }
                else -> false
            }
        }
    }

    private fun openCartActivity() {
        val intent = Intent(this, CartActivity::class.java)
        startActivity(intent)
    }

    private fun openHistoryActivity() {
        val intent = Intent(this, OrderHistoryActivity::class.java)
        startActivity(intent)
    }

    private fun updateCartBadge() {
        val totalCount = CartManager.getTotalItemCount()
        if (totalCount > 0) {
            binding.tvCartBadge.visibility = View.VISIBLE
            binding.tvCartBadge.text = totalCount.toString()
        } else {
            binding.tvCartBadge.visibility = View.GONE
        }
    }

    /**
     * Implicit Intent: Opens browser search for restaurants
     */
    private fun openBrowserImplicitIntent() {
        val browserUrl = "https://www.google.com/search?q=restoran+makanan"
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(browserUrl))

        try {
            startActivity(browserIntent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(this, getString(R.string.error_browser_not_available), Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(this, getString(R.string.error_browser_not_available), Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Implicit Intent: Opens Google Maps search for nearby restaurants
     */
    private fun openMapsImplicitIntent() {
        val mapsUri = Uri.parse("geo:0,0?q=restoran")
        val mapsIntent = Intent(Intent.ACTION_VIEW, mapsUri)

        try {
            startActivity(mapsIntent)
        } catch (e: ActivityNotFoundException) {
            // Fallback to web maps in browser
            fallbackToWebMaps()
        } catch (e: Exception) {
            fallbackToWebMaps()
        }
    }

    private fun fallbackToWebMaps() {
        val webMapsUrl = "https://www.google.com/maps/search/restoran"
        val webMapsIntent = Intent(Intent.ACTION_VIEW, Uri.parse(webMapsUrl))
        try {
            Toast.makeText(this, getString(R.string.error_maps_not_available), Toast.LENGTH_SHORT).show()
            startActivity(webMapsIntent)
        } catch (e: Exception) {
            Toast.makeText(this, getString(R.string.error_all_maps_failed), Toast.LENGTH_SHORT).show()
        }
    }
}