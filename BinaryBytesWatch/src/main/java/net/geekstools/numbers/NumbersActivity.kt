package net.geekstools.numbers

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.ResultReceiver
import android.preference.PreferenceManager
import android.support.wearable.view.ConfirmationOverlay
import android.support.wearable.view.SwipeDismissFrameLayout
import android.view.KeyEvent
import android.view.WindowManager
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.wearable.intent.RemoteIntent
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import net.geekstools.numbers.Util.Functions.FunctionsClass

import net.geekstools.numbers.Util.MainView
import net.geekstools.numbers.Util.Tile

class NumbersActivity : Activity() {

    lateinit var functionsClass: FunctionsClass

    private var view: MainView? = null

    lateinit var swipeDismissFrameLayout: SwipeDismissFrameLayout

    lateinit var firebaseRemoteConfig: FirebaseRemoteConfig

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        functionsClass = FunctionsClass(this@NumbersActivity, applicationContext)

        view = MainView(applicationContext, this@NumbersActivity)

        val settings = PreferenceManager.getDefaultSharedPreferences(this)
        view!!.hasSaveState = settings.getBoolean("save_state", false)

        if (savedInstanceState != null) {
            if (savedInstanceState.getBoolean("hasState")) {
                load()
            }
        }

        swipeDismissFrameLayout = SwipeDismissFrameLayout(this.applicationContext)
        swipeDismissFrameLayout.isDismissEnabled = false
        swipeDismissFrameLayout.addView(view)

        setContentView(swipeDismissFrameLayout)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    override fun onStart() {
        super.onStart()
        val resultReceiver = object : ResultReceiver(Handler()) {
            override protected fun onReceiveResult(resultCode: Int, resultData: Bundle?) {
                if (resultCode == RemoteIntent.RESULT_OK) {
                    println("RemoteIntent.RESULT_OK")
                    if (functionsClass.isFirstTimeOpen() || firebaseRemoteConfig.getBoolean(getString(R.string.booleanShowPlayStoreLinkDialogue))) {
                        ConfirmationOverlay()
                                .setMessage(firebaseRemoteConfig.getString(getString(R.string.stringPlayStoreLinkDialogue)))
                                .setDuration(1000 * 1)
                                .showOn(this@NumbersActivity)

                        functionsClass.savePreference(".UserState", "FirstTime", false)
                    }
                } else if (resultCode == RemoteIntent.RESULT_FAILED) {
                    println("RemoteIntent.RESULT_FAILED")
                } else {
                    println("Unexpected Result $resultCode")
                }
            }
        }

        firebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
        val configSettings = FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(BuildConfig.DEBUG)
                .build()
        firebaseRemoteConfig.setConfigSettings(configSettings)
        firebaseRemoteConfig.setDefaults(R.xml.remote_config_default)
        firebaseRemoteConfig.fetch(0)
                .addOnCompleteListener(this@NumbersActivity, OnCompleteListener<Void> { task ->
                    if (task.isSuccessful) {
                        firebaseRemoteConfig.activate().addOnSuccessListener {
                            if (firebaseRemoteConfig.getLong(getString(R.string.integerVersionCodeNewUpdatePhone)) > functionsClass.appVersionCode(packageName)) {
                                Toast.makeText(applicationContext, getString(R.string.updateAvailable), Toast.LENGTH_LONG).show()
                                functionsClass.notificationCreator(
                                        getString(R.string.updateAvailable),
                                        firebaseRemoteConfig.getString(getString(R.string.stringUpcomingChangeLogSummaryPhone)),
                                        firebaseRemoteConfig.getLong(getString(R.string.integerVersionCodeNewUpdatePhone)).toInt()
                                )
                            }
                            if (functionsClass.isFirstTimeOpen() || firebaseRemoteConfig.getBoolean(getString(R.string.booleanPlayStoreLink))) {
                                val intentPlayStore = Intent(Intent.ACTION_VIEW)
                                        .addCategory(Intent.CATEGORY_BROWSABLE)
                                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                        .setData(Uri.parse(firebaseRemoteConfig.getString(getString(R.string.stringPlayStoreLink))))
                                RemoteIntent.startRemoteActivity(
                                        applicationContext,
                                        intentPlayStore,
                                        resultReceiver)
                            }
                        }
                    } else {

                    }
                })
    }

    override fun onResume() {
        super.onResume()
        load()
    }

    override fun onPause() {
        super.onPause()
        save()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            //Do nothing
            return true
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
            view!!.game.move(2)
            return true
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
            view!!.game.move(0)
            return true
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
            view!!.game.move(3)
            return true
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
            view!!.game.move(1)
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    public override fun onSaveInstanceState(savedInstanceState: Bundle) {
        savedInstanceState.putBoolean("hasState", true)
        save()
    }

    private fun save() {
        val settings = PreferenceManager.getDefaultSharedPreferences(this)
        val editor = settings.edit()
        val field = view!!.game.grid!!.field
        val undoField = view!!.game.grid!!.undoField
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
        editor.putLong(SCORE, view!!.game.score)
        editor.putLong(HIGH_SCORE, view!!.game.highScore)
        editor.putLong(UNDO_SCORE, view!!.game.lastScore)
        editor.putBoolean(CAN_UNDO, view!!.game.canUndo)
        editor.putInt(GAME_STATE, view!!.game.gameState)
        editor.putInt(UNDO_GAME_STATE, view!!.game.lastGameState)
        editor.apply()
    }

    private fun load() {
        //Stopping all animations
        view!!.game.aGrid.cancelAnimations()

        val settings = PreferenceManager.getDefaultSharedPreferences(this)
        for (xx in view!!.game.grid!!.field.indices) {
            for (yy in view!!.game.grid!!.field[0].size until view!!.game.grid!!.field[0].size) {
                val value = settings.getInt(xx.toString() + " " + yy, -1)
                if (value > 0) {
                    view!!.game.grid!!.field[xx][yy] = Tile(xx, yy, value)
                } else if (value == 0) {
                    view!!.game.grid!!.field[xx][yy] = null
                }

                val undoValue = settings.getInt("$UNDO_GRID$xx $yy", -1)
                if (undoValue > 0) {
                    view!!.game.grid!!.undoField[xx][yy] = Tile(xx, yy, undoValue)
                } else if (value == 0) {
                    view!!.game.grid!!.undoField[xx][yy] = null
                }
            }
        }

        view!!.game.score = settings.getLong(SCORE, view!!.game.score)
        view!!.game.highScore = settings.getLong(HIGH_SCORE, view!!.game.highScore)
        view!!.game.lastScore = settings.getLong(UNDO_SCORE, view!!.game.lastScore)
        view!!.game.canUndo = settings.getBoolean(CAN_UNDO, view!!.game.canUndo)
        view!!.game.gameState = settings.getInt(GAME_STATE, view!!.game.gameState)
        view!!.game.lastGameState = settings.getInt(UNDO_GAME_STATE, view!!.game.lastGameState)
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
