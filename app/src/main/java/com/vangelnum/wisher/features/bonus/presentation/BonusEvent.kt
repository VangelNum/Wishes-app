package com.vangelnum.wisher.features.bonus.presentation

sealed class BonusEvent {
    data object OnGetBonusInfo: BonusEvent()
    data object OnClaimBonus: BonusEvent()
    data object OnBackToEmptyState: BonusEvent()
    data object OnClaimAdReward: BonusEvent()
}