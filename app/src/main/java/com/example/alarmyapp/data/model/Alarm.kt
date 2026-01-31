package com.example.alarmyapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Sound types for alarm/reminder
 */
object SoundType {
    const val TWEET = "tweet"           // Mild beep like wristwatch
    const val NOTIFICATION = "notification"  // System notification sound
    const val ALARM = "alarm"           // System alarm sound
    const val CHIME = "chime"           // Gentle chime
    const val SILENT = "silent"         // No sound, vibrate only
    
    fun getDisplayName(type: String): String = when (type) {
        TWEET -> "Tweet (Beep)"
        NOTIFICATION -> "Notification"
        ALARM -> "Alarm"
        CHIME -> "Chime"
        SILENT -> "Silent"
        else -> "Tweet (Beep)"
    }
    
    fun getAllTypes(): List<String> = listOf(TWEET, NOTIFICATION, ALARM, CHIME, SILENT)
}

@Entity(tableName = "alarms")
data class Alarm(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var hour: Int = 7,
    var minute: Int = 0,
    var label: String = "Reminder",
    var isEnabled: Boolean = true,
    var isRepeating: Boolean = false,
    var repeatDays: Set<Int> = emptySet(),
    var durationSeconds: Int = 30,
    var soundType: String = SoundType.TWEET,
    var nextAlarmTime: Long = 0
) {
    val timeString: String
        get() = String.format("%02d:%02d", hour, minute)
    
    val durationString: String
        get() = when {
            durationSeconds < 60 -> "${durationSeconds}s"
            durationSeconds % 60 == 0 -> "${durationSeconds / 60}m"
            else -> "${durationSeconds / 60}m ${durationSeconds % 60}s"
        }
    
    val soundDisplayName: String
        get() = SoundType.getDisplayName(soundType)
    
    val repeatDaysString: String
        get() {
            if (!isRepeating || repeatDays.isEmpty()) {
                return "Once"
            }
            
            if (repeatDays.size == 7) {
                return "Daily"
            }
            
            if (repeatDays.size == 5 && !repeatDays.contains(1) && !repeatDays.contains(7)) {
                return "Weekdays"
            }
            
            if (repeatDays.size == 2 && repeatDays.contains(1) && repeatDays.contains(7)) {
                return "Weekends"
            }
            
            val dayNames = arrayOf("", "Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
            return (1..7)
                .filter { repeatDays.contains(it) }
                .joinToString(", ") { dayNames[it] }
        }
}