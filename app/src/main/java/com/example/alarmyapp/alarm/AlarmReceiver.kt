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
        Log.d(TAG, "Reminder received: ${intent.action}")
        
        if (intent.action == "com.example.alarmyapp.ALARM_TRIGGER") {
            val alarmId = intent.getIntExtra("alarm_id", -1)
            val alarmLabel = intent.getStringExtra("alarm_label")
            val durationSeconds = intent.getIntExtra("duration_seconds", 30)

            if (alarmId != -1) {
                // Start the reminder service
                val serviceIntent = Intent(context, AlarmService::class.java).apply {
                    putExtra("alarm_id", alarmId)
                    putExtra("alarm_label", alarmLabel)
                    putExtra("duration_seconds", durationSeconds)
                }
                
                context.startForegroundService(serviceIntent)
            }
        }
    }
}