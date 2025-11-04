package com.example.alarmyapp.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class AlarmActionReceiver : BroadcastReceiver() {
    companion object {
        private const val TAG = "AlarmActionReceiver"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        val alarmId = intent.getIntExtra("alarm_id", -1)
        
        Log.d(TAG, "Received action: $action for alarm: $alarmId")

        when (action) {
            "DISMISS_ALARM" -> {
                // Stop the alarm service
                val serviceIntent = Intent(context, AlarmService::class.java)
                context.stopService(serviceIntent)
            }
            "SNOOZE_ALARM" -> {
                // Stop current alarm and schedule snooze
                val serviceIntent = Intent(context, AlarmService::class.java)
                context.stopService(serviceIntent)
                
                val snoozeInterval = intent.getIntExtra("snooze_interval", 5)
                val scheduler = AlarmScheduler(context)
                scheduler.scheduleSnooze(alarmId, snoozeInterval)
            }
        }
    }
}