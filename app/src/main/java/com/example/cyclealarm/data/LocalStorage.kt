package com.example.cyclealarm.data

import android.content.Context

class LocalStorage(context: Context) {
    private val preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

    fun readAlarmsJson(): String {
        return preferences.getString(KEY_LOOP_ALARMS_JSON, "[]") ?: "[]"
    }

    fun writeAlarmsJson(json: String) {
        preferences.edit().putString(KEY_LOOP_ALARMS_JSON, json).apply()
    }

    companion object {
        const val PREFERENCES_NAME = "cycle_alarm_storage"
        const val KEY_LOOP_ALARMS_JSON = "loop_alarms_json"
    }
}
