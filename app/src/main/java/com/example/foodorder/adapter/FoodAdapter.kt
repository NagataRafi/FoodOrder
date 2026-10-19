package com.example.foodorder.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.foodorder.R
import com.example.foodorder.databinding.ItemFoodBinding
import com.example.foodorder.model.Food
import com.example.foodorder.util.CurrencyUtils

class FoodAdapter(
    private val onItemClick: (Food) -> Unit
) : ListAdapter<Food, FoodAdapter.FoodViewHolder>(FoodDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodViewHolder {
        val binding = ItemFoodBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return FoodViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FoodViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class FoodViewHolder(
        private val binding: ItemFoodBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(food: Food) {
            binding.tvFoodName.text = food.name
            binding.tvFoodCategory.text = food.category
            binding.tvFoodPrice.text = CurrencyUtils.formatRupiah(food.price)

            // Safe image resource loading with fallback
            try {
                if (food.imageResId != 0) {
                    binding.ivFoodImage.setImageResource(food.imageResId)
                } else {
                    binding.ivFoodImage.setImageResource(R.drawable.ic_placeholder_food)
                }
            } catch (e: Exception) {
                binding.ivFoodImage.setImageResource(R.drawable.ic_placeholder_food)
            }

            binding.cardFood.setOnClickListener { onItemClick(food) }
            binding.btnViewDetail.setOnClickListener { onItemClick(food) }
        }
    }

    class FoodDiffCallback : DiffUtil.ItemCallback<Food>() {
        override fun areItemsTheSame(oldItem: Food, newItem: Food): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Food, newItem: Food): Boolean =
            oldItem == newItem
    }
}
