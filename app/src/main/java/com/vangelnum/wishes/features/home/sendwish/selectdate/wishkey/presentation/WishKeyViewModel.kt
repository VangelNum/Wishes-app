package com.vangelnum.wishes.features.home.sendwish.selectdate.wishkey.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vangelnum.wishes.core.data.UiState
import com.vangelnum.wishes.features.home.sendwish.selectdate.wishkey.data.model.WishKey
import com.vangelnum.wishes.features.home.sendwish.selectdate.wishkey.domain.repository.WishKeyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WishKeyViewModel @Inject constructor(
    private val wishKeyRepository: WishKeyRepository
) : ViewModel() {
    private val _keyUiState = MutableStateFlow<UiState<WishKey>>(UiState.Loading())
    val keyUiState = _keyUiState.asStateFlow()

    init {
        onEvent(WishKeyEvent.OnGetWishKey)
    }

    fun onEvent(event: WishKeyEvent) {
        when (event) {
            WishKeyEvent.OnGetWishKey -> getWishKey()
            WishKeyEvent.OnRegenerateWishKey -> regenerateWishKey()
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