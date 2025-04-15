package com.vangelnum.wisher.features.shop.presentation

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.vangelnum.wisher.MainActivity
import com.yandex.mobile.ads.common.AdError
import com.yandex.mobile.ads.common.AdRequestConfiguration
import com.yandex.mobile.ads.common.AdRequestError
import com.yandex.mobile.ads.common.ImpressionData
import com.yandex.mobile.ads.rewarded.Reward
import com.yandex.mobile.ads.rewarded.RewardedAd
import com.yandex.mobile.ads.rewarded.RewardedAdEventListener
import com.yandex.mobile.ads.rewarded.RewardedAdLoadListener
import com.yandex.mobile.ads.rewarded.RewardedAdLoader

@Composable
fun ShopScreen() {
    var rewardedAd: RewardedAd? by remember { mutableStateOf(null) }
    var rewardedAdLoader: RewardedAdLoader? by remember { mutableStateOf(null) }
    var rewardMessage by remember { mutableStateOf("Награда не получена") }
    var isLoadingAd by remember { mutableStateOf(false) }
    val adUnitId = "R-M-15084813-1"
    val context = LocalContext.current

    fun loadRewardedAd() {
        isLoadingAd = true
        rewardedAdLoader = RewardedAdLoader(context).apply {
            setAdLoadListener(object : RewardedAdLoadListener {
                override fun onAdFailedToLoad(error: AdRequestError) {
                    Log.d("YandexAds","Ошибка загрузки $error")
                    isLoadingAd = false
                    rewardedAd = null
                }
                override fun onAdLoaded(ad: RewardedAd) {
                    isLoadingAd = false
                    rewardedAd = ad
                    Log.d("YandexAds","Реклама с вознаграждением загружена успешно")
                }
            })
        }
        val adRequestConfiguration = AdRequestConfiguration.Builder(adUnitId).build()
        rewardedAdLoader?.loadAd(adRequestConfiguration)
    }

    fun showAd() {
        rewardedAd?.apply {
            setAdEventListener(object : RewardedAdEventListener {
                override fun onAdShown() {
                    Log.d("YandexAds","Реклама onAdShown")
                }

                override fun onAdFailedToShow(adError: AdError) {
                    Log.d("YandexAds","Ошибка показа рекламы с вознаграждением: ${adError.description}")
                    rewardedAd?.setAdEventListener(null)
                    rewardedAd = null
                    loadRewardedAd()
                }

                override fun onAdDismissed() {
                    Log.d("YandexAds","Реклама с вознаграждением закрыта")
                    rewardedAd?.setAdEventListener(null)
                    rewardedAd = null
                    loadRewardedAd()
                }

                override fun onAdClicked() {
                    Log.d("YandexAds","Клик по рекламе с вознаграждением")
                }

                override fun onAdImpression(impressionData: ImpressionData?) {
                    Log.d("YandexAds","Показ рекламы с вознаграждением (impression)")
                }

                override fun onRewarded(reward: Reward) {
                    Log.d("YandexAds","Пользователь получил награду: ${reward.amount} ${reward.type}")
                    rewardMessage = "Вы получили награду: ${reward.amount} ${reward.type}!"
                }
            })
            show(context as MainActivity)
        } ?: run {
            Log.d("YandexAds","Реклама с вознаграждением не загружена. Загрузите рекламу сначала.")
        }
    }

    LaunchedEffect(key1 = Unit) {
        loadRewardedAd()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = rewardMessage)
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (!isLoadingAd) {
                    showAd()
                } else {
                    Log.d("YandexAds","Реклама еще загружается, подождите.")
                }
            },
            enabled = !isLoadingAd && rewardedAd != null
        ) {
            Text("Смотреть рекламу и получить награду")
        }

        if (isLoadingAd) {
            Spacer(modifier = Modifier.height(8.dp))
            CircularProgressIndicator()
            Text("Загрузка рекламы...")
        }
    }
}