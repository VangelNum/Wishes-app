package com.vangelnum.wisher.features.home.sendwish.stage1.worldtime.domain.repository

import com.vangelnum.wisher.features.home.sendwish.stage1.worldtime.data.model.DateInfo

interface WorldTimeRepository {
    suspend fun getCurrentDate(): DateInfo
}