package com.vangelnum.wisher.features.home.sendwish.stage1.wishkey.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vangelnum.wisher.core.data.UiState
import com.vangelnum.wisher.features.home.sendwish.stage1.wishkey.data.model.WishKey
import com.vangelnum.wisher.features.home.sendwish.stage1.wishkey.domain.repository.WishKeyRepository
import com.vangelnum.wisher.features.home.sendwish.stage1.worldtime.data.model.DateInfo
import com.vangelnum.wisher.features.home.sendwish.stage1.worldtime.domain.repository.WorldTimeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WishKeyViewModel @Inject constructor(
    private val wishKeyRepository: WishKeyRepository,
    private val worldTimeRepository: WorldTimeRepository
) : ViewModel() {
    private val _homeUiState = MutableStateFlow<UiState<WishKey>>(UiState.Loading)
    val homeKeyUiState = _homeUiState.asStateFlow()

    private val _dateUiState = MutableStateFlow<UiState<DateInfo>>(UiState.Idle)
    val dateUiState = _dateUiState.asStateFlow()

    fun onEvent(event: WishKeyEvent) {
        when (event) {
            WishKeyEvent.OnGetWishKeyKey -> getWishKey()
            WishKeyEvent.OnGetDate -> getDate()
            WishKeyEvent.OnGenerateWishKeyKey -> generateWishKey()
        }
    }

    private fun getDate() {
        viewModelScope.launch {
            _dateUiState.value = UiState.Loading
            try {
                val timeInfo = worldTimeRepository.getCurrentDate()
                _dateUiState.value = UiState.Success(timeInfo)
            } catch (e: Exception) {
                _dateUiState.value =
                    UiState.Error(e.localizedMessage ?: "Failed to fetch time")
            }
        }
    }

    private fun getWishKey() {
        viewModelScope.launch {
            _homeUiState.value = UiState.Loading
            try {
                val key = wishKeyRepository.getWishKey()
                if (key == null) generateWishKey() else {
                    _homeUiState.value = UiState.Success(key)
                }
            } catch (e: Exception) {
                _homeUiState.value =
                    UiState.Error(e.localizedMessage ?: "An unexpected error occurred")
            }
        }
    }

    private fun generateWishKey() {
        viewModelScope.launch {
            _homeUiState.value = UiState.Loading
            try {
                val newKey = wishKeyRepository.generateWishKey()
                _homeUiState.value = UiState.Success(newKey)
            } catch (e: Exception) {
                _homeUiState.value =
                    UiState.Error(e.localizedMessage ?: "Failed to generate wish key")
            }
        }
    }
}