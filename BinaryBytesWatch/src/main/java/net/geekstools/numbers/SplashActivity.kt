package net.geekstools.numbers

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView

class SplashActivity : Activity() {
    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_view)

        guideScreen()
    }

    private fun guideScreen() {
        val guide = findViewById<View>(R.id.guide) as RelativeLayout
        guide.visibility = View.VISIBLE
        val arrows = findViewById<View>(R.id.arrows) as ImageView
        val dots = findViewById<View>(R.id.dots) as ImageView
        val left = findViewById<View>(R.id.left) as ImageView
        left.background = getDrawable(R.drawable.tile_two)
        val right = findViewById<View>(R.id.right) as ImageView
        right.background = getDrawable(R.drawable.tile_two)
        val appName = findViewById<View>(R.id.appName) as TextView

        val face = Typeface.createFromAsset(assets, "upcil.ttf")
        appName.typeface = face

        Handler().postDelayed({
            val leftAnim = AnimationUtils.loadAnimation(applicationContext, R.anim.ltr)
            val rightAnim = AnimationUtils.loadAnimation(applicationContext, R.anim.rtl)
            val animScale = AnimationUtils.loadAnimation(applicationContext, R.anim.scales)
            val slideOut = AnimationUtils.loadAnimation(applicationContext, android.R.anim.slide_out_right)

            left.startAnimation(leftAnim)
            right.startAnimation(rightAnim)
            leftAnim.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation) {}

                override fun onAnimationEnd(animation: Animation) {
                    left.background = getDrawable(R.drawable.tile_four)
                    left.startAnimation(animScale)
                    animScale.setAnimationListener(object : Animation.AnimationListener {
                        override fun onAnimationStart(animation: Animation) {}

                        override fun onAnimationEnd(animation: Animation) {
                            Handler().postDelayed({
                                guide.startAnimation(slideOut)
                                slideOut.setAnimationListener(object : Animation.AnimationListener {
                                    override fun onAnimationStart(animation: Animation) {
                                        startActivity(Intent(applicationContext, NumbersActivity::class.java))
                                        this@SplashActivity.finish()
                                    }

                                    override fun onAnimationEnd(animation: Animation) {}

                                    override fun onAnimationRepeat(animation: Animation) {}
                                })
                            }, 250)
                        }

                        override fun onAnimationRepeat(animation: Animation) {}
                    })
                }

                override fun onAnimationRepeat(animation: Animation) {}
            })
        }, 170)
    }
}
