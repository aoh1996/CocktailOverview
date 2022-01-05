package com.example.cocktailoverview.data.db

import androidx.room.TypeConverter


class MyTypeConverters {

    @TypeConverter
    fun stringToList(s: String): List<String> {
        return s.split(",").map { it.trim() }
    }

    @TypeConverter
    fun listToString(l: List<String>): String {
        return l.joinToString(",")
    }
}