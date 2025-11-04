package com.example.alarmyapp.alarm

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.alarmyapp.MainActivity
import com.example.alarmyapp.R

class AlarmService : Service() {
    companion object {
        private const val TAG = "AlarmService"
        private const val CHANNEL_ID = "alarm_channel"
        private const val NOTIFICATION_ID = 1001
    }
    
    private var mediaPlayer: MediaPlayer? = null
    private var vibrator: Vibrator? = null
    private var stopHandler: Handler? = null
    private var alarmId: Int = -1
    private var alarmLabel: String? = null
    private var snoozeEnabled: Boolean = true
    private var snoozeInterval: Int = 5

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        // Acquire vibrator in a backwards-compatible way (VibratorManager from API 31+)
        vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vm = getSystemService(VibratorManager::class.java)
            vm?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
        stopHandler = Handler(Looper.getMainLooper())
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            alarmId = it.getIntExtra("alarm_id", -1)
            alarmLabel = it.getStringExtra("alarm_label")
            val soundUri = it.getStringExtra("sound_uri") ?: "default"
            val volume = it.getIntExtra("volume", 80)
            val vibrationPattern = it.getIntExtra("vibration_pattern", 2)
            val durationMinutes = it.getIntExtra("duration_minutes", 5)
            snoozeEnabled = it.getBooleanExtra("snooze_enabled", true)
            snoozeInterval = it.getIntExtra("snooze_interval", 5)

            startForeground(NOTIFICATION_ID, createAlarmNotification())
            
            // Start playing alarm sound
            playAlarmSound(soundUri, volume)
            
            // Start vibration
            startVibration(vibrationPattern)
            
            // Auto-stop after duration
            stopHandler?.postDelayed({
                stopAlarm()
            }, durationMinutes * 60 * 1000L)
        }
        
        return START_NOT_STICKY
    }

    private fun playAlarmSound(soundUri: String, volume: Int) {
        try {
            val alarmSound = if (soundUri == "default") {
                RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
                    ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            } else {
                Uri.parse(soundUri)
            }

            mediaPlayer = MediaPlayer().apply {
                setDataSource(this@AlarmService, alarmSound)
                
                val attributes = AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
                setAudioAttributes(attributes)
                
                isLooping = true
                
                // Set volume (0.0 to 1.0)
                val volumeLevel = volume / 100.0f
                setVolume(volumeLevel, volumeLevel)
                
                prepare()
                start()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error playing alarm sound", e)
        }
    }

    private fun startVibration(pattern: Int) {
        if (vibrator == null || pattern == 0) return

        val vibrationPattern = when (pattern) {
            1 -> longArrayOf(0, 200, 300, 200, 300) // Light
            2 -> longArrayOf(0, 500, 300, 500, 300) // Medium
            3 -> longArrayOf(0, 1000, 500, 1000, 500) // Strong
            else -> return
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val effect = VibrationEffect.createWaveform(vibrationPattern, 0)
            vibrator?.vibrate(effect)
        } else {
            @Suppress("DEPRECATION")
            vibrator?.vibrate(vibrationPattern, 0)
        }
    }

    private fun createAlarmNotification(): Notification {
        val mainIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val mainPendingIntent = PendingIntent.getActivity(
            this, 0, mainIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_alarm)
            .setContentTitle(getString(R.string.alarm_ringing))
            .setContentText(alarmLabel ?: "Alarm")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setAutoCancel(false)
            .setOngoing(true)
            .setContentIntent(mainPendingIntent)
            .setFullScreenIntent(mainPendingIntent, true)

        // Add dismiss action
        val dismissIntent = Intent(this, AlarmActionReceiver::class.java).apply {
            action = "DISMISS_ALARM"
            putExtra("alarm_id", alarmId)
        }
        val dismissPendingIntent = PendingIntent.getBroadcast(
            this, alarmId, dismissIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        builder.addAction(R.drawable.ic_stop, getString(R.string.dismiss), dismissPendingIntent)

        // Add snooze action if enabled
        if (snoozeEnabled) {
            val snoozeIntent = Intent(this, AlarmActionReceiver::class.java).apply {
                action = "SNOOZE_ALARM"
                putExtra("alarm_id", alarmId)
                putExtra("snooze_interval", snoozeInterval)
            }
            val snoozePendingIntent = PendingIntent.getBroadcast(
                this, alarmId + 1000, snoozeIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            builder.addAction(R.drawable.ic_snooze, getString(R.string.snooze_5_min), snoozePendingIntent)
        }

        return builder.build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                getString(R.string.alarm_channel_name),
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = getString(R.string.alarm_channel_description)
                enableVibration(true)
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            }

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun stopAlarm() {
        mediaPlayer?.apply {
            stop()
            release()
        }
        mediaPlayer = null

        vibrator?.cancel()

        stopHandler?.removeCallbacksAndMessages(null)

        // Use new stopForeground(int) API (API 33+) while keeping backward compatibility
        if (Build.VERSION.SDK_INT >= 33) {
            stopForeground(STOP_FOREGROUND_REMOVE)
        } else {
            @Suppress("DEPRECATION")
            stopForeground(true)
        }
        stopSelf()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        stopAlarm()
    }
}