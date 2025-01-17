package com.vangelnum.wisher.features.home.sendwish.stage3.data.model

data class SendWishRequest(
    val text: String,
    val wishDate: String,
    val openDate: String,
    val image: String,
    val maxViewers: Int?,
    val isBlurred: Boolean,
    val cost: Int
)