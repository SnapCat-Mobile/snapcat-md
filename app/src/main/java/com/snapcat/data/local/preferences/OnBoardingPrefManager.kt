package com.snapcat.data.local.preferences

import android.content.Context
import android.content.SharedPreferences
import com.snapcat.util.Constants

class OnBoardingPrefManager (context: Context) {


    private val pref: SharedPreferences
    private val editor: SharedPreferences.Editor

    var isFirstTimeLaunch: Boolean
        get() {
            return pref.getBoolean(Constants.IS_FIRST_TIME_LAUNCH, true)
        }
        set(isFirstTime) {
            editor.putBoolean(Constants.IS_FIRST_TIME_LAUNCH, isFirstTime)
            editor.commit()
        }

    init {
        pref = context.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE)
        editor = pref.edit()
    }

}