package com.example.cocktailoverview

import android.app.Application
import com.example.cocktailoverview.data.db.FavoritesDatabase
import com.example.cocktailoverview.data.db.HistoryDatabase

class CocktailOverviewApplication : Application() {
    val favoritesDatabase: FavoritesDatabase by lazy { FavoritesDatabase.getDatabase(this) }
    val historyDatabase: HistoryDatabase by lazy { HistoryDatabase.getDatabase(this) }
}