package com.vangelnum.wisher.features.home.sendwish.stage1.wishkey.data.repository

import com.vangelnum.wisher.features.home.sendwish.stage1.wishkey.data.api.WishKeyService
import com.vangelnum.wisher.features.home.sendwish.stage1.wishkey.data.model.WishKey
import com.vangelnum.wisher.features.home.sendwish.stage1.wishkey.domain.repository.WishKeyRepository
import javax.inject.Inject

class WishKeyKeyRepositoryImpl @Inject constructor(
    private val api: WishKeyService
) : WishKeyRepository {
    override suspend fun getWishKey(): WishKey? {
        return try {
            api.getWishKey()
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun generateWishKey(): WishKey {
        return api.generateWishKey()
    }
}