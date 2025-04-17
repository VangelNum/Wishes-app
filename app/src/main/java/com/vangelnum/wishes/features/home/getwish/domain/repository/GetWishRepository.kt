package com.vangelnum.wishes.features.home.getwish.domain.repository

import com.vangelnum.wishes.core.data.UiState
import com.vangelnum.wishes.features.home.getwish.data.model.Wish
import com.vangelnum.wishes.features.home.getwish.data.model.WishDatesInfo
import kotlinx.coroutines.flow.Flow

interface GetWishRepository {
    fun getDatesByKey(key: String): Flow<UiState<List<WishDatesInfo>>>
    fun getWishes(key: String, id: Int): Flow<UiState<Wish>>
    fun getLastWishByKey(key: String): Flow<UiState<Wish>>
}