package com.example.alarmyapp.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.example.alarmyapp.data.model.Alarm
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AlarmScheduler @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    companion object {
        private const val ALARM_ACTION = "com.example.alarmyapp.ALARM_TRIGGER"
    }

    fun scheduleAlarm(alarm: Alarm): Long {
        if (!alarm.isEnabled) return -1

        val triggerTime = calculateNextTriggerTime(alarm)
        // Do not persist here; caller will update entity.

        val intent = Intent(context, AlarmReceiver::class.java).apply {
            action = ALARM_ACTION
            putExtra("alarm_id", alarm.id)
            putExtra("alarm_label", alarm.label)
            putExtra("sound_uri", alarm.soundUri)
            putExtra("volume", alarm.volume)
            putExtra("vibration_pattern", alarm.vibrationPattern)
            putExtra("duration_minutes", alarm.durationMinutes)
            putExtra("snooze_enabled", alarm.snoozeEnabled)
            putExtra("snooze_interval", alarm.snoozeIntervalMinutes)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alarm.id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerTime,
                    pendingIntent
                )
            } else {
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    triggerTime,
                    pendingIntent
                )
            }
        } catch (e: SecurityException) {
            // Fallback for devices without exact alarm permission
            alarmManager.set(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
        }
        return triggerTime
    }

    fun cancelAlarm(alarmId: Int) {
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            action = ALARM_ACTION
        }
        
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alarmId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        alarmManager.cancel(pendingIntent)
    }

    fun scheduleSnooze(alarmId: Int, snoozeMinutes: Int) {
        val calendar = Calendar.getInstance().apply {
            add(Calendar.MINUTE, snoozeMinutes)
        }
        
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            action = ALARM_ACTION
            putExtra("alarm_id", alarmId)
            putExtra("is_snooze", true)
        }
        
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alarmId + 10000, // Different ID for snooze
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    pendingIntent
                )
            } else {
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    pendingIntent
                )
            }
        } catch (e: SecurityException) {
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
        }
    }

    private fun calculateNextTriggerTime(alarm: Alarm): Long {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, alarm.hour)
            set(Calendar.MINUTE, alarm.minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        // If the time has passed today, move to tomorrow
        if (calendar.timeInMillis <= System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        // Handle repeating alarms
        if (alarm.isRepeating && alarm.repeatDays.isNotEmpty()) {
            return findNextRepeatDay(calendar, alarm.repeatDays)
        }

        return calendar.timeInMillis
    }

    private fun findNextRepeatDay(calendar: Calendar, repeatDays: Set<Int>): Long {
        val currentDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        
        // Check if today is a repeat day and time hasn't passed
        if (repeatDays.contains(currentDayOfWeek) && 
            calendar.timeInMillis > System.currentTimeMillis()) {
            return calendar.timeInMillis
        }
        
        // Find the next repeat day
        for (i in 1..7) {
            calendar.add(Calendar.DAY_OF_MONTH, 1)
            val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
            if (repeatDays.contains(dayOfWeek)) {
                return calendar.timeInMillis
            }
        }
        
        return calendar.timeInMillis
    }
}