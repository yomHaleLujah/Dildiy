package com.yome.dildiy

data class Product(
    val id: Int,
    val name: String,
    val description: String,
    val price: Double,
    val stock: Int,
    val image: String,               // Single main image
    val imageList: List<String>      // List of additional images
)