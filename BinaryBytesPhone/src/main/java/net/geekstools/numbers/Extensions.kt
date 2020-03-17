package net.geekstools.numbers

import android.content.Intent
import android.os.Build
import android.text.Html
import android.view.View
import net.geekstools.numbers.Util.Functions.PublicVariable
import net.geekstools.trexrunner.Util.AdsInterface

fun NumbersActivity.setupAds() {


    functionsClassAds.initialLoadingAds(object : AdsInterface {

        override fun enableRewardedVideo() {

            val rewardedPromotionCode = functionsClassCheckpoint.readPreference(".NoAdsRewardedInfo", "RewardedPromotionCode", 0)
            if ((rewardedPromotionCode >= 33) && functionsClassCheckpoint.readPreference(".NoAdsRewardedInfo", "Requested", false)) {

                gameActivityBinding.rewardVideo.text = Html.fromHtml("<br/><font color='#f2f7ff'>" +
                        "<big>Please Click to See Video Ads to<br/>" +
                        "\uD83D\uDC8E  \uD83D\uDC8E  \uD83D\uDC8E <b>Support Geeks Empire Open Source Projects</b> \uD83D\uDC8E  \uD83D\uDC8E  \uD83D\uDC8E </big><br/>"
                        + "</font>")
            } else {
                gameActivityBinding.rewardVideo.text = Html.fromHtml("<br/><font color='#f2f7ff'>" +
                        "<big>Click to See Video Ads to Get<br/>" +
                        "\uD83D\uDC8E  \uD83D\uDC8E  \uD83D\uDC8E <b>Promotion Codes of Geeks Empire Premium Apps</b> \uD83D\uDC8E  \uD83D\uDC8E  \uD83D\uDC8E </big><br/>"
                        + "</font>")
                gameActivityBinding.rewardVideo.append("$rewardedPromotionCode / 33" + Html.fromHtml("<br/>"))
            }

            gameActivityBinding.rewardVideo.visibility = View.VISIBLE
        }

        override fun reloadRewardedVideo() {

            gameActivityBinding.rewardVideo.visibility = View.INVISIBLE
        }

        override fun rewardedPromotionCode() {

            gameActivityBinding.rewardVideo.setTextColor(getColor(R.color.light))

            gameActivityBinding.rewardVideo.text = Html.fromHtml("<br/><font color='#f2f7ff'>" +
                    "<big>Upgrade to Geeks Empire Premium Apps<br/>" +
                    "\uD83D\uDC8E  \uD83D\uDC8E  \uD83D\uDC8E <b>Rewarded to Get Promotion Codes</b> \uD83D\uDC8E  \uD83D\uDC8E  \uD83D\uDC8E </big>"
                    + "\uD83D\uDCE9 Click Here to Request \uD83D\uDCE9<br/>"
                    + "</font>")

            gameActivityBinding.rewardVideo.visibility = View.VISIBLE
        }
    })

    gameActivityBinding.rewardVideo.setOnClickListener {

        if ((functionsClassCheckpoint.readPreference(".NoAdsRewardedInfo", "RewardedPromotionCode", 0) >= 33)
                && !functionsClassCheckpoint.readPreference(".NoAdsRewardedInfo", "Requested", false)) {

            try {
                val emailTextMessage = ("\n\n\n\n\n"
                        + "[Essential Information]" + "\n"
                        + getString(R.string.app_name) + " | " + BuildConfig.VERSION_NAME + "\n"
                        + functionsClassCheckpoint.getDeviceName() + " | " + "API " + Build.VERSION.SDK_INT + " | " + functionsClassCheckpoint.getCountryIso().toUpperCase())

                val emailIntent = Intent(Intent.ACTION_SEND).apply {
                    putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.support)))
                    putExtra(Intent.EXTRA_SUBJECT, getString(R.string.rewardedPromotionCodeTitle))
                    putExtra(Intent.EXTRA_TEXT, emailTextMessage)
                    type = "text/*"
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    setPackage("com.google.android.gm")
                }
                startActivity(Intent.createChooser(emailIntent, getString(R.string.rewardedPromotionCodeTitle)))

                functionsClassCheckpoint.savePreference(".NoAdsRewardedInfo", "Requested", true)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {

            if (functionsClassAds.rewardedVideoAdInstance.isLoaded) {
                functionsClassAds.rewardedVideoAdInstance.show()
            }
        }
    }
}

fun NumbersActivity.adsCheckpoint() {
    PublicVariable.eligibleToLoadShowAds = true

    val rewardedPromotionCode = functionsClassCheckpoint.readPreference(".NoAdsRewardedInfo", "RewardedPromotionCode", 0)
    if (rewardedPromotionCode >= 33 && functionsClassCheckpoint.readPreference(".NoAdsRewardedInfo", "Requested", false)) {

        gameActivityBinding.rewardVideo.visibility = View.INVISIBLE

    } else if (rewardedPromotionCode >= 33 && !functionsClassCheckpoint.readPreference(".NoAdsRewardedInfo", "Requested", false)) {

        gameActivityBinding.rewardVideo.setTextColor(getColor(R.color.light))

        gameActivityBinding.rewardVideo.text = Html.fromHtml("<br/><font color='#f2f7ff'>" +
                "<big>Upgrade to Geeks Empire Premium Apps<br/>" +
                "\uD83D\uDC8E  \uD83D\uDC8E  \uD83D\uDC8E <b>Rewarded to Get Promotion Codes</b> \uD83D\uDC8E  \uD83D\uDC8E  \uD83D\uDC8E </big>"
                + "\uD83D\uDCE9 Click Here to Request \uD83D\uDCE9<br/>"
                + "</font>")

        gameActivityBinding.rewardVideo.visibility = View.VISIBLE
    }
}