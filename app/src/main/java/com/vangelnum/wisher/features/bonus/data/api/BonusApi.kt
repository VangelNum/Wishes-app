package com.vangelnum.wisher.features.bonus.data.api

import com.vangelnum.wisher.features.bonus.data.model.BonusInfo
import com.vangelnum.wisher.features.bonus.data.model.ClaimBonusInfo
import retrofit2.http.GET
import retrofit2.http.POST

interface BonusApi {
    @GET("/api/v1/user/daily-bonus-state")
    suspend fun getDailyBonusInfo(): BonusInfo

    @POST("/api/v1/user/daily-login-bonus")
    suspend fun claimDailyBonus(): ClaimBonusInfo
}