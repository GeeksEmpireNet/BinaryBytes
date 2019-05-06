package net.geekstools.numbers

import android.annotation.SuppressLint
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.view.View
import android.view.WindowManager
import android.webkit.WebSettings
import android.webkit.WebView
import android.widget.Button
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import net.geekstools.numbers.Util.Functions.FunctionsClass
import net.geekstools.numbers.Util.Functions.WebInterface

class NumbersActivity : Activity() {

    lateinit var functionsClass: FunctionsClass
    lateinit var webGame: WebView

    lateinit var rewardVideo: Button

    lateinit private var firebaseRemoteConfig: FirebaseRemoteConfig

    @SuppressLint("SetJavaScriptEnabled")
    override protected fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web)
        functionsClass = FunctionsClass(this@NumbersActivity, applicationContext)
        functionsClass.guideScreen(this@NumbersActivity, false)

        rewardVideo = findViewById(R.id.rewardVideo)

//        val actionBar: ActionBar = this.actionBar
//        actionBar.title = Html.fromHtml("<font color='#FFFFFF'>" + getString(R.string.app_name) + "</font>")
//        actionBar.setBackgroundDrawable(ColorDrawable(getColor(R.color.default_color)))

        val window = this.window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.statusBarColor = getColor(R.color.default_color)
        window.navigationBarColor = getColor(R.color.default_color)

        webGame = findViewById(R.id.webGame)
        webGame.addJavascriptInterface(WebInterface(this@NumbersActivity, applicationContext), "Android")

        val settings = webGame.settings
        settings.javaScriptEnabled = true
        settings.domStorageEnabled = true
        settings.databaseEnabled = true
        settings.setRenderPriority(WebSettings.RenderPriority.HIGH)
        settings.databasePath = filesDir.parentFile.path + "/databases"

        if (savedInstanceState != null) {
            webGame.restoreState(savedInstanceState)
        } else {
            //webGame.loadUrl("file:///android_asset/2048/index.html?lang=" + Locale.getDefault().getLanguage());
            webGame.loadUrl("file:///android_asset/2048/index.html")
        }

        webGame.setOnTouchListener { view, motionEvent -> false }

        val intentFilter = IntentFilter()
        intentFilter.addAction("ENABLE_REWARDED_VIDEO")
        intentFilter.addAction("RELOAD_REWARDED_VIDEO")
        intentFilter.addAction("REWARDED_PROMOTION_CODE")
        val broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                if (intent.action == "ENABLE_REWARDED_VIDEO") {
                    val rewardedPromotionCode = functionsClass.readPreference(".NoAdsRewardedInfo", "RewardedPromotionCode", 0)
                    if ((rewardedPromotionCode >= 33)
                            && functionsClass.readPreference(".NoAdsRewardedInfo", "Requested", false) == true) {

                        rewardVideo.text = Html.fromHtml("<br/><font color='#f2f7ff'>" +
                                "<big>Please Click to See Video Ads to<br/>" +
                                "\uD83D\uDC8E  \uD83D\uDC8E  \uD83D\uDC8E <b>Support Geeks Empire Open Source Projects</b> \uD83D\uDC8E  \uD83D\uDC8E  \uD83D\uDC8E </big><br/>"
                                + "</font>")
                    } else {
                        rewardVideo.text = Html.fromHtml("<br/><font color='#f2f7ff'>" +
                                "<big>Click to See Video Ads to Get<br/>" +
                                "\uD83D\uDC8E  \uD83D\uDC8E  \uD83D\uDC8E <b>Promotion Codes of Geeks Empire Premium Apps</b> \uD83D\uDC8E  \uD83D\uDC8E  \uD83D\uDC8E </big><br/>"
                                + "</font>")
                        rewardVideo.append("$rewardedPromotionCode / 33")
                    }
                    rewardVideo.setVisibility(View.VISIBLE)
                } else if (intent.action == "RELOAD_REWARDED_VIDEO") {
                    rewardVideo.setVisibility(View.INVISIBLE)
                } else if (intent.action == "REWARDED_PROMOTION_CODE") {
                    rewardVideo.setTextColor(getColor(R.color.light))
                    rewardVideo.text = Html.fromHtml("<br/><font color='#f2f7ff'>" +
                            "<big>Upgrade to Geeks Empire Premium Apps<br/>" +
                            "\uD83D\uDC8E  \uD83D\uDC8E  \uD83D\uDC8E <b>Rewarded to Get Promotion Codes</b> \uD83D\uDC8E  \uD83D\uDC8E  \uD83D\uDC8E </big><br/>"
                            + "\uD83D\uDCE9 Click Here to Request \uD83D\uDCE9"
                            + "</font>")
                    rewardVideo.setVisibility(View.VISIBLE)
                }
            }
        }
        registerReceiver(broadcastReceiver, intentFilter)

        rewardVideo.setOnClickListener {
            if ((functionsClass.readPreference(".NoAdsRewardedInfo", "RewardedPromotionCode", 0) >= 33)
                    && functionsClass.readPreference(".NoAdsRewardedInfo", "Requested", false) == false) {
                try {
                    val textMsg = ("\n\n\n\n\n"
                            + "[Essential Information]" + "\n"
                            + getString(R.string.app_name) + " | " + functionsClass.appVersionName(getPackageName()) + "\n"
                            + functionsClass.getDeviceName() + " | " + "API " + Build.VERSION.SDK_INT + " | " + functionsClass.getCountryIso().toUpperCase())
                    val email = Intent(Intent.ACTION_SEND)
                    email.putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.support)))
                    email.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.rewardedPromotionCodeTitle))
                    email.putExtra(Intent.EXTRA_TEXT, textMsg)
                    email.type = "text/*"
                    email.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    email.setPackage("com.google.android.gm")
                    startActivity(Intent.createChooser(email, getString(R.string.rewardedPromotionCodeTitle)))

                    functionsClass.savePreference(".NoAdsRewardedInfo", "Requested", true)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else {
                sendBroadcast(Intent("SHOW_REWARDED_VIDEO"))
            }
        }
    }

    override fun onStart() {
        super.onStart()
        firebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
        val configSettings: FirebaseRemoteConfigSettings = FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(BuildConfig.DEBUG)
                .build()
        firebaseRemoteConfig.setConfigSettings(configSettings)
        firebaseRemoteConfig.setDefaults(R.xml.remote_config_default)
        firebaseRemoteConfig.fetch(0)
                .addOnCompleteListener(this@NumbersActivity, OnCompleteListener<Void> { task ->
                    if (task.isSuccessful) {
                        firebaseRemoteConfig.activate().addOnSuccessListener {
                            if (firebaseRemoteConfig.getLong(getString(R.string.integerVersionCodeNewUpdatePhone)) > functionsClass.appVersionCode(packageName)) {
                                functionsClass.notificationCreator(
                                        getString(R.string.updateAvailable),
                                        firebaseRemoteConfig.getString(getString(R.string.stringUpcomingChangeLogSummaryPhone)),
                                        firebaseRemoteConfig.getLong(getString(R.string.integerVersionCodeNewUpdatePhone)).toInt()
                                )
                            } else {

                            }
                        }
                    } else {

                    }
                })
    }

    override fun onResume() {
        super.onResume()
        val rewardedPromotionCode = functionsClass.readPreference(".NoAdsRewardedInfo", "RewardedPromotionCode", 0)
        if ((rewardedPromotionCode >= 33)
                && functionsClass.readPreference(".NoAdsRewardedInfo", "Requested", false) == true) {

            rewardVideo.text = Html.fromHtml("<br/><font color='#f2f7ff'>" +
                    "<big>Please Click to See Rewarded Ads to<br/>" +
                    "\uD83D\uDC8E  \uD83D\uDC8E  \uD83D\uDC8E <b>Support Geeks Empire Open Source Projects</b> \uD83D\uDC8E  \uD83D\uDC8E  \uD83D\uDC8E </big><br/>"
                    + "</font>")
        } else if ((rewardedPromotionCode >= 33)
                && functionsClass.readPreference(".NoAdsRewardedInfo", "Requested", false) == false) {

            rewardVideo.setTextColor(getColor(R.color.light))
            rewardVideo.text = Html.fromHtml("<br/><font color='#f2f7ff'>" +
                    "<big>Upgrade to Geeks Empire Premium Apps<br/>" +
                    "\uD83D\uDC8E  \uD83D\uDC8E  \uD83D\uDC8E <b>Rewarded to Get Promotion Codes</b> \uD83D\uDC8E  \uD83D\uDC8E  \uD83D\uDC8E </big><br/>"
                    + "\uD83D\uDCE9 Click Here to Request \uD83D\uDCE9<br/>"
                    + "</font>")
            rewardVideo.visibility = View.VISIBLE
        } else {
            rewardVideo.text = Html.fromHtml("<br/><font color='#f2f7ff'>" +
                    "<big>Click to See Rewarded Ads to Get<br/>" +
                    "\uD83D\uDC8E  \uD83D\uDC8E  \uD83D\uDC8E <b>Promotion Codes of Geeks Empire Premium Apps</b> \uD83D\uDC8E  \uD83D\uDC8E  \uD83D\uDC8E </big><br/>"
                    + "</font>")
            rewardVideo.append("$rewardedPromotionCode / 33" + Html.fromHtml("<br/>"))
        }
    }

    override protected fun onSaveInstanceState(outState: Bundle?) {
        webGame.saveState(outState)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }
}