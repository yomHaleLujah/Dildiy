package com.yome.dildiy.util

import kotlinx.serialization.Serializable

@Serializable
data class ProductQuantity(
    val productId: Int,
    val productName: String,
    val quantity: Int
)
