package com.example.cocktailoverview.data.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoritesDAO {

    @Query("SELECT * from databaseitem ORDER BY name ASC")
    suspend fun getCocktails(): List<DatabaseItem>

    @Query("SELECT * from databaseitem WHERE _id = :id")
    fun getCocktail(id: Int): Flow<DatabaseItem>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(cocktail: DatabaseItem)

    @Update
    suspend fun update(cocktail: DatabaseItem)

    @Delete
    suspend fun delete(cocktail: DatabaseItem)

    @Query("SELECT EXISTS(SELECT * FROM databaseitem WHERE _id = :id)")
    fun isRowExist(id : Int) : Boolean
}