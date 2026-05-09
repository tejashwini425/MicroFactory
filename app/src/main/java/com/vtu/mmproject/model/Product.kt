package com.vtu.mmproject.model

data class Product(
    val id: String = "",
    val businessId: String = "",
    val name: String = "",
    val description: String = "",
    val imageUrl: String = "",
    val wholesalePrice: Double = 0.0,
    val category: String = "",
    val minOrderQuantity: Int = 1,
    val dailyCapacity: Int = 0,
    val capacityUnit: String = "units"
)
