package com.yome.dildiy

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform