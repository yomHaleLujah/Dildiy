package com.yome.dildiy.remote.dto

import kotlinx.serialization.Serializable


@Serializable
data class User(
    val id: Long? = null,
    val username: String,
    val name: String,
    val email: String,
    val password: String,
    val isEnabled: Boolean,
    val isMerchant: Boolean = false,
    val tinNumber: String? = null,
    val legalName: String? = null
)