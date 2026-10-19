package com.example.foodorder

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.foodorder.adapter.OrderHistoryAdapter
import com.example.foodorder.data.OrderManager
import com.example.foodorder.databinding.ActivityOrderHistoryBinding

class OrderHistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOrderHistoryBinding
    private lateinit var orderHistoryAdapter: OrderHistoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupRecyclerView()
        setupListeners()
    }

    override fun onResume() {
        super.onResume()
        loadOrders()
    }

    private fun setupToolbar() {
        binding.toolbarHistory.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setupRecyclerView() {
        orderHistoryAdapter = OrderHistoryAdapter()
        binding.rvOrderHistory.apply {
            layoutManager = LinearLayoutManager(this@OrderHistoryActivity)
            adapter = orderHistoryAdapter
        }
    }

    private fun setupListeners() {
        binding.btnExploreFromHistory.setOnClickListener {
            finish()
        }
    }

    private fun loadOrders() {
        val orders = OrderManager.getOrders()
        if (orders.isEmpty()) {
            binding.rvOrderHistory.visibility = View.GONE
            binding.layoutEmptyHistory.visibility = View.VISIBLE
        } else {
            binding.layoutEmptyHistory.visibility = View.GONE
            binding.rvOrderHistory.visibility = View.VISIBLE
            orderHistoryAdapter.submitList(orders)
        }
    }
}
