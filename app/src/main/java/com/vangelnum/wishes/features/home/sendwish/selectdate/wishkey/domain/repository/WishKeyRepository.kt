package com.vangelnum.wishes.features.home.sendwish.selectdate.wishkey.domain.repository

import com.vangelnum.wishes.core.data.UiState
import com.vangelnum.wishes.features.home.sendwish.selectdate.wishkey.data.model.WishKey
import kotlinx.coroutines.flow.Flow

interface WishKeyRepository {
    fun getWishKey(): Flow<UiState<WishKey>>
    fun regenerateWishKey(): Flow<UiState<WishKey>>
}