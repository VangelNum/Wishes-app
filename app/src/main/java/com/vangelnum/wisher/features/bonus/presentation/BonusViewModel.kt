package com.vangelnum.wisher.features.bonus.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vangelnum.wisher.core.data.UiState
import com.vangelnum.wisher.features.bonus.data.model.AdRewardInfo
import com.vangelnum.wisher.features.bonus.data.model.BonusInfo
import com.vangelnum.wisher.features.bonus.data.model.ClaimBonusInfo
import com.vangelnum.wisher.features.bonus.domain.repository.BonusRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class BonusViewModel @Inject constructor(
    private val bonusRepository: BonusRepository
) : ViewModel() {
    private val _bonusUiState = MutableStateFlow<UiState<BonusInfo>>(UiState.Idle())
    val bonusUiState = _bonusUiState.asStateFlow()

    private val _claimBonusUiState = MutableStateFlow<UiState<ClaimBonusInfo>>(UiState.Idle())
    val claimBonusUiState = _claimBonusUiState.asStateFlow()

    private val _claimAdRewardUiState = MutableStateFlow<UiState<AdRewardInfo>>(UiState.Idle())
    val claimAdRewardUiState = _claimAdRewardUiState.asStateFlow()

    init {
        getBonusInfo()
    }

    fun onEvent(event: BonusEvent) {
        when (event) {
            BonusEvent.OnClaimBonus -> claimBonus()
            BonusEvent.OnGetBonusInfo -> getBonusInfo()
            BonusEvent.OnBackToEmptyState -> backToEmptyState()
            BonusEvent.OnClaimAdReward -> claimAdReward()
        }
    }

    private fun claimAdReward() {
        viewModelScope.launch {
            bonusRepository.claimAdReward().collect { state->
                _claimAdRewardUiState.update { state }
            }
        }
    }

    private fun backToEmptyState() {
        _claimBonusUiState.value = UiState.Idle()
        _claimAdRewardUiState.value = UiState.Idle()
    }

    private fun getBonusInfo() {
        viewModelScope.launch {
            bonusRepository.getBonusInfo().collect { state ->
                _bonusUiState.update { state }
            }
        }
    }

    private fun claimBonus() {
        viewModelScope.launch {
            bonusRepository.claimDailyBonus().collect { state ->
                _claimBonusUiState.update { state }
            }
        }
    }
}