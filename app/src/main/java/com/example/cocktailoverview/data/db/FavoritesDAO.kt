package com.example.cocktailoverview.data.db

import androidx.room.*

@Dao
interface FavoritesDAO {

    @Query("SELECT * from databaseitem ORDER BY name ASC")
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

    @Update
    suspend fun update(cocktail: DatabaseItem)

    @Delete
    suspend fun delete(cocktail: DatabaseItem)

    @Query("SELECT EXISTS(SELECT * FROM databaseitem WHERE _id = :id)")
    fun isRowExist(id : Int) : Boolean
}