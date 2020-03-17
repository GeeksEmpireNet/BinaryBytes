package net.geekstools.numbers.Util.Functions

import android.content.Context
import android.graphics.Typeface
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.geekstools.numbers.R
import net.geekstools.numbers.databinding.GameActivityBinding

class FunctionsClassGuide (private var context: Context) {

    /*Guide*/
    fun guideScreen(gameActivityBinding: GameActivityBinding, forceShow: Boolean) = CoroutineScope(Dispatchers.Main).launch {
        if (forceShow) {
            gameActivityBinding.guide.startAnimation(AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left))
        }
        gameActivityBinding.guide.visibility = View.VISIBLE
        gameActivityBinding.left.background = context.getDrawable(R.drawable.tile_two)
        gameActivityBinding.right.background = context.getDrawable(R.drawable.tile_two)

        val typeFace: Typeface = Typeface.createFromAsset(context.assets, "upcil.ttf")
        gameActivityBinding.appName.typeface = typeFace

        val leftToRightAnimation = AnimationUtils.loadAnimation(context, R.anim.left_to_right)
        val rightToLeftAnimation = AnimationUtils.loadAnimation(context, R.anim.right_to_left)
        val animationScaleUpDown = AnimationUtils.loadAnimation(context, R.anim.scales_up_down)

        val slideOut = AnimationUtils.loadAnimation(context, android.R.anim.slide_out_right)

        delay(200)
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

                        gameActivityBinding.guide.startAnimation(slideOut)
                        slideOut.setAnimationListener(object : Animation.AnimationListener {
                            override fun onAnimationStart(animation: Animation) {

                            }

                            override fun onAnimationEnd(animation: Animation) {
                                gameActivityBinding.guide.visibility = View.GONE
                            }

                            override fun onAnimationRepeat(animation: Animation) {

                            }
                        })
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