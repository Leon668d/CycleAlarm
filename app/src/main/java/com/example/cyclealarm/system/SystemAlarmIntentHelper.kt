package com.example.cyclealarm.system

import android.content.Context
import android.content.Intent
import android.provider.AlarmClock
import android.widget.Toast
import com.example.cyclealarm.model.LoopAlarm
import java.time.LocalTime

object SystemAlarmIntentHelper {
    fun openSystemAlarm(context: Context, alarm: LoopAlarm) {
        val reminderTime = LocalTime.parse(alarm.reminderTime)

        val intent = Intent(AlarmClock.ACTION_SET_ALARM).apply {
            putExtra(AlarmClock.EXTRA_MESSAGE, alarm.title)
            putExtra(AlarmClock.EXTRA_HOUR, reminderTime.hour)
            putExtra(AlarmClock.EXTRA_MINUTES, reminderTime.minute)
            putExtra(AlarmClock.EXTRA_VIBRATE, true)
            putExtra(AlarmClock.EXTRA_SKIP_UI, false)
        }

        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(intent)
        } else {
            Toast.makeText(context, "未找到可用的系统闹钟应用", Toast.LENGTH_SHORT).show()
        }
    }
}
