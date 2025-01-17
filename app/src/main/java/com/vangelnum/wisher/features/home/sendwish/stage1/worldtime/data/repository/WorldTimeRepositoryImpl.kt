package com.vangelnum.wisher.features.home.sendwish.stage1.worldtime.data.repository

import com.vangelnum.wisher.features.home.sendwish.stage1.worldtime.data.api.WorldTimeApi
import com.vangelnum.wisher.features.home.sendwish.stage1.worldtime.data.model.DateInfo
import com.vangelnum.wisher.features.home.sendwish.stage1.worldtime.domain.repository.WorldTimeRepository
import javax.inject.Inject

class WorldTimeRepositoryImpl @Inject constructor(
    private val worldTimeApi: WorldTimeApi
) : WorldTimeRepository {
    override suspend fun getCurrentDate(): DateInfo {
        return worldTimeApi.getCurrentDate()
    }
}