package com.example.cocktailoverview.data.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class DatabaseItem(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "_id")
    val id: Int,
    val name: String,
    @ColumnInfo(name = "url")
    val thumbnailUrl: String,
    val category: String,
    val alcoholic: String,
    val glass: String,
    val ingredients: List<String>
)