package com.vangelnum.wishes.features.home.sendwish.createwish.presentation

import com.vangelnum.wishes.core.data.UiState
import com.vangelnum.wishes.features.home.getwish.data.model.Wish

data class SendWishUiState(
    val modelsListState: UiState<List<String>> = UiState.Idle(),
    val sendWishState: UiState<Wish> = UiState.Idle(),
    val uploadImageState: UiState<String> = UiState.Idle(),
    val generateImageState: UiState<String> = UiState.Idle(),
    val generateTextState: UiState<String> = UiState.Idle(),
    val numberOfWishes: Long? = null
)