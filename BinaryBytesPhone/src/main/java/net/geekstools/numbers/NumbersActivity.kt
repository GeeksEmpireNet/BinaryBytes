package net.geekstools.numbers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import net.geekstools.numbers.GameData.GamePlayData
import net.geekstools.numbers.GameView.GamePlayView
import net.geekstools.numbers.Util.Functions.FunctionsClass
import net.geekstools.numbers.Util.Functions.PublicVariable
import net.geekstools.numbers.databinding.GameActivityBinding

class NumbersActivity : AppCompatActivity() {

    lateinit var functionsClass: FunctionsClass


    private lateinit var gamePlayView: GamePlayView


    private lateinit var gameActivityBinding: GameActivityBinding


    private lateinit var gamePlayData: GamePlayData


    private lateinit var firebaseRemoteConfig: FirebaseRemoteConfig

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        gameActivityBinding = GameActivityBinding.inflate(layoutInflater)
        setContentView(gameActivityBinding.root)

        PublicVariable.eligibleToLoadShowAds = true

        functionsClass = FunctionsClass(applicationContext)
        functionsClass.guideScreen(this@NumbersActivity, false)

        gamePlayView = GamePlayView(applicationContext, this@NumbersActivity)

        gamePlayData = GamePlayData(applicationContext, gamePlayView)

        if (savedInstanceState != null) {
            if (savedInstanceState.getBoolean("hasState")) {
                gamePlayData.load()
            }
        }

        gameActivityBinding.gamePlayViewHolder.addView(gamePlayView)

        val window = this.window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.statusBarColor = getColor(R.color.default_color)
        window.navigationBarColor = getColor(R.color.default_color)

        val intentFilter = IntentFilter()
        intentFilter.addAction("ENABLE_REWARDED_VIDEO")
        intentFilter.addAction("RELOAD_REWARDED_VIDEO")
        intentFilter.addAction("REWARDED_PROMOTION_CODE")
        val broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                if (intent.action == "ENABLE_REWARDED_VIDEO") {
                    val rewardedPromotionCode = functionsClass.readPreference(".NoAdsRewardedInfo", "RewardedPromotionCode", 0)
                    if ((rewardedPromotionCode >= 33) && functionsClass.readPreference(".NoAdsRewardedInfo", "Requested", false)) {

                        gameActivityBinding.rewardVideo.text = Html.fromHtml("<br/><font color='#f2f7ff'>" +
                                "<big>Please Click to See Video Ads to<br/>" +
                                "\uD83D\uDC8E  \uD83D\uDC8E  \uD83D\uDC8E <b>Support Geeks Empire Open Source Projects</b> \uD83D\uDC8E  \uD83D\uDC8E  \uD83D\uDC8E </big><br/>"
                                + "</font>")
                    } else {
                        gameActivityBinding.rewardVideo.text = Html.fromHtml("<br/><font color='#f2f7ff'>" +
                                "<big>Click to See Video Ads to Get<br/>" +
                                "\uD83D\uDC8E  \uD83D\uDC8E  \uD83D\uDC8E <b>Promotion Codes of Geeks Empire Premium Apps</b> \uD83D\uDC8E  \uD83D\uDC8E  \uD83D\uDC8E </big><br/>"
                                + "</font>")
                        gameActivityBinding.rewardVideo.append("$rewardedPromotionCode / 33")
                    }
                    gameActivityBinding.rewardVideo.setVisibility(View.VISIBLE)
                } else if (intent.action == "RELOAD_REWARDED_VIDEO") {
                    gameActivityBinding.rewardVideo.setVisibility(View.INVISIBLE)
                } else if (intent.action == "REWARDED_PROMOTION_CODE") {
                    gameActivityBinding.rewardVideo.setTextColor(getColor(R.color.light))
                    gameActivityBinding.rewardVideo.text = Html.fromHtml("<br/><font color='#f2f7ff'>" +
                            "<big>Upgrade to Geeks Empire Premium Apps<br/>" +
                            "\uD83D\uDC8E  \uD83D\uDC8E  \uD83D\uDC8E <b>Rewarded to Get Promotion Codes</b> \uD83D\uDC8E  \uD83D\uDC8E  \uD83D\uDC8E </big><br/>"
                            + "\uD83D\uDCE9 Click Here to Request \uD83D\uDCE9"
                            + "</font>")
                    gameActivityBinding.rewardVideo.setVisibility(View.VISIBLE)
                }
            }
        }
        registerReceiver(broadcastReceiver, intentFilter)

        gameActivityBinding.rewardVideo.setOnClickListener {
            if ((functionsClass.readPreference(".NoAdsRewardedInfo", "RewardedPromotionCode", 0) >= 33) && !functionsClass.readPreference(".NoAdsRewardedInfo", "Requested", false)) {
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
        firebaseRemoteConfig.setDefaultsAsync(R.xml.remote_config_default)
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
        PublicVariable.eligibleToLoadShowAds = true

        gamePlayData.load()

        val rewardedPromotionCode = functionsClass.readPreference(".NoAdsRewardedInfo", "RewardedPromotionCode", 0)
        if ((rewardedPromotionCode >= 33) && functionsClass.readPreference(".NoAdsRewardedInfo", "Requested", false)) {

            gameActivityBinding.rewardVideo.text = Html.fromHtml("<br/><font color='#f2f7ff'>" +
                    "<big>Please Click to See Rewarded Ads to<br/>" +
                    "\uD83D\uDC8E  \uD83D\uDC8E  \uD83D\uDC8E <b>Support Geeks Empire Open Source Projects</b> \uD83D\uDC8E  \uD83D\uDC8E  \uD83D\uDC8E </big><br/>"
                    + "</font>")
        } else if ((rewardedPromotionCode >= 33) && !functionsClass.readPreference(".NoAdsRewardedInfo", "Requested", false)) {

            gameActivityBinding.rewardVideo.setTextColor(getColor(R.color.light))
            gameActivityBinding.rewardVideo.text = Html.fromHtml("<br/><font color='#f2f7ff'>" +
                    "<big>Upgrade to Geeks Empire Premium Apps<br/>" +
                    "\uD83D\uDC8E  \uD83D\uDC8E  \uD83D\uDC8E <b>Rewarded to Get Promotion Codes</b> \uD83D\uDC8E  \uD83D\uDC8E  \uD83D\uDC8E </big><br/>"
                    + "\uD83D\uDCE9 Click Here to Request \uD83D\uDCE9<br/>"
                    + "</font>")
            gameActivityBinding.rewardVideo.visibility = View.VISIBLE
        } else {
            gameActivityBinding.rewardVideo.text = Html.fromHtml("<br/><font color='#f2f7ff'>" +
                    "<big>Click to See Rewarded Ads to Get<br/>" +
                    "\uD83D\uDC8E  \uD83D\uDC8E  \uD83D\uDC8E <b>Promotion Codes of Geeks Empire Premium Apps</b> \uD83D\uDC8E  \uD83D\uDC8E  \uD83D\uDC8E </big><br/>"
                    + "</font>")
            gameActivityBinding.rewardVideo.append("$rewardedPromotionCode / 33" + Html.fromHtml("<br/>"))
        }
    }

    override fun onPause() {
        super.onPause()
        PublicVariable.eligibleToLoadShowAds = false

        gamePlayData.save()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            //Do nothing
            return true
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
            gamePlayView!!.game.move(2)
            return true
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
            gamePlayView!!.game.move(0)
            return true
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
            gamePlayView!!.game.move(3)
            return true
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
            gamePlayView!!.game.move(1)
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    public override fun onSaveInstanceState(savedInstanceState: Bundle) {
        super.onSaveInstanceState(savedInstanceState)
        savedInstanceState.putBoolean("hasState", true)

        gamePlayData.save()
    }
}