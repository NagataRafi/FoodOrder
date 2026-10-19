package com.example.foodorder.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.foodorder.databinding.ItemOrderHistoryBinding
import com.example.foodorder.model.Order
import com.example.foodorder.util.CurrencyUtils

class OrderHistoryAdapter :
    ListAdapter<Order, OrderHistoryAdapter.OrderHistoryViewHolder>(OrderDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderHistoryViewHolder {
        val binding = ItemOrderHistoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return OrderHistoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderHistoryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class OrderHistoryViewHolder(
        private val binding: ItemOrderHistoryBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(order: Order) {
            binding.tvHistoryOrderId.text = order.orderId
            binding.tvHistoryOrderTime.text = order.orderTime
            binding.tvHistoryPaymentMethod.text = order.paymentMethod
            binding.tvHistoryStatus.text = order.status
            binding.tvHistoryTotalAmount.text = CurrencyUtils.formatRupiah(order.totalAmount)

            val summary = if (order.items.isNotEmpty()) {
                order.items.joinToString("\n") { "• ${it.quantity}x ${it.foodName} (${CurrencyUtils.formatRupiah(it.subtotal)})" }
            } else {
                "• Tidak ada item"
            }
            binding.tvHistoryItemsSummary.text = summary
        }
    }

    class OrderDiffCallback : DiffUtil.ItemCallback<Order>() {
        override fun areItemsTheSame(oldItem: Order, newItem: Order): Boolean =
            oldItem.orderId == newItem.orderId

        override fun areContentsTheSame(oldItem: Order, newItem: Order): Boolean =
            oldItem == newItem
    }
}
