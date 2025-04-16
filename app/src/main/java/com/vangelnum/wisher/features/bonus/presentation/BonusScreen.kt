package com.vangelnum.wisher.features.bonus.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vangelnum.wisher.R
import com.vangelnum.wisher.core.data.UiState
import com.vangelnum.wisher.core.presentation.ErrorScreen
import com.vangelnum.wisher.core.presentation.LoadingScreen
import com.vangelnum.wisher.core.presentation.SmallLoadingIndicator
import com.vangelnum.wisher.core.presentation.SnackbarController
import com.vangelnum.wisher.core.presentation.SnackbarEvent
import com.vangelnum.wisher.core.utils.string
import com.vangelnum.wisher.features.bonus.data.model.BonusInfo
import com.vangelnum.wisher.features.bonus.data.model.ClaimBonusInfo
import kotlinx.coroutines.delay

@Composable
fun BonusScreen(
    bonusUiState: UiState<BonusInfo>,
    claimUiState: UiState<ClaimBonusInfo>,
    onClaimBonus: () -> Unit,
    onGetBonusInfo: () -> Unit
) {
    when (bonusUiState) {
        is UiState.Error -> {
            ErrorScreen(bonusUiState.message)
        }

        is UiState.Idle -> {}
        is UiState.Loading -> {
            LoadingScreen()
        }

        is UiState.Success -> {
            BonusContent(
                bonusInfo = bonusUiState.data,
                onClaimBonus = onClaimBonus,
                claimUiState = claimUiState,
                onGetBonusInfo = onGetBonusInfo
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun BonusContent(
    bonusInfo: BonusInfo,
    onClaimBonus: () -> Unit,
    claimUiState: UiState<ClaimBonusInfo>,
    onGetBonusInfo: () -> Unit
) {
    val filledColor = MaterialTheme.colorScheme.primary
    val emptyColor = Color.Gray.copy(alpha = 0.3f)

    val isClaimable = bonusInfo.remainingHours == 0 && bonusInfo.remainingMinutes == 0
    val isButtonEnabled = isClaimable && claimUiState !is UiState.Loading

    val context = LocalContext.current

    LaunchedEffect(claimUiState) {
        when (claimUiState) {
            is UiState.Error -> {
                SnackbarController.sendEvent(SnackbarEvent(claimUiState.message))
            }

            is UiState.Success -> {
                val coinsString = context.getString(R.string.coins)
                SnackbarController.sendEvent(
                    SnackbarEvent(
                        string(
                            context,
                            R.string.bonus_claimed_success,
                            claimUiState.data.coinsAwarded,
                            coinsString
                        )
                    )
                )
                delay(2000)
                onGetBonusInfo()
            }

            else -> {}
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.daily_bonus_streak),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            val maxStreak = 10
            repeat(maxStreak) { index ->
                val dayNumber = index + 1
                val isFilled =
                    dayNumber <= bonusInfo.currentStreak % maxStreak || (bonusInfo.currentStreak > 0 && bonusInfo.currentStreak % maxStreak == 0 && dayNumber == maxStreak)

                Box(
                    modifier = Modifier
                        .size(45.dp)
                        .clip(CircleShape)
                        .background(if (isFilled) filledColor else emptyColor),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "$dayNumber",
                        color = if (isFilled) MaterialTheme.colorScheme.onPrimary else Color.Gray,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(24.dp))


        ElevatedCard(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(
                text = stringResource(R.string.next_bonus, bonusInfo.nextBonusCoins, stringResource(R.string.coins)),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.fillMaxWidth().padding(start = 16.dp, top = 16.dp, end = 16.dp),
                textAlign = TextAlign.Center
            )
            val timeRemainingText = remember(bonusInfo.remainingHours, bonusInfo.remainingMinutes) {
                when {
                    isClaimable -> string(context, R.string.bonus_ready_to_claim)
                    bonusInfo.remainingHours > 0 -> string(
                        context,
                        R.string.claim_in_hours_minutes,
                        bonusInfo.remainingHours,
                        bonusInfo.remainingMinutes
                    )
                    else -> string(context, R.string.claim_in_minutes, bonusInfo.remainingMinutes)
                }
            }
            Text(
                text = timeRemainingText,
                style = MaterialTheme.typography.bodyMedium,
                color = if (isClaimable) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                textAlign = TextAlign.Center
            )
        }
        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onClaimBonus,
            enabled = isButtonEnabled,
            modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = OutlinedTextFieldDefaults.MinHeight)
        ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.height(24.dp)) {
                if (claimUiState is UiState.Loading) {
                    SmallLoadingIndicator()
                } else {
                    Text(stringResource(R.string.claim_reward))
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (claimUiState is UiState.Success) {
            Text(
                text = stringResource(
                    R.string.bonus_received_coins,
                    claimUiState.data.coinsAwarded,
                    stringResource(R.string.coins)
                ),
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF4CAF50),
                textAlign = TextAlign.Center
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewBonusContent() {
    BonusContent(
        bonusInfo = BonusInfo(
            currentStreak = 5,
            nextBonusCoins = 100,
            remainingHours = 23,
            remainingMinutes = 59
        ),
        claimUiState = UiState.Idle(),
        onClaimBonus = {},
        onGetBonusInfo = {}
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewBonusContentLoading() {
    BonusContent(
        bonusInfo = BonusInfo(
            currentStreak = 3,
            nextBonusCoins = 50,
            remainingHours = 10,
            remainingMinutes = 30
        ),
        claimUiState = UiState.Loading(),
        onClaimBonus = {},
        onGetBonusInfo = {}
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewBonusContentClaimed() {
    BonusContent(
        bonusInfo = BonusInfo(
            currentStreak = 7,
            nextBonusCoins = 150,
            remainingHours = 1,
            remainingMinutes = 15
        ),
        claimUiState = UiState.Success(ClaimBonusInfo(coinsAwarded = 100, nextBonusCoins = 100, currentStreak = 5)),
        onClaimBonus = {},
        onGetBonusInfo = {}
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewBonusContentError() {
    BonusContent(
        bonusInfo = BonusInfo(
            currentStreak = 2,
            nextBonusCoins = 25,
            remainingHours = 5,
            remainingMinutes = 0
        ),
        claimUiState = UiState.Error(stringResource(R.string.bonus_claim_failed)),
        onClaimBonus = {},
        onGetBonusInfo = {}
    )
}