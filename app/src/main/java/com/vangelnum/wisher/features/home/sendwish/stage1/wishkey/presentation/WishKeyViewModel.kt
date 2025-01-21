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
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WishKeyViewModel @Inject constructor(
    private val wishKeyRepository: WishKeyRepository,
    private val worldTimeRepository: WorldTimeRepository
) : ViewModel() {
    private val _keyUiState = MutableStateFlow<UiState<WishKey>>(UiState.Loading())
    val keyUiState = _keyUiState.asStateFlow()

    private val _dateUiState = MutableStateFlow<UiState<DateInfo>>(UiState.Idle())
    val dateUiState = _dateUiState.asStateFlow()

    fun onEvent(event: WishKeyEvent) {
        when (event) {
            WishKeyEvent.OnGetWishKeyKey -> getWishKey()
            WishKeyEvent.OnGetDate -> getDate()
            WishKeyEvent.OnRegenerateWishKey -> regenerateWishKey()
        }
    }

    private fun getDate() {
        viewModelScope.launch {
            worldTimeRepository.getCurrentDate().collectLatest { state ->
                _dateUiState.update {
                    state
                }
            }
        }
    }

    private fun getWishKey() {
        viewModelScope.launch {
            wishKeyRepository.getWishKey().collectLatest { state ->
                _keyUiState.update { state }
            }
        }
    }

    private fun regenerateWishKey() {
        viewModelScope.launch {
            wishKeyRepository.regenerateWishKey().collectLatest { state ->
                _keyUiState.update {
                    state
                }
            }
        }
    }
}