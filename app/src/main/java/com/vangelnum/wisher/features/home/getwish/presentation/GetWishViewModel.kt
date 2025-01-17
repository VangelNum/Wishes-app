package com.vangelnum.wisher.features.home.getwish.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vangelnum.wisher.core.data.UiState
import com.vangelnum.wisher.features.home.getwish.data.model.GetWishResponse
import com.vangelnum.wisher.features.home.getwish.domain.repository.GetWishRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GetWishViewModel @Inject constructor(
    private val wishRepository: GetWishRepository
) : ViewModel() {

    private val _wishesState = MutableStateFlow<UiState<List<GetWishResponse>>>(UiState.Idle)
    val wishesState = _wishesState.asStateFlow()

    fun getWishes(key: String) {
        viewModelScope.launch {
            _wishesState.value = UiState.Loading
            try {
                val wishes = wishRepository.getWishesByKey(key)
                _wishesState.value = UiState.Success(wishes)
            } catch (e: Exception) {
                _wishesState.value = UiState.Error(e.localizedMessage ?: "An unexpected error occurred")
            }
        }
    }
}