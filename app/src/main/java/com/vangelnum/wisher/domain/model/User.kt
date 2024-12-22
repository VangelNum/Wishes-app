package com.vangelnum.wisher.domain.model

data class User(
    val id: Int,
    val name: String,
    val email: String,
    val avatarUrl: String?,
    val role: String,
    val maxWishes: Int
)
