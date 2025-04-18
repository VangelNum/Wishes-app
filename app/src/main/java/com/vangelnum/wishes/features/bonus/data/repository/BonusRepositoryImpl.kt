package com.vangelnum.wishes.features.bonus.data.repository

import com.vangelnum.wishes.core.data.UiState
import com.vangelnum.wishes.core.utils.ErrorParser
import com.vangelnum.wishes.features.bonus.data.api.BonusApi
import com.vangelnum.wishes.features.bonus.data.model.AdRewardInfo
import com.vangelnum.wishes.features.bonus.data.model.BonusInfo
import com.vangelnum.wishes.features.bonus.data.model.ClaimBonusInfo
import com.vangelnum.wishes.features.bonus.domain.repository.BonusRepository
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class BonusRepositoryImpl @Inject constructor(
    private val api: BonusApi,
    private val errorParser: ErrorParser
) : BonusRepository {
    override fun getBonusInfo(): Flow<UiState<BonusInfo>> = flow {
        emit(UiState.Loading())
        try {
            val response = api.getDailyBonusInfo()
            emit(UiState.Success(response))
        } catch (e: Exception) {
            emit(UiState.Error(errorParser.parseError(e)))
        }
    }

    override fun claimDailyBonus(): Flow<UiState<ClaimBonusInfo>> = flow {
        emit(UiState.Loading())
        try {
            val response = api.claimDailyBonus()
            emit(UiState.Success(response))
        } catch (e: Exception) {
            emit(UiState.Error(errorParser.parseError(e)))
        }
    }

    override fun claimAdReward(): Flow<UiState<AdRewardInfo>> = flow {
        emit(UiState.Loading())
        try {
            val response = api.claimAdReward()
            emit(UiState.Success(response))
        } catch (e: Exception) {
            emit(UiState.Error(errorParser.parseError(e)))
        }
    }

    override fun getAdRewardCooldownInfo(): Flow<UiState<Long>> = flow {
        emit(UiState.Loading())
        try {
            val response = api.getAdRewardCooldownInfo()
            emit(UiState.Success(response))
        } catch (e: Exception) {
            emit(UiState.Error(errorParser.parseError(e)))
        }
    }
}