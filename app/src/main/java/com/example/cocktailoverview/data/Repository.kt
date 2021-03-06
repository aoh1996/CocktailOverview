package com.example.cocktailoverview.data

import com.example.cocktailoverview.CocktailOverviewApplication
import com.example.cocktailoverview.data.db.DatabaseItem

class Repository private constructor(application: CocktailOverviewApplication) {

    private val remoteSource = application.retrofit
    private val historySource = application.historyDatabase.historyDao()
    private val favoriteSource = application.favoritesDatabase.favoritesDao()

    inner class HistoryRepo {
        suspend fun getHistory(): List<DatabaseItem> {
            return historySource.getCocktails()
        }

        suspend fun getById(id: Int): DatabaseItem {
            return historySource.getCocktail(id)
        }

        suspend fun addToHistory(cocktail: DatabaseItem) {
            historySource.insertWithTimestamp(cocktail)
        }

        suspend fun deleteOld() {
            historySource.removeOldData()
        }

        suspend fun deleteItem(cocktail: DatabaseItem) {
            historySource.delete(cocktail)
        }
    }

    inner class FavoritesRepo {

        suspend fun getAll(): List<DatabaseItem> {
           return favoriteSource.getCocktails()
        }

        suspend fun getById(id: Int): DatabaseItem {
            return favoriteSource.getCocktail(id)
        }

        suspend fun add(cocktail: DatabaseItem) {
            favoriteSource.insertWithTimestamp(cocktail)
        }

        suspend fun remove(cocktail: DatabaseItem) {
            favoriteSource.delete(cocktail)
        }

        fun isFavorite(id: Int): Boolean {
            return favoriteSource.isRowExist(id)
        }
    }

    inner class RemoteRepo {

        suspend fun getByName(name: String): List<Cocktail>? {
            return remoteSource.searchByName(name).responseData
        }

        suspend fun getById(id: String): Cocktail? {
            return remoteSource.getCocktailById(id).responseData?.get(0)
        }

        suspend fun getCategories(): List<Cocktail>? {
            return remoteSource.getCategories().responseData
        }

        suspend fun getCategoryItems(category: String): List<Cocktail>? {
            return remoteSource.getCategoryItems(category).responseData
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: Repository? = null

        fun getRepository(application: CocktailOverviewApplication): Repository {
            // if the INSTANCE is not null, then return it,
            // if it is, then create repo
            return INSTANCE ?: synchronized(this) {
                val instance = Repository(application)
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }

}