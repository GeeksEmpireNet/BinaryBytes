package net.geekstools.numbers.Util.Functions

import android.content.Context
import android.os.Build
import android.telephony.TelephonyManager
import java.io.FileOutputStream

class FunctionsClassCheckpoint(var context: Context) {

    /*Files*/
    fun saveFile(fileName: String, fileContent: String) {
        try {
            val fileOutputStream: FileOutputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE)
            fileOutputStream.write((fileContent).toByteArray())

            fileOutputStream.flush()
            fileOutputStream.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun savePreference(PreferenceName: String, KEY: String, VALUE: Int) {
        val sharedPreferences = context.getSharedPreferences(PreferenceName, Context.MODE_PRIVATE)
        val editorSharedPreferences = sharedPreferences.edit()
        editorSharedPreferences.putInt(KEY, VALUE)
        editorSharedPreferences.apply()
    }

    fun savePreference(PreferenceName: String, KEY: String, VALUE: Long) {
        val sharedPreferences = context.getSharedPreferences(PreferenceName, Context.MODE_PRIVATE)
        val editorSharedPreferences = sharedPreferences.edit()
        editorSharedPreferences.putLong(KEY, VALUE)
        editorSharedPreferences.apply()
    }

    fun savePreference(PreferenceName: String, KEY: String, VALUE: Boolean) {
        val sharedPreferences = context.getSharedPreferences(PreferenceName, Context.MODE_PRIVATE)
        val editorSharedPreferences = sharedPreferences.edit()
        editorSharedPreferences.putBoolean(KEY, VALUE)
        editorSharedPreferences.apply()
    }

    fun readPreference(PreferenceName: String, KEY: String, defaultVALUE: Int): Int {
        return context.getSharedPreferences(PreferenceName, Context.MODE_PRIVATE).getInt(KEY, defaultVALUE)
    }
    fun readPreference(PreferenceName: String, KEY: String, defaultVALUE: Boolean): Boolean {
        return context.getSharedPreferences(PreferenceName, Context.MODE_PRIVATE).getBoolean(KEY, defaultVALUE)
    }

    /*System Checkpoint*/
    fun getDeviceName(): String {
        val manufacturer: String = Build.MANUFACTURER
        val model: String = Build.MODEL
        if (model.startsWith(manufacturer)) {
            return capitalizeFirstChar(manufacturer)
        } else {
            return capitalizeFirstChar(manufacturer) + " " + model
        }
    }

    private fun capitalizeFirstChar(someText: String?) : String {

        return if (someText == null || someText.isEmpty()) {
            ""
        } else if (Character.isUpperCase(someText[0])) {
            someText
        } else {
            Character.toUpperCase(someText[0]) + someText.substring(1)
        }
    }

    fun getCountryIso(): String {
        var countryISO = "Undefined"
        try {
            val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            countryISO = telephonyManager.simCountryIso
        } catch (e: Exception) {
            countryISO = "Undefined"
        }
        return countryISO
    }
}