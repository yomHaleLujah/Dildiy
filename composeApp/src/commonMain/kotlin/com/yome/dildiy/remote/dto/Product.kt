package com.yome.dildiy.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class Product(
    val id: Int?,
    val name: String,
    val description: String,
    val price: Double,
    val stock: Int,
    val category: String,
    val image: String,               // Single main image
    val imageList: List<String>,      // List of additional images
    val userId: String,
    val vat : Double?=0.0
    ) {
}
