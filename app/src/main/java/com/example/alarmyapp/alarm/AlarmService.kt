package com.example.alarmyapp.alarm

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
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
import com.example.alarmyapp.data.model.SoundType
import kotlin.math.sin

class AlarmService : Service() {
    companion object {
        private const val TAG = "AlarmService"
        private const val CHANNEL_ID = "reminder_channel"
        private const val NOTIFICATION_ID = 1001
        
        // Tweet beep sound parameters
        private const val SAMPLE_RATE = 44100
        private const val BEEP_FREQUENCY = 2500.0  // Hz - high pitched like a watch
        private const val BEEP_DURATION_MS = 100   // Short beep
        private const val BEEP_PAUSE_MS = 400      // Pause between beeps
    }
    
    private var mediaPlayer: MediaPlayer? = null
    private var audioTrack: AudioTrack? = null
    private var vibrator: Vibrator? = null
    private var stopHandler: Handler? = null
    private var beepHandler: Handler? = null
    private var alarmId: Int = -1
    private var alarmLabel: String? = null
    private var durationSeconds: Int = 30
    private var soundType: String = SoundType.TWEET
    private var startTimeMs: Long = 0
    private var isPlaying: Boolean = false
    
    // Volume button receiver to stop alarm
    private val volumeButtonReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == "android.media.VOLUME_CHANGED_ACTION" && isPlaying) {
                Log.d(TAG, "Volume button pressed - stopping alarm")
                stopAlarm()
            }
        }
    }
    private var isReceiverRegistered = false

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
        stopHandler = Handler(Looper.getMainLooper())
        beepHandler = Handler(Looper.getMainLooper())
        
        // Register volume button receiver
        registerVolumeButtonReceiver()
    }
    
    private fun registerVolumeButtonReceiver() {
        if (!isReceiverRegistered) {
            val filter = IntentFilter("android.media.VOLUME_CHANGED_ACTION")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                registerReceiver(volumeButtonReceiver, filter, Context.RECEIVER_EXPORTED)
            } else {
                registerReceiver(volumeButtonReceiver, filter)
            }
            isReceiverRegistered = true
        }
    }
    
    private fun unregisterVolumeButtonReceiver() {
        if (isReceiverRegistered) {
            try {
                unregisterReceiver(volumeButtonReceiver)
            } catch (e: Exception) {
                Log.e(TAG, "Error unregistering receiver", e)
            }
            isReceiverRegistered = false
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            alarmId = it.getIntExtra("alarm_id", -1)
            alarmLabel = it.getStringExtra("alarm_label")
            durationSeconds = it.getIntExtra("duration_seconds", 30)
            soundType = it.getStringExtra("sound_type") ?: SoundType.TWEET
            startTimeMs = System.currentTimeMillis()
            isPlaying = true

            startForeground(NOTIFICATION_ID, createReminderNotification())
            
            // Start playing sound based on type
            when (soundType) {
                SoundType.SILENT -> { /* No sound */ }
                SoundType.TWEET -> playTweetBeepLoop()
                else -> playSoundLoop()
            }
            
            vibrateLoop()
            
            // Schedule auto-stop after duration
            stopHandler?.postDelayed({
                stopAlarm()
            }, durationSeconds * 1000L)
        }
        
        return START_NOT_STICKY
    }

    private fun playTweetBeepLoop() {
        if (!isPlaying) return
        
        // Generate and play a short beep
        Thread {
            try {
                val numSamples = (SAMPLE_RATE * BEEP_DURATION_MS / 1000.0).toInt()
                val samples = ShortArray(numSamples)
                
                // Generate sine wave with fade in/out for smoother sound
                for (i in 0 until numSamples) {
                    val t = i.toDouble() / SAMPLE_RATE
                    var amplitude = sin(2.0 * Math.PI * BEEP_FREQUENCY * t)
                    
                    // Fade in first 10%
                    val fadeIn = i.toDouble() / (numSamples * 0.1)
                    if (fadeIn < 1.0) amplitude *= fadeIn
                    
                    // Fade out last 10%
                    val fadeOut = (numSamples - i).toDouble() / (numSamples * 0.1)
                    if (fadeOut < 1.0) amplitude *= fadeOut
                    
                    samples[i] = (amplitude * Short.MAX_VALUE * 0.7).toInt().toShort()
                }
                
                val bufferSize = AudioTrack.getMinBufferSize(
                    SAMPLE_RATE,
                    AudioFormat.CHANNEL_OUT_MONO,
                    AudioFormat.ENCODING_PCM_16BIT
                )
                
                audioTrack?.release()
                audioTrack = AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_ALARM)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .build()
                    )
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setSampleRate(SAMPLE_RATE)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .build()
                    )
                    .setBufferSizeInBytes(bufferSize.coerceAtLeast(samples.size * 2))
                    .setTransferMode(AudioTrack.MODE_STATIC)
                    .build()
                
                audioTrack?.write(samples, 0, samples.size)
                audioTrack?.play()
                
            } catch (e: Exception) {
                Log.e(TAG, "Error playing tweet beep", e)
            }
        }.start()
        
        // Schedule next beep
        beepHandler?.postDelayed({
            if (isPlaying) {
                playTweetBeepLoop()
            }
        }, BEEP_DURATION_MS.toLong() + BEEP_PAUSE_MS)
    }

    private fun playSoundLoop() {
        try {
            val soundUri = when (soundType) {
                SoundType.NOTIFICATION -> RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                SoundType.ALARM -> RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
                SoundType.CHIME -> RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                else -> RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            } ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)

            mediaPlayer?.release()
            mediaPlayer = MediaPlayer().apply {
                setDataSource(this@AlarmService, soundUri)
                
                val attributes = AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
                setAudioAttributes(attributes)
                
                isLooping = true
                setVolume(1.0f, 1.0f)
                
                prepare()
                start()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error playing sound", e)
        }
    }

    private fun vibrateLoop() {
        vibrator?.let { vib ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                // Vibrate pattern: 500ms on, 500ms off, repeating
                val timings = longArrayOf(0, 500, 500)
                val amplitudes = intArrayOf(0, VibrationEffect.DEFAULT_AMPLITUDE, 0)
                val effect = VibrationEffect.createWaveform(timings, amplitudes, 0) // 0 = repeat
                vib.vibrate(effect)
            } else {
                @Suppress("DEPRECATION")
                vib.vibrate(longArrayOf(0, 500, 500), 0) // 0 = repeat
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
        
        val durationText = if (durationSeconds >= 60) {
            "${durationSeconds / 60}m"
        } else {
            "${durationSeconds}s"
        }

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_alarm)
            .setContentTitle(alarmLabel ?: "Reminder")
            .setContentText("Playing for $durationText")
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
        isPlaying = false
        
        // Unregister volume button receiver
        unregisterVolumeButtonReceiver()
        
        mediaPlayer?.apply {
            stop()
            release()
        }
        mediaPlayer = null
        
        audioTrack?.apply {
            stop()
            release()
        }
        audioTrack = null

        vibrator?.cancel()
        stopHandler?.removeCallbacksAndMessages(null)
        beepHandler?.removeCallbacksAndMessages(null)

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
        unregisterVolumeButtonReceiver()
        stopAlarm()
    }
}