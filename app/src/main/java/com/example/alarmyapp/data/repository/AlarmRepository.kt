package com.example.alarmyapp.data.repository

import android.content.Context
import androidx.lifecycle.LiveData
import com.example.alarmyapp.data.dao.AlarmDao
import com.example.alarmyapp.data.database.AlarmDatabase
import com.example.alarmyapp.data.model.Alarm
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AlarmRepository(context: Context) {
    private val alarmDao: AlarmDao = AlarmDatabase.getDatabase(context).alarmDao()
    
    val allAlarms: LiveData<List<Alarm>> = alarmDao.getAllAlarms()

    fun getAlarmById(id: Int): LiveData<Alarm?> = alarmDao.getAlarmById(id)

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