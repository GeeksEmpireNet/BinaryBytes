package net.geekstools.numbers.Util.Functions

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Typeface
import android.graphics.drawable.Icon
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.telephony.TelephonyManager
import android.text.Html
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.reward.RewardItem
import com.google.android.gms.ads.reward.RewardedVideoAdListener
import net.geekstools.numbers.BuildConfig
import net.geekstools.numbers.R
import net.geekstools.numbers.databinding.GameActivityBinding
import java.io.FileOutputStream


class FunctionsClass (private var context: Context) {

    init {
        MobileAds.initialize(context, context.getString(R.string.ad_app_id))
        val rewardedVideoAdInstance = MobileAds.getRewardedVideoAdInstance(context)
        rewardedVideoAdInstance.setImmersiveMode(true)
        rewardedVideoAdInstance.loadAd(context.getString(R.string.ad_unit_reward), AdRequest.Builder()
                .addTestDevice("CDCAA1F20B5C9C948119E886B31681DE")
                .addTestDevice("D101234A6C1CF51023EE5815ABC285BD")
                .addTestDevice("65B5827710CBE90F4A99CE63099E524C")
                .addTestDevice("DD428143B4772EC7AA87D1E2F9DA787C")
                .build())
        rewardedVideoAdInstance.rewardedVideoAdListener = object : RewardedVideoAdListener {
            override fun onRewardedVideoAdLoaded() {
                context.sendBroadcast(Intent("ENABLE_REWARDED_VIDEO"))

                val intentFilter = IntentFilter()
                intentFilter.addAction("SHOW_REWARDED_VIDEO")
                val broadcastReceiver = object : BroadcastReceiver() {
                    override fun onReceive(context: Context, intent: Intent) {
                        if (intent.action == "SHOW_REWARDED_VIDEO") {
                            rewardedVideoAdInstance.show()
                        }
                    }
                }
                context.registerReceiver(broadcastReceiver, intentFilter)
            }

            override fun onRewardedVideoAdOpened() {}

            override fun onRewardedVideoStarted() {}

            override fun onRewardedVideoAdClosed() {
                val rewardedPromotionCode = readPreference(".NoAdsRewardedInfo", "RewardedPromotionCode", 0)
                if (rewardedPromotionCode >= 33 && readPreference(".NoAdsRewardedInfo", "Requested", false) == false) {
                    context.sendBroadcast(Intent("REWARDED_PROMOTION_CODE"))
                } else {
                    context.sendBroadcast(Intent("RELOAD_REWARDED_VIDEO"))
                    rewardedVideoAdInstance.loadAd(context.getString(R.string.ad_unit_reward), AdRequest.Builder()
                            .addTestDevice("CDCAA1F20B5C9C948119E886B31681DE")
                            .addTestDevice("D101234A6C1CF51023EE5815ABC285BD")
                            .addTestDevice("65B5827710CBE90F4A99CE63099E524C")
                            .addTestDevice("DD428143B4772EC7AA87D1E2F9DA787C")
                            .build())
                }
            }

            override fun onRewarded(rewardItem: RewardItem) {
                val rewardedPromotionCode = readPreference(".NoAdsRewardedInfo", "RewardedPromotionCode", 0)
                savePreference(".NoAdsRewardedInfo", "RewardedPromotionCode", rewardedPromotionCode + rewardItem.amount)

                savePreference(".NoAdsRewardedInfo", "RewardedTime", System.currentTimeMillis())
                savePreference(".NoAdsRewardedInfo", "RewardedAmount", rewardItem.amount)
                saveFile(".NoAdsRewarded", context.packageName)
            }

            override fun onRewardedVideoAdLeftApplication() {}

            override fun onRewardedVideoAdFailedToLoad(failedCode: Int) {
                if (BuildConfig.DEBUG) {
                    println("Ad Failed $failedCode")
                }

                if (PublicVariable.eligibleToLoadShowAds) {
                    rewardedVideoAdInstance.loadAd(context.getString(R.string.ad_unit_reward), AdRequest.Builder()
                            .addTestDevice("CDCAA1F20B5C9C948119E886B31681DE")
                            .addTestDevice("D101234A6C1CF51023EE5815ABC285BD")
                            .addTestDevice("65B5827710CBE90F4A99CE63099E524C")
                            .addTestDevice("DD428143B4772EC7AA87D1E2F9DA787C")
                            .build())
                }
            }

            override fun onRewardedVideoCompleted() {}
        }

        val adRequest = AdRequest.Builder()
                .addTestDevice("CDCAA1F20B5C9C948119E886B31681DE")
                .addTestDevice("D101234A6C1CF51023EE5815ABC285BD")
                .addTestDevice("65B5827710CBE90F4A99CE63099E524C")
                .addTestDevice("DD428143B4772EC7AA87D1E2F9DA787C")
                .build()
        val interstitialAd: InterstitialAd = InterstitialAd(context)
        interstitialAd.adUnitId = context.getString(R.string.ad_unit_interstitial)
        interstitialAd.setImmersiveMode(true)
        interstitialAd.loadAd(adRequest)
        interstitialAd.adListener = object : AdListener() {
            override fun onAdLoaded() {
                val intentFilter = IntentFilter()
                intentFilter.addAction("SHOW_INTERSTITIAL_ADS")
                intentFilter.addAction("LOAD_INTERSTITIAL_ADS")
                val broadcastReceiver = object : BroadcastReceiver() {
                    override fun onReceive(context: Context, intent: Intent) {
                        if (intent.action == "SHOW_INTERSTITIAL_ADS") {
                            interstitialAd.show()
                        } else if (intent.action == "LOAD_INTERSTITIAL_ADS") {
                            interstitialAd.loadAd(adRequest)

                        }
                    }
                }
                context.registerReceiver(broadcastReceiver, intentFilter)
            }

            override fun onAdFailedToLoad(errorCode: Int) {
                if (PublicVariable.eligibleToLoadShowAds) {
                    interstitialAd.loadAd(adRequest)
                }
            }

            override fun onAdOpened() {

            }

            override fun onAdClicked() {

            }

            override fun onAdLeftApplication() {

            }

            override fun onAdClosed() {

            }
        }
    }

    /*Guide*/
    fun guideScreen(gameActivityBinding: GameActivityBinding, forceShow: Boolean) {
        if (forceShow) {
            gameActivityBinding.guide.startAnimation(AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left))
        }
        gameActivityBinding.guide.visibility = View.VISIBLE
        gameActivityBinding.left.background = context.getDrawable(R.drawable.tile_two)
        gameActivityBinding.right.background = context.getDrawable(R.drawable.tile_two)

        val typeFace: Typeface = Typeface.createFromAsset(context.assets, "upcil.ttf")
        gameActivityBinding.appName.typeface = typeFace

        Handler().postDelayed({
            val leftToRightAnimation = AnimationUtils.loadAnimation(context, R.anim.left_to_right)
            val rightToLeftAnimation = AnimationUtils.loadAnimation(context, R.anim.right_to_left)
            val animationScaleUpDown = AnimationUtils.loadAnimation(context, R.anim.scales_up_down)

            val slideOut = AnimationUtils.loadAnimation(context, android.R.anim.slide_out_right)

            gameActivityBinding.left.startAnimation(leftToRightAnimation)
            gameActivityBinding.right.startAnimation(rightToLeftAnimation)
            leftToRightAnimation.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation) {

                }

                override fun onAnimationEnd(animation: Animation) {
                    gameActivityBinding.left.background = context.getDrawable(R.drawable.tile_four)
                    gameActivityBinding.left.startAnimation(animationScaleUpDown)
                    animationScaleUpDown.setAnimationListener(object : Animation.AnimationListener {
                        override fun onAnimationStart(animation: Animation) {

                        }

                        override fun onAnimationEnd(animation: Animation) {
                            Handler().postDelayed({
                                gameActivityBinding.guide.startAnimation(slideOut)
                                slideOut.setAnimationListener(object : Animation.AnimationListener {
                                    override fun onAnimationStart(animation: Animation) {

                                    }

                                    override fun onAnimationEnd(animation: Animation) {
                                        gameActivityBinding.guide.visibility = View.INVISIBLE
                                    }

                                    override fun onAnimationRepeat(animation: Animation) {

                                    }
                                })
                            }, 250)
                        }

                        override fun onAnimationRepeat(animation: Animation) {

                        }
                    })
                }

                override fun onAnimationRepeat(animation: Animation) {

                }
            })
        }, 230)
    }

    /*System Checkpoint*/
    fun getDeviceName(): String {
        val manufacturer: String = Build.MANUFACTURER
        val model: String = Build.MODEL
        return if (model.startsWith(manufacturer)) {
            capitalizeFirstChar(model)
        } else {
            capitalizeFirstChar(manufacturer) + " " + model
        }
    }

    private fun capitalizeFirstChar(someText: String?): String {
        if (someText == null || someText.length == 0) {
            return ""
        }
        val first = someText[0]
        return if (Character.isUpperCase(first)) {
            someText
        } else {
            Character.toUpperCase(first) + someText.substring(1)
        }
    }

    fun getCountryIso(): String {
        var countryISO = "Undefined"
        try {
            val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            countryISO = telephonyManager.simCountryIso
        } catch (e: Exception) {
            countryISO = "Undefined"
        }

        return countryISO
    }

    fun appVersionName(pack: String): String {

        return BuildConfig.VERSION_NAME
    }

    fun appVersionCode(packageName: String): Int {

        return BuildConfig.VERSION_CODE
    }

    /*Files*/
    fun saveFile(fileName: String, fileContent: String) {
        try {
            val fileOutputStream: FileOutputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE)
            fileOutputStream.write((fileContent).toByteArray())

            fileOutputStream.flush()
            fileOutputStream.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun savePreference(PreferenceName: String, KEY: String, VALUE: Int) {
        val sharedPreferences = context.getSharedPreferences(PreferenceName, Context.MODE_PRIVATE)
        val editorSharedPreferences = sharedPreferences.edit()
        editorSharedPreferences.putInt(KEY, VALUE)
        editorSharedPreferences.apply()
    }

    fun savePreference(PreferenceName: String, KEY: String, VALUE: Long) {
        val sharedPreferences = context.getSharedPreferences(PreferenceName, Context.MODE_PRIVATE)
        val editorSharedPreferences = sharedPreferences.edit()
        editorSharedPreferences.putLong(KEY, VALUE)
        editorSharedPreferences.apply()
    }

    fun savePreference(PreferenceName: String, KEY: String, VALUE: Boolean) {
        val sharedPreferences = context.getSharedPreferences(PreferenceName, Context.MODE_PRIVATE)
        val editorSharedPreferences = sharedPreferences.edit()
        editorSharedPreferences.putBoolean(KEY, VALUE)
        editorSharedPreferences.apply()
    }

    fun readPreference(PreferenceName: String, KEY: String, defaultVALUE: Int): Int {
        return context.getSharedPreferences(PreferenceName, Context.MODE_PRIVATE).getInt(KEY, defaultVALUE)
    }

    fun readPreference(PreferenceName: String, KEY: String, defaultVALUE: Boolean): Boolean {
        return context.getSharedPreferences(PreferenceName, Context.MODE_PRIVATE).getBoolean(KEY, defaultVALUE)
    }

    /*Notification*/
    fun notificationCreator(titleText: String, contentText: String, notificationId: Int) {
        try {
            val notificationManager: NotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val notificationBuilder: Notification.Builder = Notification.Builder(context)
            notificationBuilder.setContentTitle(Html.fromHtml("<b><font color='" + context.getColor(R.color.default_color_darker) + "'>" + titleText + "</font></b>"))
            notificationBuilder.setContentText(Html.fromHtml("<font color='" + context.getColor(R.color.default_color_light) + "'>" + contentText + "</font>"))
            notificationBuilder.setTicker(context.getString(R.string.app_name))
            notificationBuilder.setSmallIcon(R.drawable.ic_notification)
            notificationBuilder.setAutoCancel(true)
            notificationBuilder.setColor(context.getColor(R.color.default_color))
            notificationBuilder.setPriority(Notification.PRIORITY_HIGH)

            val newUpdate: Intent = Intent(Intent.ACTION_VIEW, Uri.parse(context.getString(R.string.play_store_link) + context.packageName))
            val newUpdatePendingIntent: PendingIntent = PendingIntent.getActivity(context, 1, newUpdate, PendingIntent.FLAG_UPDATE_CURRENT)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val notificationChannel: NotificationChannel = NotificationChannel(context.packageName, context.getString(R.string.app_name), NotificationManager.IMPORTANCE_HIGH)
                notificationManager.createNotificationChannel(notificationChannel)
                notificationBuilder.setChannelId(context.packageName)
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val builderActionNotification = Notification.Action.Builder(
                        Icon.createWithResource(context, R.drawable.draw_share_menu),
                        context.getString(R.string.rate),
                        newUpdatePendingIntent
                )
                notificationBuilder.addAction(builderActionNotification.build())
            }
            notificationBuilder.setContentIntent(newUpdatePendingIntent)
            notificationManager.notify(notificationId, notificationBuilder.build())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}