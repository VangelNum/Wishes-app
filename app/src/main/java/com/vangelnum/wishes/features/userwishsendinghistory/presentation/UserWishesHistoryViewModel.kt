package com.vangelnum.wishes.features.userwishsendinghistory.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vangelnum.wishes.core.data.UiState
import com.vangelnum.wishes.features.home.getwish.data.model.Wish
import com.vangelnum.wishes.features.userwishsendinghistory.domain.repository.UserWishesHistoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserWishesHistoryViewModel @Inject constructor(
    private val userWishesHistoryRepository: UserWishesHistoryRepository
) : ViewModel() {

    private val _mySendingWishesState = MutableStateFlow<UiState<List<Wish>>>(UiState.Idle())
    val mySendingWishesState = _mySendingWishesState.asStateFlow()

    init {
        onEvent(UserWishesHistoryEvent.OnGetMyWishes)
    }

    fun onEvent(event: UserWishesHistoryEvent) {
        when (event) {
            UserWishesHistoryEvent.OnGetMyWishes -> getSendingMyWishes()
            is UserWishesHistoryEvent.OnDeleteWish -> deleteWish(event.id)
        }
    }

    private fun deleteWish(id: Int) {
        viewModelScope.launch {
            if (userWishesHistoryRepository.deleteWish(id).isSuccessful) {
                getSendingMyWishes()
            }
        }
    }

    private fun getSendingMyWishes() {
        viewModelScope.launch {
            userWishesHistoryRepository.getMyWishes().collect { state ->
                _mySendingWishesState.value = state
            }
        }
    }
}