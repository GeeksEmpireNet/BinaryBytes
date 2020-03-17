package net.geekstools.trexrunner.Util

import android.content.Context
import com.google.android.gms.ads.*
import com.google.android.gms.ads.reward.RewardItem
import com.google.android.gms.ads.reward.RewardedVideoAd
import com.google.android.gms.ads.reward.RewardedVideoAdListener
import net.geekstools.numbers.R
import net.geekstools.numbers.Util.Functions.FunctionsClassCheckpoint
import net.geekstools.numbers.Util.Functions.PublicVariable

class FunctionsClassAds(var context: Context) {

    val functionsClassPreferences = FunctionsClassCheckpoint(context)

    val rewardedVideoAdInstance: RewardedVideoAd = MobileAds.getRewardedVideoAdInstance(context)

    val interstitialAd: InterstitialAd = InterstitialAd(context)

    private val adsRequest: AdRequest = AdRequest.Builder()
            .build()
    private val requestConfiguration = RequestConfiguration.Builder()
            .setTestDeviceIds(arrayOf("CDCAA1F20B5C9C948119E886B31681DE", "D101234A6C1CF51023EE5815ABC285BD", "65B5827710CBE90F4A99CE63099E524C", "DD428143B4772EC7AA87D1E2F9DA787C").toMutableList())
            .build()

    init {
        rewardedVideoAdInstance.setImmersiveMode(true)
        rewardedVideoAdInstance.loadAd(context.getString(R.string.adUnitReward), adsRequest)

        interstitialAd.adUnitId = context.getString(R.string.adUnitInterstitial)
        interstitialAd.setImmersiveMode(true)
        interstitialAd.loadAd(adsRequest)
    }

    fun initialLoadingAds(adsInterface: AdsInterface) {
        MobileAds.initialize(context, context.getString(R.string.AdAppId))
        MobileAds.setRequestConfiguration(requestConfiguration)


        rewardedVideoAdInstance.rewardedVideoAdListener = object : RewardedVideoAdListener {
            override fun onRewardedVideoAdLoaded() {
                adsInterface.enableRewardedVideo()
            }

            override fun onRewardedVideoAdOpened() {

            }

            override fun onRewardedVideoStarted() {

            }

            override fun onRewardedVideoAdClosed() {
                val rewardedPromotionCode = functionsClassPreferences.readPreference(".NoAdsRewardedInfo", "RewardedPromotionCode", 0)
                if (rewardedPromotionCode >= 33 && !functionsClassPreferences.readPreference(".NoAdsRewardedInfo", "Requested", false)) {
                    adsInterface.rewardedPromotionCode()
                } else {
                    adsInterface.reloadRewardedVideo()

                    rewardedVideoAdInstance.loadAd(context.getString(R.string.adUnitReward), adsRequest)
                }
            }

            override fun onRewarded(rewardItem: RewardItem) {
                val rewardedPromotionCode = functionsClassPreferences.readPreference(".NoAdsRewardedInfo", "RewardedPromotionCode", 0)
                functionsClassPreferences.savePreference(".NoAdsRewardedInfo", "RewardedPromotionCode", rewardedPromotionCode + rewardItem.amount)

                functionsClassPreferences.savePreference(".NoAdsRewardedInfo", "RewardedTime", System.currentTimeMillis())
                functionsClassPreferences.savePreference(".NoAdsRewardedInfo", "RewardedAmount", rewardItem.amount)
                functionsClassPreferences.saveFile(".NoAdsRewarded", context.packageName)
            }

            override fun onRewardedVideoAdLeftApplication() {

            }

            override fun onRewardedVideoAdFailedToLoad(failedCode: Int) {
                rewardedVideoAdInstance.loadAd(context.getString(R.string.adUnitReward), adsRequest)
            }

            override fun onRewardedVideoCompleted() {

            }
        }

        interstitialAd.adListener = object : AdListener() {
            override fun onAdLoaded() {

            }

            override fun onAdFailedToLoad(errorCode: Int) {
                if (PublicVariable.eligibleToLoadShowAds) {
                    interstitialAd.loadAd(adsRequest)
                }
            }

            override fun onAdOpened() {

            }

            override fun onAdClicked() {

            }

            override fun onAdLeftApplication() {

            }

            override fun onAdClosed() {
                interstitialAd.loadAd(adsRequest)
            }
        }
    }
}