package com.yome.dildiy.model

import kotlinx.serialization.Serializable

@Serializable
data class Orders(
    val id: Long = 0,
    val userId: String, // User ID associated with the order
    val fullName: String, // User's full name
    val email: String, // User's email address
    val phone: String, // User's phone number
    val address: String, // Shipping address
    val apartment: String?, // Optional apartment number
    val specialInstructions: String?, // Optional special instructions for the delivery
    val location: String, // Location (e.g., city or region)
    val totalPrice: Double, // Total price of the order
    var status: String = "Pending", // Default status
    val createdAt: String, // Timestamp of when the order was created
    var updatedAt: String, // Timestamp of when the order was last updated
    val cartItems: String, // Cart items, could be JSON or string format
    val items: List<OrderItem> // List of order items
)

@Serializable
data class OrderItem(
    val productId: String, // Product ID as String (as per your backend model)
    val productName: String, // Product name
    val quantity: Int, // Quantity of the product
    val price: Double // Price of the product
)
