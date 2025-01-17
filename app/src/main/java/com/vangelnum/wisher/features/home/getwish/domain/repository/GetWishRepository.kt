package com.vangelnum.wisher.features.home.getwish.domain.repository

import com.vangelnum.wisher.features.home.getwish.data.model.GetWishResponse

interface GetWishRepository {
    suspend fun getWishesByKey(key: String): List<GetWishResponse>
}