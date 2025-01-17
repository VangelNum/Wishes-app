package com.vangelnum.wisher.features.home.getwish.data.model

import com.vangelnum.wisher.features.home.User

data class GetWishResponse(
    val id: Int,
    val text: String,
    val user: User,
    val wishDate: String,
    val image: String?,
    val openDate: String,
    val maxViewers: Int,
    val isBlurred: Boolean,
    val cost: Int
)