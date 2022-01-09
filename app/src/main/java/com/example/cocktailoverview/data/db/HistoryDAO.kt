package com.example.cocktailoverview.data.db

import androidx.room.*

@Dao
interface HistoryDAO {
    @Query("SELECT * from databaseitem ORDER BY created_at DESC")
    suspend fun getCocktails(): List<DatabaseItem>

    @Query("SELECT * from databaseitem WHERE _id = :id")
    suspend fun getCocktail(id: Int): DatabaseItem

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(cocktail: DatabaseItem)

    suspend fun insertWithTimestamp(cocktail: DatabaseItem) {
        insert(cocktail.apply{
            createdAt = System.currentTimeMillis()
        })
    }

    @Query("DELETE FROM databaseitem WHERE _id IN (SELECT _id FROM databaseitem ORDER BY created_at DESC LIMIT 1 OFFSET 10)")
    suspend fun removeOldData()

    @Update
    suspend fun update(cocktail: DatabaseItem)

    @Delete
    suspend fun delete(cocktail: DatabaseItem)

}