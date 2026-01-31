package com.example.alarmyapp.ui.screen

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.media.MediaPlayer
import android.media.RingtoneManager
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.alarmyapp.data.model.Alarm
import com.example.alarmyapp.data.model.SoundType
import kotlin.math.sin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlarmEditDialog(
    alarm: Alarm?,
    onSave: (Alarm) -> Unit,
    onDismiss: () -> Unit
) {
    var hour by remember { mutableStateOf(alarm?.hour ?: 7) }
    var minute by remember { mutableStateOf(alarm?.minute ?: 0) }
    var label by remember { mutableStateOf(alarm?.label ?: "Reminder") }
    var isRepeating by remember { mutableStateOf(alarm?.isRepeating ?: false) }
    var repeatDays by remember { mutableStateOf(alarm?.repeatDays ?: setOf<Int>()) }
    var durationSeconds by remember { mutableStateOf(alarm?.durationSeconds ?: 30) }
    var soundType by remember { mutableStateOf(alarm?.soundType ?: SoundType.TWEET) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.9f),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                // Header with Time Picker
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .padding(24.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = if (alarm == null) "New Reminder" else "Edit Reminder",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        
                        Spacer(modifier = Modifier.height(20.dp))
                        
                        EditableTimePicker(
                            hour = hour,
                            minute = minute,
                            onHourChange = { hour = it },
                            onMinuteChange = { minute = it }
                        )
                    }
                }

                // Content
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // Label
                    SettingSection(
                        icon = Icons.Outlined.Label,
                        title = "Label"
                    ) {
                        OutlinedTextField(
                            value = label,
                            onValueChange = { label = it },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                            ),
                            singleLine = true,
                            placeholder = { Text("Enter reminder label") }
                        )
                    }

                    // Duration
                    SettingSection(
                        icon = Icons.Outlined.Timer,
                        title = "Sound Duration"
                    ) {
                        DurationSelector(
                            seconds = durationSeconds,
                            onSelect = { durationSeconds = it }
                        )
                    }

                    // Sound Type
                    SettingSection(
                        icon = Icons.Outlined.MusicNote,
                        title = "Sound"
                    ) {
                        SoundSelector(
                            selectedSound = soundType,
                            onSelect = { soundType = it }
                        )
                    }

                    // Repeat
                    SettingSection(
                        icon = Icons.Outlined.Repeat,
                        title = "Repeat"
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (isRepeating) "Weekly" else "Once",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Switch(
                                    checked = isRepeating,
                                    onCheckedChange = { isRepeating = it },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = MaterialTheme.colorScheme.primary,
                                        checkedTrackColor = MaterialTheme.colorScheme.primaryContainer
                                    )
                                )
                            }
                            
                            if (isRepeating) {
                                Spacer(modifier = Modifier.height(16.dp))
                                DaySelector(
                                    selectedDays = repeatDays,
                                    onDayToggle = { day ->
                                        repeatDays = if (repeatDays.contains(day)) {
                                            repeatDays - day
                                        } else {
                                            repeatDays + day
                                        }
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .weight(1f)
                                .height(52.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.onSurface
                            )
                        ) {
                            Text("Cancel", fontWeight = FontWeight.Medium)
                        }
                        
                        Button(
                            onClick = {
                                val newAlarm = alarm?.copy() ?: Alarm()
                                newAlarm.hour = hour
                                newAlarm.minute = minute
                                newAlarm.label = label
                                newAlarm.isRepeating = isRepeating
                                newAlarm.repeatDays = repeatDays
                                newAlarm.durationSeconds = durationSeconds
                                newAlarm.soundType = soundType
                                newAlarm.isEnabled = true
                                
                                onSave(newAlarm)
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(52.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Icon(
                                Icons.Default.Check,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Save", fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EditableTimePicker(
    hour: Int,
    minute: Int,
    onHourChange: (Int) -> Unit,
    onMinuteChange: (Int) -> Unit
) {
    var hourText by remember(hour) { mutableStateOf(String.format("%02d", hour)) }
    var minuteText by remember(minute) { mutableStateOf(String.format("%02d", minute)) }
    
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        // Hour input with arrows
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            IconButton(
                onClick = { 
                    val newHour = (hour + 1) % 24
                    onHourChange(newHour)
                    hourText = String.format("%02d", newHour)
                },
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    Icons.Default.KeyboardArrowUp,
                    contentDescription = "Increase hour",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                )
            }
            
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.3f),
                modifier = Modifier.size(70.dp, 56.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    BasicTextField(
                        value = hourText,
                        onValueChange = { newValue ->
                            if (newValue.length <= 2 && newValue.all { it.isDigit() }) {
                                hourText = newValue
                                newValue.toIntOrNull()?.let { h ->
                                    if (h in 0..23) onHourChange(h)
                                }
                            }
                        },
                        textStyle = TextStyle(
                            fontSize = 36.sp,
                            fontWeight = FontWeight.Light,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            textAlign = TextAlign.Center
                        ),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                        modifier = Modifier
                            .fillMaxWidth()
                            .onFocusChanged { focusState ->
                                if (!focusState.isFocused) {
                                    // Validate and format on blur
                                    val h = hourText.toIntOrNull()
                                    if (h != null && h in 0..23) {
                                        hourText = String.format("%02d", h)
                                        onHourChange(h)
                                    } else {
                                        hourText = String.format("%02d", hour)
                                    }
                                }
                            }
                    )
                }
            }
            
            IconButton(
                onClick = { 
                    val newHour = if (hour == 0) 23 else hour - 1
                    onHourChange(newHour)
                    hourText = String.format("%02d", newHour)
                },
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    Icons.Default.KeyboardArrowDown,
                    contentDescription = "Decrease hour",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                )
            }
        }
        
        Text(
            text = ":",
            style = TextStyle(
                fontSize = 36.sp,
                fontWeight = FontWeight.Light,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            ),
            modifier = Modifier.padding(horizontal = 4.dp)
        )
        
        // Minute input with arrows
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            IconButton(
                onClick = { 
                    val newMinute = (minute + 1) % 60
                    onMinuteChange(newMinute)
                    minuteText = String.format("%02d", newMinute)
                },
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    Icons.Default.KeyboardArrowUp,
                    contentDescription = "Increase minute",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                )
            }
            
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.3f),
                modifier = Modifier.size(70.dp, 56.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    BasicTextField(
                        value = minuteText,
                        onValueChange = { newValue ->
                            if (newValue.length <= 2 && newValue.all { it.isDigit() }) {
                                minuteText = newValue
                                newValue.toIntOrNull()?.let { m ->
                                    if (m in 0..59) onMinuteChange(m)
                                }
                            }
                        },
                        textStyle = TextStyle(
                            fontSize = 36.sp,
                            fontWeight = FontWeight.Light,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            textAlign = TextAlign.Center
                        ),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                        modifier = Modifier
                            .fillMaxWidth()
                            .onFocusChanged { focusState ->
                                if (!focusState.isFocused) {
                                    // Validate and format on blur
                                    val m = minuteText.toIntOrNull()
                                    if (m != null && m in 0..59) {
                                        minuteText = String.format("%02d", m)
                                        onMinuteChange(m)
                                    } else {
                                        minuteText = String.format("%02d", minute)
                                    }
                                }
                            }
                    )
                }
            }
            
            IconButton(
                onClick = { 
                    val newMinute = if (minute == 0) 59 else minute - 1
                    onMinuteChange(newMinute)
                    minuteText = String.format("%02d", newMinute)
                },
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    Icons.Default.KeyboardArrowDown,
                    contentDescription = "Decrease minute",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
fun SettingSection(
    icon: ImageVector,
    title: String,
    content: @Composable () -> Unit
) {
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 12.dp)
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        content()
    }
}

@Composable
fun DurationSelector(
    seconds: Int,
    onSelect: (Int) -> Unit
) {
    // Options: 5s, 10s, 30s, 1m, 2m, 5m
    val options = listOf(
        5 to "5s",
        10 to "10s",
        30 to "30s",
        60 to "1m",
        120 to "2m",
        300 to "5m"
    )
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        options.forEach { (value, label) ->
            val isSelected = seconds == value
            
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .clickable { onSelect(value) },
                shape = RoundedCornerShape(10.dp),
                color = if (isSelected) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.surfaceVariant
            ) {
                Text(
                    text = label,
                    modifier = Modifier.padding(vertical = 10.dp),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    color = if (isSelected) MaterialTheme.colorScheme.onPrimary
                    else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun DaySelector(
    selectedDays: Set<Int>,
    onDayToggle: (Int) -> Unit
) {
    val days = listOf(
        1 to "Sun",
        2 to "Mon",
        3 to "Tue",
        4 to "Wed",
        5 to "Thu",
        6 to "Fri",
        7 to "Sat"
    )
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        days.forEach { (value, name) ->
            val isSelected = selectedDays.contains(value)
            
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(
                        if (isSelected) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.surfaceVariant
                    )
                    .clickable { onDayToggle(value) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = name.first().toString(),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    color = if (isSelected) MaterialTheme.colorScheme.onPrimary
                    else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun SoundSelector(
    selectedSound: String,
    onSelect: (String) -> Unit
) {
    val context = LocalContext.current
    var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }
    var audioTrack by remember { mutableStateOf<AudioTrack?>(null) }
    
    // Cleanup when leaving
    DisposableEffect(Unit) {
        onDispose {
            mediaPlayer?.release()
            audioTrack?.release()
        }
    }
    
    fun previewSound(soundType: String) {
        // Stop any current preview
        mediaPlayer?.release()
        mediaPlayer = null
        audioTrack?.release()
        audioTrack = null
        
        when (soundType) {
            SoundType.SILENT -> { /* No preview */ }
            SoundType.TWEET -> {
                // Play tweet beep preview
                Thread {
                    try {
                        val sampleRate = 44100
                        val frequency = 2500.0
                        val durationMs = 150
                        val numSamples = (sampleRate * durationMs / 1000.0).toInt()
                        val samples = ShortArray(numSamples)
                        
                        for (i in 0 until numSamples) {
                            val t = i.toDouble() / sampleRate
                            var amplitude = sin(2.0 * Math.PI * frequency * t)
                            val fadeIn = i.toDouble() / (numSamples * 0.1)
                            if (fadeIn < 1.0) amplitude *= fadeIn
                            val fadeOut = (numSamples - i).toDouble() / (numSamples * 0.1)
                            if (fadeOut < 1.0) amplitude *= fadeOut
                            samples[i] = (amplitude * Short.MAX_VALUE * 0.7).toInt().toShort()
                        }
                        
                        val bufferSize = AudioTrack.getMinBufferSize(
                            sampleRate,
                            AudioFormat.CHANNEL_OUT_MONO,
                            AudioFormat.ENCODING_PCM_16BIT
                        )
                        
                        audioTrack = AudioTrack.Builder()
                            .setAudioAttributes(
                                AudioAttributes.Builder()
                                    .setUsage(AudioAttributes.USAGE_ALARM)
                                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                                    .build()
                            )
                            .setAudioFormat(
                                AudioFormat.Builder()
                                    .setSampleRate(sampleRate)
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
                        // Ignore preview errors
                    }
                }.start()
            }
            else -> {
                try {
                    val soundUri = when (soundType) {
                        SoundType.NOTIFICATION -> RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                        SoundType.ALARM -> RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
                        SoundType.CHIME -> RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                        else -> RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                    }
                    
                    mediaPlayer = MediaPlayer().apply {
                        setDataSource(context, soundUri)
                        setAudioAttributes(
                            AudioAttributes.Builder()
                                .setUsage(AudioAttributes.USAGE_ALARM)
                                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                                .build()
                        )
                        prepare()
                        start()
                    }
                } catch (e: Exception) {
                    // Ignore preview errors
                }
            }
        }
    }
    
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        SoundType.getAllTypes().forEach { type ->
            val isSelected = selectedSound == type
            val displayName = SoundType.getDisplayName(type)
            val icon = when (type) {
                SoundType.TWEET -> Icons.Outlined.GraphicEq
                SoundType.NOTIFICATION -> Icons.Outlined.Notifications
                SoundType.ALARM -> Icons.Outlined.Alarm
                SoundType.CHIME -> Icons.Outlined.NotificationsActive
                SoundType.SILENT -> Icons.Outlined.VolumeOff
                else -> Icons.Outlined.MusicNote
            }
            
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { 
                        onSelect(type)
                        previewSound(type)
                    },
                shape = RoundedCornerShape(12.dp),
                color = if (isSelected) MaterialTheme.colorScheme.primaryContainer
                else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                border = if (isSelected) null else null
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            icon,
                            contentDescription = null,
                            modifier = Modifier.size(22.dp),
                            tint = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer
                            else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = displayName,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                            color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer
                            else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    
                    if (isSelected) {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = "Selected",
                            modifier = Modifier.size(20.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
        
        Text(
            text = "Tap to preview sound",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}