package com.vangelnum.wisher.features.home.sendwish.createwish.presentation

import com.vangelnum.wisher.core.data.UiState
import com.vangelnum.wisher.features.home.getwish.data.model.Wish

data class SendWishUiState(
    val modelsListState: UiState<List<String>> = UiState.Idle(),
    val sendWishState: UiState<Wish> = UiState.Idle(),
    val uploadImageState: UiState<String> = UiState.Idle(),
    val generateImageState: UiState<String> = UiState.Idle(),
    val generateTextState: UiState<String> = UiState.Idle(),
    val numberOfWishes: Long? = null
)