package com.vangelnum.wishes.features.bonus.data.model

data class ClaimBonusInfo(
    val coinsAwarded: Int,
    val currentStreak: Int,
    val nextBonusCoins: Int
)