package com.vangelnum.wisher.features.home.getwish.data.repository

import com.vangelnum.wisher.core.data.UiState
import com.vangelnum.wisher.features.home.getwish.data.api.GetWishApi
import com.vangelnum.wisher.features.home.getwish.data.model.WishDatesInfo
import com.vangelnum.wisher.features.home.getwish.data.model.WishResponse
import com.vangelnum.wisher.features.home.getwish.domain.repository.GetWishRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import javax.inject.Inject

class GetWishRepositoryImpl @Inject constructor(
    private val api: GetWishApi
) : GetWishRepository {

    override fun getDatesByKey(key: String): Flow<UiState<List<WishDatesInfo>>> = flow {
        emit(UiState.Loading())
        try {
            val response = api.getWishesDatesByKey(key)
            emit(UiState.Success(response))
        } catch (e: HttpException) {
            emit(UiState.Error(e.message() ?: "HTTP Error"))
        } catch (e: Exception) {
            emit(UiState.Error("An unexpected error occurred"))
        }
    }

    override fun getWishes(key: String, id: Int): Flow<UiState<WishResponse>> = flow {
        emit(UiState.Loading())
        try {
            val response = api.getWishes(key, id)
            emit(UiState.Success(response))
        } catch (e: HttpException) {
            emit(UiState.Error(e.message() ?: "HTTP Error"))
        } catch (e: Exception) {
            emit(UiState.Error("An unexpected error occurred"))
        }
    }
}