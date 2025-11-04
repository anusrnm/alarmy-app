package com.example.alarmyapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "alarms")
data class Alarm(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var hour: Int = 7,
    var minute: Int = 0,
    var label: String = "Alarm",
    var isEnabled: Boolean = true,
    var isRepeating: Boolean = false,
    var repeatDays: Set<Int> = emptySet(),
    var soundUri: String = "default",
    var volume: Int = 80,
    var vibrationPattern: Int = 2,
    var durationMinutes: Int = 5,
    var snoozeEnabled: Boolean = true,
    var snoozeIntervalMinutes: Int = 5,
    var nextAlarmTime: Long = 0
) {
    val timeString: String
        get() = String.format("%02d:%02d", hour, minute)
    
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