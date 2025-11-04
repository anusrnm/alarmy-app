package com.example.alarmyapp.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.alarmyapp.data.database.AlarmDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BootReceiver : BroadcastReceiver() {
    companion object {
        private const val TAG = "BootReceiver"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        Log.d(TAG, "Boot receiver action: $action")

        if (action == Intent.ACTION_BOOT_COMPLETED ||
            action == Intent.ACTION_MY_PACKAGE_REPLACED ||
            action == Intent.ACTION_PACKAGE_REPLACED) {
            
            // Reschedule all enabled alarms after boot
            rescheduleAlarms(context)
        }
    }

    private fun rescheduleAlarms(context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val database = AlarmDatabase.getDatabase(context)
                val enabledAlarms = database.alarmDao().getEnabledAlarms()
                
                val scheduler = AlarmScheduler(context)
                enabledAlarms.forEach { alarm ->
                    scheduler.scheduleAlarm(alarm)
                    Log.d(TAG, "Rescheduled alarm: ${alarm.label}")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error rescheduling alarms", e)
            }
        }
    }
}