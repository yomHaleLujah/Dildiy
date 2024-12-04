package com.yome.dildiy.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class Customization(
    val title: String,
    val description: String
)