package com.vangelnum.wisher.features.home.sendwish.stage1.wishkey.domain.repository

import com.vangelnum.wisher.features.home.sendwish.stage1.wishkey.data.model.WishKey

interface WishKeyRepository {
    suspend fun getWishKey(): WishKey?
    suspend fun generateWishKey(): WishKey
}