package com.vangelnum.wisher.features.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vangelnum.wisher.core.data.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val homeRepository: HomeRepository
): ViewModel() {
    private val _homeUiState = MutableStateFlow<UiState<WishKey>>(UiState.Loading)
    val homeKeyUiState = _homeUiState.asStateFlow()
    
    fun onEvent(event: HomeEvent) {
        when (event) {
            HomeEvent.onGetWishKey -> getWishKey()
        }
    }

    private fun getWishKey() {
        viewModelScope.launch {
            delay(5000L)
            try {
                _homeUiState.value = UiState.Success(homeRepository.getWishKey())
            } catch (e: Exception) {
                _homeUiState.value = UiState.Error(e.localizedMessage ?: "An unexpected error occurred")
            }
        }
    }
}