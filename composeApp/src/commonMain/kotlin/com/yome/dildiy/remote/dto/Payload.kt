package com.yome.dildiy.remote.dto

import kotlinx.serialization.Serializable


@Serializable
data class Payload(
    val amount: String,
    val currency: String,
    val email: String,
    val first_name: String,
    val last_name: String,
    val phone_number: String,
    val tx_ref: String,
    val callback_url: String,
    val return_url: String,
    val customization: Customization
)
