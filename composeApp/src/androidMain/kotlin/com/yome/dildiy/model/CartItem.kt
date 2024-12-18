package com.yome.dildiy.model

import com.yome.dildiy.remote.dto.Product
import kotlinx.serialization.Serializable
import java.io.Serial


@Serializable
data class CartItem(
    val id: Long = 0,

    val cart: Cart,

    val product: Product,

    var quantity: Int,

    val username: String
)
