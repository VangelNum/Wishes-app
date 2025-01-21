package com.vangelnum.wisher.features.home.sendwish.stage3.presentation

import com.vangelnum.wisher.core.data.UiState

data class SendWishUiState(
    val modelsListState: UiState<List<String>> = UiState.Idle(),
    val sendWishState: UiState<Unit> = UiState.Idle(),
    val uploadImageState: UiState<String> = UiState.Idle(),
    val generateImageState: UiState<String> = UiState.Idle(),
    val generateTextState: UiState<String> = UiState.Idle(),
)