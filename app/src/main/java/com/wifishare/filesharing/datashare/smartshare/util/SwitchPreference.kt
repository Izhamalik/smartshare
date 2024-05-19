package com.wifishare.filesharing.datashare.smartshare.util

import android.content.Context
import android.content.SharedPreferences

class SwitchPreference(var context: Context) {

    var editor: SharedPreferences.Editor
    var pref: SharedPreferences = context.getSharedPreferences("SmartSwitch", 0)

    init {
        editor = pref.edit()
    }

    companion object {

        const val IS_IN_APP = "sharedInApp"
        const val IS_CONFIG = "myConfig"
        const val IS_AD_FREE = "isFree"
        const val IS_AD_MONTHLY = "isMonthly"
        const val IS_RATED = "isRatedDone"
        const val IS_DARK_MODE = "isDarkMode"

    }

    var selectedLanguage: String?
        get() = pref.getString(IS_CONFIG, "en")
        set(selectedLanguage) {
            editor.putString(IS_CONFIG, selectedLanguage)
            editor.apply()
        }

    var isPurchasedApp: Boolean
        get() = pref.getBoolean(IS_IN_APP, false)
        set(isPurchased) {
            editor.putBoolean(IS_IN_APP, isPurchased)
            editor.apply()
        }

    var isDarkMode: Boolean
        get() = pref.getBoolean(IS_DARK_MODE, false)
        set(isDarkMode) {
            editor.putBoolean(IS_DARK_MODE, isDarkMode)
            editor.apply()
        }

    var isMonth: Boolean
        get() = pref.getBoolean(IS_AD_MONTHLY, false)
        set(isMonthly) {
            editor.putBoolean(IS_AD_MONTHLY, isMonthly)
            editor.apply()
        }

    var isDemo: Long
        get() = pref.getLong(IS_AD_FREE, 0)
        set(isAdFree) {
            editor.putLong(IS_AD_FREE, isAdFree)
            editor.apply()
        }

    var isRated: Boolean
        get() = pref.getBoolean(IS_RATED, false)
        set(isRated) {
            editor.putBoolean(IS_RATED, isRated)
            editor.apply()
        }

}