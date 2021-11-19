package com.example.webviewexample

import android.content.Context
import android.content.SharedPreferences

object Utils {
    fun setPrefString(context: Context, key: String, value: String) {
        val prefs: SharedPreferences = context.getSharedPreferences(
            Url.PREFRENCE,
            Context.MODE_PRIVATE
        )
        val editor : SharedPreferences.Editor = prefs.edit()
        editor.putString(key, value)
        editor.commit()
    }

    fun getPrefString(context: Context, key: String): String? {
        val prefs: SharedPreferences = context.getSharedPreferences(
            Url.PREFRENCE,
            Context.MODE_PRIVATE
        )
        return prefs.getString(key, "")
    }
}