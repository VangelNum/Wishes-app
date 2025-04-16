package com.vangelnum.wisher.features.bonus.domain.repository

import com.vangelnum.wisher.core.data.UiState
import com.vangelnum.wisher.features.bonus.data.model.BonusInfo
import com.vangelnum.wisher.features.bonus.data.model.ClaimBonusInfo
import kotlinx.coroutines.flow.Flow

interface BonusRepository {
    fun getBonusInfo(): Flow<UiState<BonusInfo>>
    fun claimDailyBonus(): Flow<UiState<ClaimBonusInfo>>
}