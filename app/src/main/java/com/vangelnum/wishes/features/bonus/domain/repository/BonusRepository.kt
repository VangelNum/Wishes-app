package com.vangelnum.wishes.features.bonus.domain.repository

import com.vangelnum.wishes.core.data.UiState
import com.vangelnum.wishes.features.bonus.data.model.AdRewardInfo
import com.vangelnum.wishes.features.bonus.data.model.BonusInfo
import com.vangelnum.wishes.features.bonus.data.model.ClaimBonusInfo
import kotlinx.coroutines.flow.Flow

interface BonusRepository {
    fun getBonusInfo(): Flow<UiState<BonusInfo>>
    fun claimDailyBonus(): Flow<UiState<ClaimBonusInfo>>
    fun claimAdReward(): Flow<UiState<AdRewardInfo>>
    fun getAdRewardCooldownInfo(): Flow<UiState<Long>>
}