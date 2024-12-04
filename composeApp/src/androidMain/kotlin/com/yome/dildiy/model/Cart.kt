package com.yome.dildiy.model

import kotlinx.serialization.Serializable


@Serializable
data class Cart(
    val id: Long = 0,
    val cartItems: List<CartItem> = mutableListOf(),

    val userName: String  // Unique identifier for each user (can be userId or userName)
)
