package com.example.alarmyapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.alarmyapp.alarm.AlarmScheduler
import com.example.alarmyapp.data.model.Alarm
import com.example.alarmyapp.data.repository.AlarmRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlarmViewModel @Inject constructor(
    private val repository: AlarmRepository,
    private val scheduler: AlarmScheduler
) : ViewModel() {

    val allAlarms: LiveData<List<Alarm>> = repository.allAlarms

    fun getAlarmById(id: Int): LiveData<Alarm?> = repository.getAlarmById(id)

    fun insertAlarm(alarm: Alarm) {
        viewModelScope.launch {
            val id = repository.insertAlarm(alarm)
            alarm.id = id.toInt()
            if (alarm.isEnabled) {
                val next = scheduler.scheduleAlarm(alarm)
                if (next > 0) {
                    alarm.nextAlarmTime = next
                    repository.updateAlarm(alarm)
                }
            }
        }
    }

    fun updateAlarm(alarm: Alarm) {
        viewModelScope.launch {
            repository.updateAlarm(alarm)
            if (alarm.isEnabled) {
                val next = scheduler.scheduleAlarm(alarm)
                if (next > 0) {
                    alarm.nextAlarmTime = next
                    repository.updateAlarm(alarm)
                }
            } else {
                scheduler.cancelAlarm(alarm.id)
            }
        }
    }

    fun deleteAlarm(alarm: Alarm) {
        viewModelScope.launch {
            repository.deleteAlarm(alarm)
            scheduler.cancelAlarm(alarm.id)
        }
    }

    fun toggleAlarmEnabled(alarmId: Int, enabled: Boolean) {
        viewModelScope.launch {
            repository.setAlarmEnabled(alarmId, enabled)
            if (enabled) {
                val alarm = repository.getAlarmEntity(alarmId)
                if (alarm != null && alarm.isEnabled) {
                    val next = scheduler.scheduleAlarm(alarm)
                    if (next > 0) {
                        alarm.nextAlarmTime = next
                        repository.updateAlarm(alarm)
                    }
                }
            } else {
                scheduler.cancelAlarm(alarmId)
            }
        }
    }
}