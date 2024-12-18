package com.yome.dildiy.model

import androidx.room.Entity
import androidx.room.ForeignKey
import com.yome.dildiy.networking.CartItemDTO
import kotlinx.serialization.Serializable

@Serializable
data class Orders(
    val id: Long? = null,
    val userId: String, // User ID associated with the order
    val fullName: String, // User's full name
    val email: String, // User's email address
    val phone: String, // User's phone number
    val address: String, // Shipping address
    val apartment: String?, // Optional apartment number
    val specialInstructions: String?, // Optional special instructions for the delivery
    val location: String, // Location (e.g., city or region)
    var totalPrice: Double, // Total price of the order
    var status: String = "Pending", // Default status
    val createdAt: String, // Timestamp of when the order was created
    var updatedAt: String, // Timestamp of when the order was last updated
    val txRef :String? = null,
    val orderNumber: String? = null,
    val cartItems: List<CartItemDTO> = emptyList(),
    val items: List<OrderItem>  = emptyList()// List of order items
)

@Serializable
data class OrderItem(
    val id: Long? = null,
    val productId: String, // Product ID as String (as per your backend model)
    val productName: String, // Product name
    val quantity: Int, // Quantity of the product
    val price: Double // Price of the product
)
