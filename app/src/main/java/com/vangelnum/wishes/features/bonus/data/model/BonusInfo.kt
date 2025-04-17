package com.vangelnum.wishes.features.bonus.data.model

data class BonusInfo(
    val currentStreak: Int,
    val nextBonusCoins: Int,
    val remainingHours: Int,
    val remainingMinutes: Int
)