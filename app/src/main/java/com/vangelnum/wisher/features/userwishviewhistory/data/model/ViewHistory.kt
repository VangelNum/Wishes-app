package com.vangelnum.wisher.features.userwishviewhistory.data.model

import com.vangelnum.wisher.features.auth.core.model.User
import com.vangelnum.wisher.features.home.getwish.data.model.Wish

data class ViewHistory(
    val id: Int,
    val viewTime: String,
    val viewer: User,
    val wish: Wish,
    val wishOwner: User
)