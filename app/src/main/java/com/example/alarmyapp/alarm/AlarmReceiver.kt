package com.example.alarmyapp.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class AlarmReceiver : BroadcastReceiver() {
    companion object {
        private const val TAG = "AlarmReceiver"
    }

    override fun onReceive(context: Context, intent: Intent) {
        Log.d(TAG, "Alarm received: ${intent.action}")
        
        if (intent.action == "com.example.alarmyapp.ALARM_TRIGGER") {
            val alarmId = intent.getIntExtra("alarm_id", -1)
            val alarmLabel = intent.getStringExtra("alarm_label")
            val soundUri = intent.getStringExtra("sound_uri")
            val volume = intent.getIntExtra("volume", 80)
            val vibrationPattern = intent.getIntExtra("vibration_pattern", 2)
            val durationMinutes = intent.getIntExtra("duration_minutes", 5)
            val snoozeEnabled = intent.getBooleanExtra("snooze_enabled", true)
            val snoozeInterval = intent.getIntExtra("snooze_interval", 5)
            val isSnooze = intent.getBooleanExtra("is_snooze", false)

            if (alarmId != -1) {
                // Start the alarm service
                val serviceIntent = Intent(context, AlarmService::class.java).apply {
                    putExtra("alarm_id", alarmId)
                    putExtra("alarm_label", alarmLabel)
                    putExtra("sound_uri", soundUri)
                    putExtra("volume", volume)
                    putExtra("vibration_pattern", vibrationPattern)
                    putExtra("duration_minutes", durationMinutes)
                    putExtra("snooze_enabled", snoozeEnabled)
                    putExtra("snooze_interval", snoozeInterval)
                    putExtra("is_snooze", isSnooze)
                }
                
                context.startForegroundService(serviceIntent)
            }
        }
    }
}