package com.vangelnum.wishes.features.bonus.data.api

import com.vangelnum.wishes.features.bonus.data.model.AdRewardInfo
import com.vangelnum.wishes.features.bonus.data.model.BonusInfo
import com.vangelnum.wishes.features.bonus.data.model.ClaimBonusInfo
import retrofit2.http.GET
import retrofit2.http.POST

interface BonusApi {
    @GET("/api/v1/user/daily-bonus-state")
    suspend fun getDailyBonusInfo(): BonusInfo

    @POST("/api/v1/user/daily-login-bonus")
    suspend fun claimDailyBonus(): ClaimBonusInfo

    @GET("/api/v1/user/ad-cooldown")
    suspend fun getAdRewardCooldownInfo(): Long
    @POST("/api/v1/user/claim-ad-reward")
    suspend fun claimAdReward(): AdRewardInfo
}