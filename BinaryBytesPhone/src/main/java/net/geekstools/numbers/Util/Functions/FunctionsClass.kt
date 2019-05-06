package net.geekstools.numbers.Util.Functions

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Typeface
import android.graphics.drawable.Icon
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.preference.PreferenceManager
import android.telephony.TelephonyManager
import android.text.Html
import android.util.TypedValue
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.reward.RewardItem
import com.google.android.gms.ads.reward.RewardedVideoAdListener
import net.geekstools.numbers.BuildConfig
import net.geekstools.numbers.R
import java.io.FileOutputStream


class FunctionsClass {

    lateinit var activity: Activity
    lateinit var context: Context

    var API: Int

    init {
        API = Build.VERSION.SDK_INT
    }

    constructor(activityInit: Activity, contextInit: Context) {
        this.activity = activityInit;
        this.context = contextInit

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
                rewardedVideoAdInstance.loadAd(context.getString(R.string.ad_unit_reward), AdRequest.Builder()
                        .addTestDevice("CDCAA1F20B5C9C948119E886B31681DE")
                        .addTestDevice("D101234A6C1CF51023EE5815ABC285BD")
                        .addTestDevice("65B5827710CBE90F4A99CE63099E524C")
                        .addTestDevice("DD428143B4772EC7AA87D1E2F9DA787C")
                        .build())
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
                interstitialAd.loadAd(adRequest)
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
    public fun guideScreen(activity: Activity, forceShow: Boolean) {
        val guide: RelativeLayout = activity.findViewById(R.id.guide) as RelativeLayout
        if (forceShow) {
            guide.startAnimation(AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left))
        }
        guide.visibility = View.VISIBLE
        val arrows: ImageView = activity.findViewById(R.id.arrows) as ImageView
        val dots: ImageView = activity.findViewById(R.id.dots) as ImageView
        val left: ImageView = activity.findViewById(R.id.left) as ImageView
        left.background = context.getDrawable(R.drawable.tile_two)
        val right: ImageView = activity.findViewById(R.id.right) as ImageView
        right.background = context.getDrawable(R.drawable.tile_two)
        val appName: TextView = activity.findViewById(R.id.appName) as TextView

        val typeFace: Typeface = Typeface.createFromAsset(context.getAssets(), "upcil.ttf")
        appName.setTypeface(typeFace);

        Handler().postDelayed(Runnable {
            val leftAnim = AnimationUtils.loadAnimation(context, R.anim.ltr)
            val rightAnim = AnimationUtils.loadAnimation(context, R.anim.rtl)
            val animScale = AnimationUtils.loadAnimation(context, R.anim.scales)
            val slideOut = AnimationUtils.loadAnimation(context, android.R.anim.slide_out_right)

            left.startAnimation(leftAnim)
            right.startAnimation(rightAnim)
            leftAnim.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation) {

                }

                override fun onAnimationEnd(animation: Animation) {
                    left.background = context.getDrawable(R.drawable.tile_four)
                    left.startAnimation(animScale)
                    animScale.setAnimationListener(object : Animation.AnimationListener {
                        override fun onAnimationStart(animation: Animation) {

                        }

                        override fun onAnimationEnd(animation: Animation) {
                            Handler().postDelayed({
                                guide.startAnimation(slideOut)
                                slideOut.setAnimationListener(object : Animation.AnimationListener {
                                    override fun onAnimationStart(animation: Animation) {

                                    }

                                    override fun onAnimationEnd(animation: Animation) {
                                        guide.visibility = View.INVISIBLE
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
        }, if (forceShow) {
            230
        } else {
            170
        })
    }

    /*System Checkpoint*/
    public fun getDeviceName(): String {
        val manufacturer: String = Build.MANUFACTURER
        val model: String = Build.MODEL
        return if (model.startsWith(manufacturer)) {
            capitalize(model)
        } else {
            capitalize(manufacturer) + " " + model
        }
    }

    private fun capitalize(someText: String?): String {
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

    public fun getCountryIso(): String {
        var countryISO = "Undefined"
        try {
            val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            countryISO = telephonyManager.simCountryIso
        } catch (e: Exception) {
            countryISO = "Undefined"
        }

        return countryISO
    }

    public fun returnAPI(): Int {
        return API
    }

    public fun appVersionName(pack: String): String {
        var Version = "0"

        try {
            val packInfo = context.packageManager.getPackageInfo(pack, 0)
            Version = packInfo.versionName
        } catch (e: Exception) {
        }

        return Version
    }

    public fun appVersionCode(packageName: String): Int {
        var VersionCode = 0
        try {
            val packInfo = context.packageManager.getPackageInfo(packageName, 0)
            VersionCode = packInfo.versionCode
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return VersionCode
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

    fun savePreference(PreferenceName: String, KEY: String, VALUE: String?) {
        val sharedPreferences = context.getSharedPreferences(PreferenceName, Context.MODE_PRIVATE)
        val editorSharedPreferences = sharedPreferences.edit()
        editorSharedPreferences.putString(KEY, VALUE)
        editorSharedPreferences.apply()
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

    fun saveDefaultPreference(KEY: String, VALUE: Int) {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val editorSharedPreferences = sharedPreferences.edit()
        editorSharedPreferences.putInt(KEY, VALUE)
        editorSharedPreferences.apply()
    }

    fun readPreference(PreferenceName: String, KEY: String, defaultVALUE: String?): String? {
        return context.getSharedPreferences(PreferenceName, Context.MODE_PRIVATE).getString(KEY, defaultVALUE)
    }

    fun readPreference(PreferenceName: String, KEY: String, defaultVALUE: Int): Int {
        return context.getSharedPreferences(PreferenceName, Context.MODE_PRIVATE).getInt(KEY, defaultVALUE)
    }

    fun readPreference(PreferenceName: String, KEY: String, defaultVALUE: Long): Long {
        return context.getSharedPreferences(PreferenceName, Context.MODE_PRIVATE).getLong(KEY, defaultVALUE)
    }

    fun readPreference(PreferenceName: String, KEY: String, defaultVALUE: Boolean): Boolean {
        return context.getSharedPreferences(PreferenceName, Context.MODE_PRIVATE).getBoolean(KEY, defaultVALUE)
    }

    fun readDefaultPreference(KEY: String, defaultVALUE: Int): Int {
        return PreferenceManager.getDefaultSharedPreferences(context).getInt(KEY, defaultVALUE)
    }

    fun readDefaultPreference(KEY: String, defaultVALUE: String): String? {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(KEY, defaultVALUE)
    }

    fun readDefaultPreference(KEY: String, defaultVALUE: Boolean): Boolean {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(KEY, defaultVALUE)
    }

    /*GUI Functions*/
    fun DpToInteger(dp: Int): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), context.resources.displayMetrics).toInt()
    }

    /*Notification*/
    public fun notificationCreator(titleText: String, contentText: String, notificationId: Int) {
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