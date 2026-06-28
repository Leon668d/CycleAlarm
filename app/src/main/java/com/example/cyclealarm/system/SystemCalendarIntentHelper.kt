package com.example.cyclealarm.system

import android.content.Context
import android.content.Intent
import android.provider.CalendarContract
import android.widget.Toast
import com.example.cyclealarm.model.LoopAlarm
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId

object SystemCalendarIntentHelper {
    fun openCalendarInsert(context: Context, alarm: LoopAlarm) {
        val dueDate = LocalDate.parse(alarm.nextDueDate)
        val reminderTime = LocalTime.parse(alarm.reminderTime)
        val zoneId = ZoneId.systemDefault()

        val startMillis = dueDate
            .atTime(reminderTime)
            .atZone(zoneId)
            .toInstant()
            .toEpochMilli()

        val endMillis = dueDate
            .atTime(reminderTime.plusMinutes(30))
            .atZone(zoneId)
            .toInstant()
            .toEpochMilli()

        val intent = Intent(Intent.ACTION_INSERT).apply {
            data = CalendarContract.Events.CONTENT_URI
            putExtra(CalendarContract.Events.TITLE, alarm.title)
            putExtra(CalendarContract.Events.DESCRIPTION, alarm.note ?: "")
            putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startMillis)
            putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endMillis)
        }

        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(intent)
        } else {
            Toast.makeText(context, "未找到可用的系统日历应用", Toast.LENGTH_SHORT).show()
        }
    }
}
