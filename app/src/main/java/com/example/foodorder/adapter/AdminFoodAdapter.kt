package com.example.foodorder.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.foodorder.databinding.ItemAdminFoodBinding
import com.example.foodorder.model.Food
import com.example.foodorder.util.CurrencyUtils

class AdminFoodAdapter : RecyclerView.Adapter<AdminFoodAdapter.AdminFoodViewHolder>() {

    private val foods = mutableListOf<Food>()

    fun submitList(newFoods: List<Food>) {
        foods.clear()
        foods.addAll(newFoods)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdminFoodViewHolder {
        val binding = ItemAdminFoodBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return AdminFoodViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AdminFoodViewHolder, position: Int) {
        holder.bind(foods[position])
    }

    override fun getItemCount(): Int = foods.size

    class AdminFoodViewHolder(private val binding: ItemAdminFoodBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(food: Food) {
            binding.ivAdminFoodThumb.setImageResource(food.imageResId)
            binding.tvAdminFoodName.text = food.name
            binding.tvAdminFoodCategory.text = food.category
            binding.tvAdminFoodDesc.text = food.description
            binding.tvAdminFoodId.text = "ID: ${food.id}"
            binding.tvAdminFoodPrice.text = CurrencyUtils.formatRupiah(food.price)
        }
    }
}
