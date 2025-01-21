package com.vangelnum.wisher.features.home.getwish.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vangelnum.wisher.core.data.UiState
import com.vangelnum.wisher.features.home.getwish.data.model.WishDatesInfo
import com.vangelnum.wisher.features.home.getwish.data.model.WishResponse
import com.vangelnum.wisher.features.home.getwish.domain.repository.GetWishRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GetWishViewModel @Inject constructor(
    private val wishRepository: GetWishRepository
) : ViewModel() {

    private val _wishesDatesState = MutableStateFlow<UiState<List<WishDatesInfo>>>(UiState.Idle())
    val wishesDatesState = _wishesDatesState.asStateFlow()

    private val _wishesState = MutableStateFlow<UiState<WishResponse>>(UiState.Idle())
    val wishesState = _wishesState.asStateFlow()

    fun onEvent(event: GetWishEvent) {
        when (event) {
            is GetWishEvent.OnGetWishesDates -> getWishesDates(event.key)
            is GetWishEvent.OnGetWishes -> getWishes(event.key, event.id)
        }
    }

    private fun getWishes(key: String, id: Int) {
        viewModelScope.launch {
            wishRepository.getWishes(key, id).collectLatest { state ->
                _wishesState.update { state }
            }
        }
    }

    private fun getWishesDates(key: String) {
        viewModelScope.launch {
            wishRepository.getDatesByKey(key).collectLatest { state ->
                _wishesDatesState.update { state }
            }
        }
    }
}