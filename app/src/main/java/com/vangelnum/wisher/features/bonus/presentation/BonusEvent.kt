package com.vangelnum.wisher.features.bonus.presentation

sealed class BonusEvent {
    data object OnGetBonusInfo: BonusEvent()
    data object OnClaimBonus: BonusEvent()
}