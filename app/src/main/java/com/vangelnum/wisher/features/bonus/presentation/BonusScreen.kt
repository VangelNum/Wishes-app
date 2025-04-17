package com.vangelnum.wisher.features.bonus.presentation

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.vangelnum.wisher.MainActivity
import com.vangelnum.wisher.R
import com.vangelnum.wisher.core.data.UiState
import com.vangelnum.wisher.core.presentation.ErrorScreen
import com.vangelnum.wisher.core.presentation.LoadingScreen
import com.vangelnum.wisher.core.presentation.SmallLoadingIndicator
import com.vangelnum.wisher.core.presentation.SnackbarController
import com.vangelnum.wisher.core.presentation.SnackbarEvent
import com.vangelnum.wisher.core.utils.string
import com.vangelnum.wisher.features.bonus.data.model.AdRewardInfo
import com.vangelnum.wisher.features.bonus.data.model.BonusInfo
import com.vangelnum.wisher.features.bonus.data.model.ClaimBonusInfo
import com.yandex.mobile.ads.common.AdError
import com.yandex.mobile.ads.common.AdRequestConfiguration
import com.yandex.mobile.ads.common.AdRequestError
import com.yandex.mobile.ads.common.ImpressionData
import com.yandex.mobile.ads.rewarded.Reward
import com.yandex.mobile.ads.rewarded.RewardedAd
import com.yandex.mobile.ads.rewarded.RewardedAdEventListener
import com.yandex.mobile.ads.rewarded.RewardedAdLoadListener
import com.yandex.mobile.ads.rewarded.RewardedAdLoader
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

@Composable
fun BonusScreen(
    bonusUiState: UiState<BonusInfo>,
    claimUiState: UiState<ClaimBonusInfo>,
    claimAdRewardUiState: UiState<AdRewardInfo>,
    onClaimBonus: () -> Unit,
    onClaimAdReward: () -> Unit
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
                claimAdRewardUiState = claimAdRewardUiState,
                onClaimAdReward = onClaimAdReward
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
    claimAdRewardUiState: UiState<AdRewardInfo>,
    onClaimAdReward: () -> Unit
) {
    val filledColor = MaterialTheme.colorScheme.primary
    val emptyColor = Color.Gray.copy(alpha = 0.3f)

    val isClaimableInitial = bonusInfo.remainingHours == 0 && bonusInfo.remainingMinutes == 0
    var isClaimable by remember { mutableStateOf(isClaimableInitial) }
    val isButtonEnabled = isClaimable && claimUiState !is UiState.Loading

    val context = LocalContext.current

    var remainingTimeInMillis by remember {
        mutableStateOf(
            TimeUnit.HOURS.toMillis(bonusInfo.remainingHours.toLong()) + TimeUnit.MINUTES.toMillis(bonusInfo.remainingMinutes.toLong())
        )
    }

    LaunchedEffect(Unit) {
        while (isActive && remainingTimeInMillis > 0) {
            delay(1000)
            remainingTimeInMillis -= 1000
            if (remainingTimeInMillis <= 0) {
                isClaimable = true
            }
        }
        if (remainingTimeInMillis <= 0) {
            isClaimable = true
        }
    }


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
            }

            else -> {}
        }
    }

    LaunchedEffect(claimAdRewardUiState) {
        when (claimAdRewardUiState) {
            is UiState.Error -> {
                SnackbarController.sendEvent(SnackbarEvent(claimAdRewardUiState.message))
            }

            is UiState.Success -> {
                val coinsString = context.getString(R.string.coins)
                SnackbarController.sendEvent(
                    SnackbarEvent(
                        string(
                            context,
                            R.string.bonus_claimed_success,
                            claimAdRewardUiState.data.coinsAwarded,
                            coinsString
                        )
                    )
                )
            }

            else -> {}
        }
    }

    val scope = rememberCoroutineScope()
    var rewardedAd: RewardedAd? by remember { mutableStateOf(null) }
    var rewardedAdLoader: RewardedAdLoader? by remember { mutableStateOf(null) }
    var isLoadingAd by remember { mutableStateOf(false) }
    var isAdAvailable by remember { mutableStateOf(true) }
    var lastAdViewTime by remember { mutableStateOf(0L) }
    val adUnitId = "R-M-15084813-1"
    val adCooldown: Long = TimeUnit.MINUTES.toMillis(1)
    var remainingAdTimeInMillis by remember { mutableStateOf(0L) }

    fun loadRewardedAd() {
        isLoadingAd = true
        rewardedAdLoader = RewardedAdLoader(context).apply {
            setAdLoadListener(object : RewardedAdLoadListener {
                override fun onAdFailedToLoad(error: AdRequestError) {
                    Log.d("YandexAds", "Ошибка загрузки $error")
                    isLoadingAd = false
                    rewardedAd = null
                }

                override fun onAdLoaded(ad: RewardedAd) {
                    isLoadingAd = false
                    rewardedAd = ad
                    Log.d("YandexAds", "Реклама с вознаграждением загружена успешно")
                }
            })
        }
        val adRequestConfiguration = AdRequestConfiguration.Builder(adUnitId).build()
        rewardedAdLoader?.loadAd(adRequestConfiguration)
    }

    fun showAd() {
        if (!isAdAvailable) {
            scope.launch {
                SnackbarController.sendEvent(SnackbarEvent(string(context, R.string.ad_not_available_yet)))
            }
            return
        }

        rewardedAd?.apply {
            setAdEventListener(object : RewardedAdEventListener {
                override fun onAdShown() {
                    Log.d("YandexAds", "Реклама onAdShown")
                }

                override fun onAdFailedToShow(adError: AdError) {
                    Log.d("YandexAds", "Ошибка показа рекламы с вознаграждением: ${adError.description}")
                    rewardedAd?.setAdEventListener(null)
                    rewardedAd = null
                    loadRewardedAd()
                }

                override fun onAdDismissed() {
                    Log.d("YandexAds", "Реклама с вознаграждением закрыта")
                    rewardedAd?.setAdEventListener(null)
                    rewardedAd = null
                    loadRewardedAd()
                }

                override fun onAdClicked() {
                    Log.d("YandexAds", "Клик по рекламе с вознаграждением")
                }

                override fun onAdImpression(impressionData: ImpressionData?) {
                    Log.d("YandexAds", "Показ рекламы с вознаграждением (impression)")
                }

                override fun onRewarded(reward: Reward) {
                    onClaimAdReward()
                    Log.d("YandexAds", "Пользователь получил награду: ${reward.amount} ${reward.type}")
                    scope.launch {
                        SnackbarController.sendEvent(SnackbarEvent(string(context, R.string.ad_reward_received, reward.amount, reward.type)))
                    }

                    lastAdViewTime = System.currentTimeMillis()
                    isAdAvailable = false
                    remainingAdTimeInMillis = adCooldown
                }
            })
            show(context as MainActivity)
        } ?: run {
            Log.d("YandexAds", "Реклама с вознаграждением не загружена. Загрузите рекламу сначала.")
            scope.launch {
                SnackbarController.sendEvent(SnackbarEvent(string(context, R.string.ad_not_loaded)))
            }
        }
    }

    fun checkAdAvailability() {
        val currentTime = System.currentTimeMillis()
        val elapsedTime = currentTime - lastAdViewTime // Время, прошедшее с последнего просмотра

        if (elapsedTime >= adCooldown) {
            isAdAvailable = true
            remainingAdTimeInMillis = 0 // Если время вышло, сбрасываем таймер
        } else {
            isAdAvailable = false
            remainingAdTimeInMillis = adCooldown - elapsedTime // Рассчитываем оставшееся время
        }

        val previouslyAvailable = isAdAvailable
        if (previouslyAvailable != isAdAvailable) {
            Log.d("YandexAds", "checkAdAvailability: isAdAvailable changed to $isAdAvailable")
        }
        if (isAdAvailable && rewardedAd == null && !isLoadingAd) { // Add !isLoadingAd check
            Log.d("YandexAds", "checkAdAvailability: Time is up and no ad loaded/loading. Requesting new ad.")
            loadRewardedAd()
        }
    }

    LaunchedEffect(Unit) {
        loadRewardedAd()
        checkAdAvailability()

        while (isActive) {
            delay(1000)
            checkAdAvailability()
        }
    }

    LaunchedEffect(Unit) {
        loadRewardedAd()
        checkAdAvailability()
        while (isActive) {
            delay(10_000)
            checkAdAvailability()
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
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, top = 16.dp, end = 16.dp),
                textAlign = TextAlign.Center
            )

            val currentRemainingHours = TimeUnit.MILLISECONDS.toHours(remainingTimeInMillis)
            val currentRemainingMinutes = TimeUnit.MILLISECONDS.toMinutes(remainingTimeInMillis) % 60

            val timeRemainingText = remember(remainingTimeInMillis) {
                when {
                    isClaimable -> string(context, R.string.bonus_ready_to_claim)
                    currentRemainingHours > 0 -> string(
                        context,
                        R.string.claim_in_hours_minutes,
                        currentRemainingHours,
                        currentRemainingMinutes
                    )

                    else -> string(context, R.string.claim_in_minutes, currentRemainingMinutes)
                }
            }

            Text(
                text = timeRemainingText,
                style = MaterialTheme.typography.bodyMedium,
                color = if (isClaimable) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                textAlign = TextAlign.Center
            )
        }
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onClaimBonus,
            enabled = isButtonEnabled,
            modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = OutlinedTextFieldDefaults.MinHeight),
            shape = RoundedCornerShape(16.dp)
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

        Button(
            onClick = {
                if (!isLoadingAd) {
                    showAd()
                } else {
                    Log.d("YandexAds", "Реклама еще загружается, подождите.")
                    scope.launch {
                        SnackbarController.sendEvent(SnackbarEvent(string(context, R.string.ad_loading_wait)))
                    }
                }
            },
            enabled = !isLoadingAd && rewardedAd != null && isAdAvailable,
            modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = OutlinedTextFieldDefaults.MinHeight),
            shape = RoundedCornerShape(16.dp)
        ) {
            if (isLoadingAd) {
                SmallLoadingIndicator()
            } else {
                Text(stringResource(R.string.watch_ad_and_get_reward))
                Spacer(modifier = Modifier.width(8.dp))
                Card {
                    Row(
                        modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("+10")
                        Image(painterResource(R.drawable.coin), modifier = Modifier.size(16.dp), contentDescription = null)
                    }
                }
            }
        }

        if (remainingAdTimeInMillis > 0) {
            val minutes = TimeUnit.MILLISECONDS.toMinutes(remainingAdTimeInMillis)
            val seconds = TimeUnit.MILLISECONDS.toSeconds(remainingAdTimeInMillis) % 60
            Text(
                text = stringResource(R.string.ad_available_in, minutes, seconds),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                textAlign = TextAlign.Center
            )
        }
    }
}