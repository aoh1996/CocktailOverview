package com.example.cocktailoverview

import android.app.Application
import com.example.cocktailoverview.data.db.FavoritesDatabase

class CocktailOverviewApplication : Application() {
    val favoritesDatabase: FavoritesDatabase by lazy { FavoritesDatabase.getDatabase(this) }
}