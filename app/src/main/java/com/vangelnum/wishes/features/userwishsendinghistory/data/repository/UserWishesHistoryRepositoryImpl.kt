package com.vangelnum.wishes.features.userwishsendinghistory.data.repository

import com.vangelnum.wishes.core.data.UiState
import com.vangelnum.wishes.core.utils.ErrorParser
import com.vangelnum.wishes.features.home.getwish.data.model.Wish
import com.vangelnum.wishes.features.userwishsendinghistory.data.api.UserWishesHistoryApi
import com.vangelnum.wishes.features.userwishsendinghistory.domain.repository.UserWishesHistoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Response
import javax.inject.Inject

class UserWishesHistoryRepositoryImpl @Inject constructor(
    private val api: UserWishesHistoryApi,
    private val errorParser: ErrorParser
) : UserWishesHistoryRepository {
    override fun getMyWishes(): Flow<UiState<List<Wish>>> = flow {
        emit(UiState.Loading())
        try {
            val response = api.getMyWishes()
            emit(UiState.Success(response))
        } catch (e: Exception) {
            emit(UiState.Error(errorParser.parseError(e)))
        }
    }

    override suspend fun deleteWish(id: Int): Response<Unit> {
        return api.deleteWish(id)
    }
}