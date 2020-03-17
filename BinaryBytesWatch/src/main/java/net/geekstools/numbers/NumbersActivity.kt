package net.geekstools.numbers

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.ResultReceiver
import android.support.wearable.view.ConfirmationOverlay
import android.view.KeyEvent
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import androidx.wear.ambient.AmbientModeSupport
import com.google.android.wearable.intent.RemoteIntent
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import net.geekstools.numbers.GameData.GamePlayData
import net.geekstools.numbers.GameView.GamePlayView
import net.geekstools.numbers.Util.Functions.FunctionsClass

class NumbersActivity : AppCompatActivity(), AmbientModeSupport.AmbientCallbackProvider  {

    private lateinit var functionsClass: FunctionsClass


    private lateinit var ambientController: AmbientModeSupport.AmbientController


    private lateinit var gamePlayView: GamePlayView

    private lateinit var gamePlayData: GamePlayData


    lateinit var firebaseRemoteConfig: FirebaseRemoteConfig


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ambientController = AmbientModeSupport.attach(this)
        ambientController.setAmbientOffloadEnabled(true)

        functionsClass = FunctionsClass(applicationContext)

        gamePlayView = GamePlayView(applicationContext, this@NumbersActivity)

        gamePlayData = GamePlayData(applicationContext, gamePlayView)
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        gamePlayView.hasSaveState = sharedPreferences.getBoolean("save_state", false)

        if (savedInstanceState != null) {
            if (savedInstanceState.getBoolean("hasState")) {
                gamePlayData.load()
            }
        }

        setContentView(gamePlayView)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    override fun onStart() {
        super.onStart()

        val resultReceiver = object : ResultReceiver(Handler()) {

            override fun onReceiveResult(resultCode: Int, resultData: Bundle?) {

                if (resultCode == RemoteIntent.RESULT_OK) {
                    if (functionsClass.isFirstTimeOpen() || firebaseRemoteConfig.getBoolean(getString(R.string.booleanShowPlayStoreLinkDialogue))) {

                        ConfirmationOverlay()
                                .setMessage(firebaseRemoteConfig.getString(getString(R.string.stringPlayStoreLinkDialogue)))
                                .setDuration(1000 * 1)
                                .showOn(this@NumbersActivity)

                        functionsClass.savePreference(".UserState", "FirstTime", false)
                    }
                } else if (resultCode == RemoteIntent.RESULT_FAILED) {

                } else {

                }
            }
        }

        firebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
        firebaseRemoteConfig.fetch(0)
                .addOnCompleteListener(this@NumbersActivity) { task ->

                    if (task.isSuccessful) {

                        firebaseRemoteConfig.activate().addOnSuccessListener {

                            if (firebaseRemoteConfig.getLong(getString(R.string.integerVersionCodeNewUpdatePhone)) > BuildConfig.VERSION_CODE) {
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
                    }
                }
    }

    override fun onResume() {
        super.onResume()

        gamePlayData.load()
    }

    override fun onPause() {
        super.onPause()

        gamePlayData.save()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            //Do nothing
            return true
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
            gamePlayView.gameLogic.move(2)
            return true
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
            gamePlayView.gameLogic.move(0)
            return true
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
            gamePlayView.gameLogic.move(3)
            return true
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
            gamePlayView.gameLogic.move(1)
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onSaveInstanceState(savedInstanceState: Bundle) {
        super.onSaveInstanceState(savedInstanceState)
        savedInstanceState.putBoolean("hasState", true)

        gamePlayData.save()
    }

    override fun getAmbientCallback(): AmbientModeSupport.AmbientCallback {

        return AmbientCallbackBrowseCategoryView()
    }

    private class AmbientCallbackBrowseCategoryView : AmbientModeSupport.AmbientCallback() {

        override fun onEnterAmbient(ambientDetails: Bundle?) {

        }

        override fun onExitAmbient() {

        }

        override fun onUpdateAmbient() {

        }
    }
}
