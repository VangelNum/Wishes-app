package com.vangelnum.wisher.features.home

interface HomeRepository {
    suspend fun getWishKey(): WishKey
}