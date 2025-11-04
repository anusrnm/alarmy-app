package com.example.alarmyapp.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.alarmyapp.data.model.Alarm
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlarmEditDialog(
    alarm: Alarm?,
    onSave: (Alarm) -> Unit,
    onDismiss: () -> Unit
) {
    var hour by remember { mutableStateOf(alarm?.hour ?: 7) }
    var minute by remember { mutableStateOf(alarm?.minute ?: 0) }
    var label by remember { mutableStateOf(alarm?.label ?: "Alarm") }
    var isRepeating by remember { mutableStateOf(alarm?.isRepeating ?: false) }
    var repeatDays by remember { mutableStateOf(alarm?.repeatDays ?: setOf<Int>()) }
    var volume by remember { mutableStateOf(alarm?.volume ?: 80) }
    var vibrationPattern by remember { mutableStateOf(alarm?.vibrationPattern ?: 2) }
    var durationMinutes by remember { mutableStateOf(alarm?.durationMinutes ?: 5) }
    var snoozeEnabled by remember { mutableStateOf(alarm?.isSnoozeEnabled ?: true) }
    var snoozeInterval by remember { mutableStateOf(alarm?.snoozeIntervalMinutes ?: 5) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = if (alarm == null) "Add Alarm" else "Edit Alarm",
                    style = MaterialTheme.typography.headlineSmall
                )

                // Time Picker
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Time:", style = MaterialTheme.typography.bodyLarge)
                    
                    OutlinedTextField(
                        value = String.format("%02d", hour),
                        onValueChange = { value ->
                            value.toIntOrNull()?.let { h ->
                                if (h in 0..23) hour = h
                            }
                        },
                        label = { Text("Hour") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.width(80.dp)
                    )
                    
                    Text(":", style = MaterialTheme.typography.headlineMedium)
                    
                    OutlinedTextField(
                        value = String.format("%02d", minute),
                        onValueChange = { value ->
                            value.toIntOrNull()?.let { m ->
                                if (m in 0..59) minute = m
                            }
                        },
                        label = { Text("Min") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.width(80.dp)
                    )
                }

                // Label
                OutlinedTextField(
                    value = label,
                    onValueChange = { label = it },
                    label = { Text("Label") },
                    modifier = Modifier.fillMaxWidth()
                )

                // Repeat Settings
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = isRepeating,
                        onCheckedChange = { isRepeating = it }
                    )
                    Text("Repeat", modifier = Modifier.padding(start = 8.dp))
                }

                if (isRepeating) {
                    Column {
                        Text("Repeat Days:", style = MaterialTheme.typography.bodyMedium)
                        val dayNames = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            dayNames.forEachIndexed { index, dayName ->
                                val dayValue = index + 1
                                FilterChip(
                                    onClick = {
                                        repeatDays = if (repeatDays.contains(dayValue)) {
                                            repeatDays - dayValue
                                        } else {
                                            repeatDays + dayValue
                                        }
                                    },
                                    label = { Text(dayName, fontSize = 12.sp) },
                                    selected = repeatDays.contains(dayValue),
                                    modifier = Modifier.width(45.dp)
                                )
                            }
                        }
                    }
                }

                // Volume
                Column {
                    Text("Volume: $volume%", style = MaterialTheme.typography.bodyMedium)
                    Slider(
                        value = volume.toFloat(),
                        onValueChange = { volume = it.toInt() },
                        valueRange = 0f..100f,
                        steps = 19
                    )
                }

                // Vibration Pattern
                Column {
                    Text("Vibration:", style = MaterialTheme.typography.bodyMedium)
                    val vibrationOptions = listOf(
                        0 to "None",
                        1 to "Light",
                        2 to "Medium", 
                        3 to "Strong"
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        vibrationOptions.forEach { (value, name) ->
                            FilterChip(
                                onClick = { vibrationPattern = value },
                                label = { Text(name, fontSize = 12.sp) },
                                selected = vibrationPattern == value
                            )
                        }
                    }
                }

                // Duration
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Duration:", style = MaterialTheme.typography.bodyMedium)
                    OutlinedTextField(
                        value = durationMinutes.toString(),
                        onValueChange = { value ->
                            value.toIntOrNull()?.let { d ->
                                if (d > 0) durationMinutes = d
                            }
                        },
                        label = { Text("Minutes") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.width(100.dp)
                    )
                }

                // Snooze Settings
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = snoozeEnabled,
                        onCheckedChange = { snoozeEnabled = it }
                    )
                    Text("Enable Snooze", modifier = Modifier.padding(start = 8.dp))
                }

                if (snoozeEnabled) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("Snooze Interval:", style = MaterialTheme.typography.bodyMedium)
                        OutlinedTextField(
                            value = snoozeInterval.toString(),
                            onValueChange = { value ->
                                value.toIntOrNull()?.let { s ->
                                    if (s > 0) snoozeInterval = s
                                }
                            },
                            label = { Text("Minutes") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.width(100.dp)
                        )
                    }
                }

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Button(
                        onClick = {
                            val newAlarm = alarm?.copy() ?: Alarm()
                            newAlarm.hour = hour
                            newAlarm.minute = minute
                            newAlarm.label = label
                            newAlarm.isRepeating = isRepeating
                            newAlarm.repeatDays = repeatDays
                            newAlarm.volume = volume
                            newAlarm.vibrationPattern = vibrationPattern
                            newAlarm.durationMinutes = durationMinutes
                            newAlarm.isSnoozeEnabled = snoozeEnabled
                            newAlarm.snoozeIntervalMinutes = snoozeInterval
                            newAlarm.isEnabled = true
                            
                            onSave(newAlarm)
                        }
                    ) {
                        Text("Save")
                    }
                }
            }
        }
    }
}