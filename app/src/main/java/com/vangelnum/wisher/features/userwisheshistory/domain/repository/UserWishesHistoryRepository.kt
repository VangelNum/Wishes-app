package com.vangelnum.wisher.features.userwisheshistory.domain.repository

import com.vangelnum.wisher.core.data.UiState
import com.vangelnum.wisher.features.home.getwish.data.model.Wish
import kotlinx.coroutines.flow.Flow

interface UserWishesHistoryRepository {
    fun getMyWishes(): Flow<UiState<List<Wish>>>
}