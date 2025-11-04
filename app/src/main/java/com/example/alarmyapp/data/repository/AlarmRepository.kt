package com.example.alarmyapp.data.repository

import androidx.lifecycle.LiveData
import com.example.alarmyapp.data.dao.AlarmDao
import com.example.alarmyapp.data.model.Alarm
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AlarmRepository @Inject constructor(
    private val alarmDao: AlarmDao
) {
    val allAlarms: LiveData<List<Alarm>> = alarmDao.getAllAlarms()

    fun getAlarmById(id: Int): LiveData<Alarm?> = alarmDao.getAlarmById(id)

    suspend fun getAlarmEntity(id: Int): Alarm? = withContext(Dispatchers.IO) {
        alarmDao.getAlarmEntity(id)
    }

    suspend fun insertAlarm(alarm: Alarm): Long = withContext(Dispatchers.IO) {
        alarmDao.insertAlarm(alarm)
    }

    suspend fun updateAlarm(alarm: Alarm) = withContext(Dispatchers.IO) {
        alarmDao.updateAlarm(alarm)
    }

    suspend fun deleteAlarm(alarm: Alarm) = withContext(Dispatchers.IO) {
        alarmDao.deleteAlarm(alarm)
    }

    suspend fun deleteAlarmById(id: Int) = withContext(Dispatchers.IO) {
        alarmDao.deleteAlarmById(id)
    }

    suspend fun setAlarmEnabled(id: Int, enabled: Boolean) = withContext(Dispatchers.IO) {
        alarmDao.setAlarmEnabled(id, enabled)
    }

    suspend fun getEnabledAlarms(): List<Alarm> = withContext(Dispatchers.IO) {
        alarmDao.getEnabledAlarms()
    }

    suspend fun getAlarmCount(): Int = withContext(Dispatchers.IO) {
        alarmDao.getAlarmCount()
    }
}