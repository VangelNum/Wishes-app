package com.vangelnum.wisher.features.home.sendwish.stage1.wishkey.data.repository

import com.vangelnum.wisher.core.data.UiState
import com.vangelnum.wisher.features.home.sendwish.stage1.wishkey.data.api.WishKeyApi
import com.vangelnum.wisher.features.home.sendwish.stage1.wishkey.data.model.WishKey
import com.vangelnum.wisher.features.home.sendwish.stage1.wishkey.domain.repository.WishKeyRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import javax.inject.Inject

class WishKeyKeyRepositoryImpl @Inject constructor(
    private val api: WishKeyApi
) : WishKeyRepository {
    override fun getWishKey(): Flow<UiState<WishKey>> = flow {
        emit(UiState.Loading())
        try {
            val response = api.getWishKey()
            if (response.isSuccessful && response.body() != null) {
                emit(UiState.Success(response.body()!!))
            } else {
                val generatedKey = api.generateWishKey()
                emit(UiState.Success(generatedKey))
            }
        } catch (e: HttpException) {
            emit(UiState.Error(e.message ?: "HTTP Error"))
        } catch (e: Exception) {
            emit(UiState.Error("An unexpected error occurred"))
        }
    }

    override fun regenerateWishKey(): Flow<UiState<WishKey>> = flow {
        emit(UiState.Loading())
        try {
            val response = api.regenerateWishKey()
            emit(UiState.Success(response))
        } catch (e: HttpException) {
            emit(UiState.Error(e.message ?: "HTTP Error"))
        } catch (e: Exception) {
            emit(UiState.Error("An unexpected error occurred"))
        }
    }
}