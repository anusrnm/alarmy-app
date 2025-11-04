package com.example.alarmyapp.data.converter

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    @TypeConverter
    fun fromIntegerSet(value: Set<Int>?): String {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun toIntegerSet(value: String?): Set<Int>? {
        if (value == null) return null
        val setType = object : TypeToken<Set<Int>>() {}.type
        return Gson().fromJson(value, setType)
    }
}