package com.vangelnum.wisher.presentation

sealed class UiEvent {
    object Success : UiEvent()
    data class Failure(val message: String) : UiEvent()
}