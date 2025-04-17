package com.vangelnum.wishes.features.userwishsendinghistory.domain.repository

import com.vangelnum.wishes.core.data.UiState
import com.vangelnum.wishes.features.home.getwish.data.model.Wish
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

interface UserWishesHistoryRepository {
    fun getMyWishes(): Flow<UiState<List<Wish>>>
    suspend fun deleteWish(id: Int): Response<Unit>
}