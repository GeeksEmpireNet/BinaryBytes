package net.geekstools.numbers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.text.Html
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import net.geekstools.numbers.GameView.GamePlayView
import net.geekstools.numbers.GameView.Tile
import net.geekstools.numbers.Util.Functions.FunctionsClass
import net.geekstools.numbers.Util.Functions.PublicVariable

class NumbersActivity : AppCompatActivity() {

    lateinit var functionsClass: FunctionsClass

    private var gamePlayView: GamePlayView? = null
    lateinit var rewardVideo: Button

    lateinit var firebaseRemoteConfig: FirebaseRemoteConfig

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.game_activity)

        PublicVariable.eligibleToLoadShowAds = true

        functionsClass = FunctionsClass(this@NumbersActivity, applicationContext)
        functionsClass.guideScreen(this@NumbersActivity, false)

        rewardVideo = findViewById(R.id.rewardVideo)

        gamePlayView = GamePlayView(applicationContext, this@NumbersActivity)

        gamePlayView.addView(gamePlayView)


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
        PublicVariable.eligibleToLoadShowAds = true

        val rewardedPromotionCode = functionsClass.readPreference(".NoAdsRewardedInfo", "RewardedPromotionCode", 0)
        if ((rewardedPromotionCode >= 33) && functionsClass.readPreference(".NoAdsRewardedInfo", "Requested", false)) {

            rewardVideo.text = Html.fromHtml("<br/><font color='#f2f7ff'>" +
                    "<big>Please Click to See Rewarded Ads to<br/>" +
                    "\uD83D\uDC8E  \uD83D\uDC8E  \uD83D\uDC8E <b>Support Geeks Empire Open Source Projects</b> \uD83D\uDC8E  \uD83D\uDC8E  \uD83D\uDC8E </big><br/>"
                    + "</font>")
        } else if ((rewardedPromotionCode >= 33) && !functionsClass.readPreference(".NoAdsRewardedInfo", "Requested", false)) {

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

    override fun onPause() {
        super.onPause()
        PublicVariable.eligibleToLoadShowAds = false
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
        save()
    }

    private fun save() {
        val settings = PreferenceManager.getDefaultSharedPreferences(this)
        val editor = settings.edit()
        val field = gamePlayView!!.game.grid!!.field
        val undoField = gamePlayView!!.game.grid!!.undoField
        editor.putInt(WIDTH, field.size)
        editor.putInt(HEIGHT, field.size)
        for (xx in field.indices) {
            for (yy in 0 until field[0].size) {
                if (field[xx][yy] != null) {
                    editor.putInt(xx.toString() + " " + yy, field[xx][yy]!!.value)
                } else {
                    editor.putInt(xx.toString() + " " + yy, 0)
                }

                if (undoField[xx][yy] != null) {
                    editor.putInt("$UNDO_GRID$xx $yy", undoField[xx][yy]!!.value)
                } else {
                    editor.putInt("$UNDO_GRID$xx $yy", 0)
                }
            }
        }
        editor.putLong(SCORE, gamePlayView!!.game.score)
        editor.putLong(HIGH_SCORE, gamePlayView!!.game.highScore)
        editor.putLong(UNDO_SCORE, gamePlayView!!.game.lastScore)
        editor.putBoolean(CAN_UNDO, gamePlayView!!.game.canUndo)
        editor.putInt(GAME_STATE, gamePlayView!!.game.gameState)
        editor.putInt(UNDO_GAME_STATE, gamePlayView!!.game.lastGameState)
        editor.apply()
    }

    private fun load() {
        //Stopping all animations
        gamePlayView!!.game.aGrid.cancelAnimations()

        val settings = PreferenceManager.getDefaultSharedPreferences(this)
        for (xx in gamePlayView!!.game.grid!!.field.indices) {
            for (yy in gamePlayView!!.game.grid!!.field[0].size until gamePlayView!!.game.grid!!.field[0].size) {
                val value = settings.getInt(xx.toString() + " " + yy, -1)
                if (value > 0) {
                    gamePlayView!!.game.grid!!.field[xx][yy] = Tile(xx, yy, value)
                } else if (value == 0) {
                    gamePlayView!!.game.grid!!.field[xx][yy] = null
                }

                val undoValue = settings.getInt("$UNDO_GRID$xx $yy", -1)
                if (undoValue > 0) {
                    gamePlayView!!.game.grid!!.undoField[xx][yy] = Tile(xx, yy, undoValue)
                } else if (value == 0) {
                    gamePlayView!!.game.grid!!.undoField[xx][yy] = null
                }
            }
        }

        gamePlayView!!.game.score = settings.getLong(SCORE, gamePlayView!!.game.score)
        gamePlayView!!.game.highScore = settings.getLong(HIGH_SCORE, gamePlayView!!.game.highScore)
        gamePlayView!!.game.lastScore = settings.getLong(UNDO_SCORE, gamePlayView!!.game.lastScore)
        gamePlayView!!.game.canUndo = settings.getBoolean(CAN_UNDO, gamePlayView!!.game.canUndo)
        gamePlayView!!.game.gameState = settings.getInt(GAME_STATE, gamePlayView!!.game.gameState)
        gamePlayView!!.game.lastGameState = settings.getInt(UNDO_GAME_STATE, gamePlayView!!.game.lastGameState)
    }

    companion object {
        private val WIDTH = "width"
        private val HEIGHT = "height"
        private val SCORE = "score"
        private val HIGH_SCORE = "high score temp"
        private val UNDO_SCORE = "undo score"
        private val CAN_UNDO = "can undo"
        private val UNDO_GRID = "undo"
        private val GAME_STATE = "game state"
        private val UNDO_GAME_STATE = "undo game state"
    }
}