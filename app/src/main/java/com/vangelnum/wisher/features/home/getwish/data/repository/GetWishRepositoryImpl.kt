package com.vangelnum.wisher.features.home.getwish.data.repository

import com.vangelnum.wisher.features.home.getwish.data.api.GetWishApi
import com.vangelnum.wisher.features.home.getwish.data.model.GetWishResponse
import com.vangelnum.wisher.features.home.getwish.domain.repository.GetWishRepository
import javax.inject.Inject

class GetWishRepositoryImpl @Inject constructor(
    private val api: GetWishApi
) : GetWishRepository {
    override suspend fun getWishesByKey(key: String): List<GetWishResponse> {
        return api.getWishesByKey(key)
    }
}