package com.example.cocktailoverview.data.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.cocktailoverview.data.Cocktail

@Entity
data class DatabaseItem(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "_id")
    val id: Int,
    val name: String,
    @ColumnInfo(name = "url")
    val thumbnailUrl: String,
    @ColumnInfo(name = "created_at") var createdAt: Long?,
)

fun DatabaseItem.toCocktail(): Cocktail {
    return Cocktail(
        id.toString(),
        name,
        thumbnailUrl
    )
}