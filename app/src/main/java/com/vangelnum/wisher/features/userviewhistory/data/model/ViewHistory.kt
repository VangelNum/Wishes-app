package com.vangelnum.wisher.features.userviewhistory.data.model

import com.vangelnum.wisher.features.home.User
import com.vangelnum.wisher.features.home.getwish.data.model.Wish

data class ViewHistory(
    val id: Int,
    val viewTime: String,
    val viewer: User,
    val wish: Wish,
    val wishOwner: User
)