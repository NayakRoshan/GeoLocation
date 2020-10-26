package com.roshan.mylocation.handlers

import android.content.Context

class PreferenceHandler(context: Context) {

    private val PREFERENCE_NAME : String = "LocationApp Preference"
    private val USER_ASKED_LOCATION_PERMISSION : String = "First Time Permission"
    private val sharedPreferences by lazy { context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE) }

    fun permissionAskedFirstTime() {
        val editor = sharedPreferences.edit()
        editor.putBoolean(USER_ASKED_LOCATION_PERMISSION, true)
        editor.apply()
    }

    fun getAskedPermissionFirstTimeStatus() = sharedPreferences.getBoolean(USER_ASKED_LOCATION_PERMISSION, false)

}