package com.example.alarmyapp.di

import android.content.Context
import androidx.room.Room
import com.example.alarmyapp.alarm.AlarmScheduler
import com.example.alarmyapp.data.dao.AlarmDao
import com.example.alarmyapp.data.database.AlarmDatabase
import com.example.alarmyapp.data.repository.AlarmRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AlarmDatabase =
        Room.databaseBuilder(
            context,
            AlarmDatabase::class.java,
            "alarm_database"
        ).build()

    @Provides
    fun provideAlarmDao(db: AlarmDatabase): AlarmDao = db.alarmDao()

    @Provides
    @Singleton
    fun provideAlarmScheduler(@ApplicationContext context: Context): AlarmScheduler =
        AlarmScheduler(context)

    @Provides
    @Singleton
    fun provideAlarmRepository(alarmDao: AlarmDao): AlarmRepository =
        AlarmRepository(alarmDao)
}
