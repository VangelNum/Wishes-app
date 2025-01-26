package com.vangelnum.wisher.features.userwisheshistory.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vangelnum.wisher.core.data.UiState
import com.vangelnum.wisher.features.home.getwish.data.model.Wish
import com.vangelnum.wisher.features.userwisheshistory.domain.repository.UserWishesHistoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserWishesHistoryViewModel @Inject constructor(
    private val repository: UserWishesHistoryRepository
) : ViewModel() {

    private val _mySendingWishesState = MutableStateFlow<UiState<List<Wish>>>(UiState.Idle())
    val mySendingWishesState = _mySendingWishesState.asStateFlow()

    init {
        onEvent(UserWishesHistoryEvent.OnGetMyWishes)
    }

    fun onEvent(event: UserWishesHistoryEvent) {
        when (event) {
            UserWishesHistoryEvent.OnGetMyWishes -> getSendingMyWishes()
        }
    }

    private fun getSendingMyWishes() {
        viewModelScope.launch {
            repository.getMyWishes().collect { state ->
                _mySendingWishesState.value = state
            }
        }
    }
}