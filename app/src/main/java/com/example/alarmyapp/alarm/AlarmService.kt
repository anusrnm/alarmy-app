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
        private const val CHANNEL_ID = "reminder_channel"
        private const val NOTIFICATION_ID = 1001
        private const val RING_DURATION_MS = 2000L // Each ring lasts 2 seconds
        private const val RING_PAUSE_MS = 1000L // Pause between rings
    }
    
    private var mediaPlayer: MediaPlayer? = null
    private var vibrator: Vibrator? = null
    private var ringHandler: Handler? = null
    private var alarmId: Int = -1
    private var alarmLabel: String? = null
    private var ringCount: Int = 3
    private var currentRing: Int = 0

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vm = getSystemService(VibratorManager::class.java)
            vm?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
        ringHandler = Handler(Looper.getMainLooper())
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            alarmId = it.getIntExtra("alarm_id", -1)
            alarmLabel = it.getStringExtra("alarm_label")
            ringCount = it.getIntExtra("ring_count", 3)
            currentRing = 0

            startForeground(NOTIFICATION_ID, createReminderNotification())
            
            // Start the ring sequence
            playRingSequence()
        }
        
        return START_NOT_STICKY
    }

    private fun playRingSequence() {
        if (currentRing >= ringCount) {
            // Done ringing, stop the service
            stopAlarm()
            return
        }

        currentRing++
        
        // Play sound and vibrate
        playRingSound()
        vibrate()
        
        // Schedule next ring or stop
        ringHandler?.postDelayed({
            mediaPlayer?.stop()
            mediaPlayer?.reset()
            
            if (currentRing < ringCount) {
                // Wait a bit then ring again
                ringHandler?.postDelayed({
                    playRingSequence()
                }, RING_PAUSE_MS)
            } else {
                stopAlarm()
            }
        }, RING_DURATION_MS)
    }

    private fun playRingSound() {
        try {
            val alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)

            mediaPlayer?.release()
            mediaPlayer = MediaPlayer().apply {
                setDataSource(this@AlarmService, alarmSound)
                
                val attributes = AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
                setAudioAttributes(attributes)
                
                isLooping = false
                setVolume(1.0f, 1.0f)
                
                prepare()
                start()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error playing ring sound", e)
        }
    }

    private fun vibrate() {
        vibrator?.let { vib ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val effect = VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE)
                vib.vibrate(effect)
            } else {
                @Suppress("DEPRECATION")
                vib.vibrate(500)
            }
        }
    }

    private fun createReminderNotification(): Notification {
        val mainIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val mainPendingIntent = PendingIntent.getActivity(
            this, 0, mainIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val dismissIntent = Intent(this, AlarmActionReceiver::class.java).apply {
            action = "DISMISS_ALARM"
            putExtra("alarm_id", alarmId)
        }
        val dismissPendingIntent = PendingIntent.getBroadcast(
            this, alarmId, dismissIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_alarm)
            .setContentTitle(alarmLabel ?: "Reminder")
            .setContentText("Ringing $currentRing of $ringCount")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setAutoCancel(true)
            .setOngoing(true)
            .setContentIntent(mainPendingIntent)
            .setFullScreenIntent(mainPendingIntent, true)
            .addAction(R.drawable.ic_stop, getString(R.string.dismiss), dismissPendingIntent)
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Reminders",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Reminder notifications"
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
        ringHandler?.removeCallbacksAndMessages(null)

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