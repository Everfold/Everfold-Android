package com.everfold.kotlin

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform
