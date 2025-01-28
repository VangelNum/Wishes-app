package com.vangelnum.wisher.features.keylogshistory.data.model

import com.vangelnum.wisher.features.home.User

data class KeyLogsHistory(
    val id: Int,
    val key: String,
    val viewedAt: String,
    val viewer: User
)