package net.geekstools.numbers

import android.os.Bundle
import android.view.KeyEvent
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import net.geekstools.numbers.GameData.GamePlayData
import net.geekstools.numbers.GameView.GamePlayView
import net.geekstools.numbers.Util.Functions.FunctionsClassCheckpoint
import net.geekstools.numbers.Util.Functions.FunctionsClassGuide
import net.geekstools.numbers.Util.Functions.FunctionsClassUI
import net.geekstools.numbers.Util.Functions.PublicVariable
import net.geekstools.numbers.databinding.GameActivityBinding
import net.geekstools.trexrunner.Util.AdsInterface
import net.geekstools.trexrunner.Util.FunctionsClassAds

class NumbersActivity : AppCompatActivity() {

    val functionsClassGuide: FunctionsClassGuide by lazy {
        FunctionsClassGuide(applicationContext)
    }

    val functionsClassCheckpoint: FunctionsClassCheckpoint by lazy {
        FunctionsClassCheckpoint(applicationContext)
    }

    val functionsClassUI: FunctionsClassUI by lazy {
        FunctionsClassUI(applicationContext)
    }

    val functionsClassAds: FunctionsClassAds by lazy {
        FunctionsClassAds(applicationContext)
    }

    private lateinit var gamePlayView: GamePlayView


    lateinit var gameActivityBinding: GameActivityBinding


    private lateinit var gamePlayData: GamePlayData


    private lateinit var firebaseRemoteConfig: FirebaseRemoteConfig

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        gameActivityBinding = GameActivityBinding.inflate(layoutInflater)
        setContentView(gameActivityBinding.root)

        PublicVariable.eligibleToLoadShowAds = true

        setupAds()

        functionsClassGuide.guideScreen(gameActivityBinding, false)

        gamePlayView = GamePlayView(applicationContext, this@NumbersActivity, object : AdsInterface {

            override fun showInterstitialAd() {

                if (functionsClassAds.interstitialAd.isLoaded) {
                    functionsClassAds.interstitialAd.show()
                }
            }
        })

        gamePlayData = GamePlayData(applicationContext, gamePlayView)
        gamePlayView.hasSaveState = PreferenceManager.getDefaultSharedPreferences(applicationContext).getBoolean("save_state", false)

        if (savedInstanceState != null) {
            if (savedInstanceState.getBoolean("hasState")) {
                gamePlayData.load()
            }
        }

        gameActivityBinding.gamePlayViewHolder.addView(gamePlayView)
    }

    override fun onResume() {
        super.onResume()

        gamePlayData.load()

        adsCheckpoint()

        firebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
        firebaseRemoteConfig.fetch(0)
                .addOnCompleteListener(this@NumbersActivity, OnCompleteListener<Void> { task ->

                    if (task.isSuccessful) {

                        firebaseRemoteConfig.activate().addOnSuccessListener {
                            if (firebaseRemoteConfig.getLong(getString(R.string.integerVersionCodeNewUpdatePhone)) > BuildConfig.VERSION_CODE) {
                                functionsClassUI.notificationCreator(
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

    override fun onPause() {
        super.onPause()
        PublicVariable.eligibleToLoadShowAds = false

        gamePlayData.save()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {

        return when (keyCode) {
            KeyEvent.KEYCODE_MENU -> {
                //Do nothing
                true
            }
            KeyEvent.KEYCODE_DPAD_DOWN -> {
                gamePlayView.gameLogic.move(2)
                true
            }
            KeyEvent.KEYCODE_DPAD_UP -> {
                gamePlayView.gameLogic.move(0)
                true
            }
            KeyEvent.KEYCODE_DPAD_LEFT -> {
                gamePlayView.gameLogic.move(3)
                true
            }
            KeyEvent.KEYCODE_DPAD_RIGHT -> {
                gamePlayView.gameLogic.move(1)
                true
            }
            else -> {
                //Do nothing
                super.onKeyDown(keyCode, event)
            }
        }
    }

    override fun onSaveInstanceState(savedInstanceState: Bundle) {
        super.onSaveInstanceState(savedInstanceState)
        savedInstanceState.putBoolean("hasState", true)

        gamePlayData.save()
    }
}