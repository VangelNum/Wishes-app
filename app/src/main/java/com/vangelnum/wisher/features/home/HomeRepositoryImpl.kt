package com.vangelnum.wisher.features.home

import javax.inject.Inject

class HomeRepositoryImpl @Inject constructor(
    private val api: HomeApi
): HomeRepository {
    override suspend fun getWishKey(): WishKey {
        return api.getWishKey()
    }
}