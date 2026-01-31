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
        
        Log.d(TAG, "Received action: $action for reminder: $alarmId")

        when (action) {
            "DISMISS_ALARM" -> {
                // Stop the reminder service
                val serviceIntent = Intent(context, AlarmService::class.java)
                context.stopService(serviceIntent)
            }
        }
    }
}