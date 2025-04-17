package com.vangelnum.wishes.features.userwishviewhistory.data.model

import com.vangelnum.wishes.features.auth.core.model.User
import com.vangelnum.wishes.features.home.getwish.data.model.Wish

data class ViewHistory(
    val id: Int,
    val viewTime: String,
    val viewer: User,
    val wish: Wish,
    val wishOwner: User
)