package com.example.alarmyapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.alarmyapp.alarm.AlarmScheduler
import com.example.alarmyapp.data.model.Alarm
import com.example.alarmyapp.data.repository.AlarmRepository
import kotlinx.coroutines.launch

class AlarmViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = AlarmRepository(application)
    private val scheduler = AlarmScheduler(application)
    
    val allAlarms: LiveData<List<Alarm>> = repository.allAlarms

    fun getAlarmById(id: Int): LiveData<Alarm?> = repository.getAlarmById(id)

    fun insertAlarm(alarm: Alarm) {
        viewModelScope.launch {
            val id = repository.insertAlarm(alarm)
            alarm.id = id.toInt()
            if (alarm.isEnabled) {
                scheduler.scheduleAlarm(alarm)
            }
        }
    }

    fun updateAlarm(alarm: Alarm) {
        viewModelScope.launch {
            repository.updateAlarm(alarm)
            if (alarm.isEnabled) {
                scheduler.scheduleAlarm(alarm)
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
                getAlarmById(alarmId).observeForever { alarm ->
                    alarm?.let { scheduler.scheduleAlarm(it) }
                }
            } else {
                scheduler.cancelAlarm(alarmId)
            }
        }
    }
}