package com.vangelnum.wisher.features.home.getwish.data.model

import com.vangelnum.wisher.features.auth.core.model.User

data class Wish(
    val id: Int,
    val text: String,
    val user: User,
    val wishDate: String,
    val image: String,
    val openDate: String,
    val maxViewers: Int?,
    val isBlurred: Boolean,
    val cost: Int
)