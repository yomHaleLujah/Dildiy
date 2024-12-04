package com.yome.dildiy.Dao

import kotlinx.serialization.Serializable

@Serializable
data class PaymentResponse(
    val message: String,
    val status: String,
    val data: PaymentData
)

@Serializable
data class PaymentData(
    val first_name: String? = null, // Nullable string
    val last_name: String? = null,  // Nullable string
    val email: String? = null,      // Nullable string
    val phone_number: String? = null, // Nullable string
    val currency: String,
    val amount: Int,
    val charge: Double,
    val mode: String,
    val method: String,
    val type: String,
    val status: String,
    val reference: String,
    val tx_ref: String,
    val customization: Customization,
    val meta: String? = null,
    val created_at: String,
    val updated_at: String
)

@Serializable
data class Customization(
    val title: String,
    val description: String,
    val logo: String? = null // Nullable string
)
