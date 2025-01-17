package com.vangelnum.wisher.features.home.sendwish.stage1.wishkey.data.model

import com.vangelnum.wisher.features.home.User

data class WishKey(
    val id: Int,
    val key: String,
    val user: User
)