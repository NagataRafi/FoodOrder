package com.example.foodorder.model

import java.io.Serializable

data class Food(
    val id: String,
    val name: String,
    val category: String,
    val description: String,
    val price: Int,
    val imageResId: Int
) : Serializable
