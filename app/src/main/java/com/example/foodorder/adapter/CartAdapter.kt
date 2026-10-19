package com.example.foodorder.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.foodorder.R
import com.example.foodorder.databinding.ItemCartBinding
import com.example.foodorder.model.CartItem
import com.example.foodorder.model.Food
import com.example.foodorder.util.CurrencyUtils

class CartAdapter(
    private val onQuantityChanged: (Food, Int) -> Unit,
    private val onItemRemoved: (Food) -> Unit
) : ListAdapter<CartItem, CartAdapter.CartViewHolder>(CartDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = ItemCartBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class CartViewHolder(
        private val binding: ItemCartBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: CartItem) {
            val food = item.food
            binding.tvCartFoodName.text = food.name
            binding.tvCartUnitPrice.text = "${CurrencyUtils.formatRupiah(food.price)} / porsi"
            binding.tvCartQuantity.text = item.quantity.toString()
            binding.tvCartSubtotal.text = "Subtotal: ${CurrencyUtils.formatRupiah(item.subtotal)}"

            try {
                if (food.imageResId != 0) {
                    binding.ivCartFoodThumb.setImageResource(food.imageResId)
                } else {
                    binding.ivCartFoodThumb.setImageResource(R.drawable.ic_placeholder_food)
                }
            } catch (e: Exception) {
                binding.ivCartFoodThumb.setImageResource(R.drawable.ic_placeholder_food)
            }

            binding.btnCartPlus.setOnClickListener {
                onQuantityChanged(food, item.quantity + 1)
            }

            binding.btnCartMinus.setOnClickListener {
                if (item.quantity > 1) {
                    onQuantityChanged(food, item.quantity - 1)
                } else {
                    // Reached 0, remove
                    onItemRemoved(food)
                }
            }

            binding.btnDeleteCartItem.setOnClickListener {
                onItemRemoved(food)
            }
        }
    }

    class CartDiffCallback : DiffUtil.ItemCallback<CartItem>() {
        override fun areItemsTheSame(oldItem: CartItem, newItem: CartItem): Boolean =
            oldItem.food.id == newItem.food.id

        override fun areContentsTheSame(oldItem: CartItem, newItem: CartItem): Boolean =
            oldItem == newItem && oldItem.quantity == newItem.quantity
    }
}
