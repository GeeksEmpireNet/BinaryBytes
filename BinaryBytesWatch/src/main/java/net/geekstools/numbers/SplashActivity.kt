package net.geekstools.numbers

import android.app.ActivityOptions
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.*
import net.geekstools.numbers.databinding.SplashViewBinding

class SplashActivity : AppCompatActivity() {

    private lateinit var splashViewBinding: SplashViewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        splashViewBinding = SplashViewBinding.inflate(layoutInflater)
        setContentView(splashViewBinding.root)

        initialGuideScreen()
    }

    private fun initialGuideScreen() = CoroutineScope(SupervisorJob() + Dispatchers.Main).launch {
        delay(200)

        splashViewBinding.left.background = getDrawable(R.drawable.tile_two)
        splashViewBinding.right.background = getDrawable(R.drawable.tile_two)

        val face = Typeface.createFromAsset(assets, "upcil.ttf")
        splashViewBinding.appName.typeface = face

        val leftToRightAnimation = AnimationUtils.loadAnimation(applicationContext, R.anim.left_to_right)
        val rightToLeftAnimation = AnimationUtils.loadAnimation(applicationContext, R.anim.right_to_left)
        val animationScaleUpDown = AnimationUtils.loadAnimation(applicationContext, R.anim.scales_up_down)

        splashViewBinding.left.startAnimation(leftToRightAnimation)
        splashViewBinding.right.startAnimation(rightToLeftAnimation)

        leftToRightAnimation.setAnimationListener(object : Animation.AnimationListener {

            override fun onAnimationStart(animation: Animation) {

            }

            override fun onAnimationEnd(animation: Animation) {

                splashViewBinding.left.background = getDrawable(R.drawable.tile_four)
                splashViewBinding.left.startAnimation(animationScaleUpDown)
                animationScaleUpDown.setAnimationListener(object : Animation.AnimationListener {

                    override fun onAnimationStart(animation: Animation) {

                    }

                    override fun onAnimationEnd(animation: Animation) {

                        CoroutineScope(Dispatchers.Main).launch {
                            delay(500)

                            startActivity(Intent(applicationContext, NumbersActivity::class.java).apply {
                                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            }, ActivityOptions.makeCustomAnimation(applicationContext, R.anim.right_to_left, 0).toBundle())

                            this@SplashActivity.finish()
                        }
                    }

                    override fun onAnimationRepeat(animation: Animation) {

                    }
                })
            }

            override fun onAnimationRepeat(animation: Animation) {

            }
        })
    }
}
