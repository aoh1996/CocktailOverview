package com.example.cocktailoverview

import android.app.Application
import com.example.cocktailoverview.data.db.FavoritesDatabase
import com.example.cocktailoverview.data.db.HistoryDatabase
import com.example.cocktailoverview.data.network.CocktailDbApi
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create

class CocktailOverviewApplication : Application() {
    val favoritesDatabase: FavoritesDatabase by lazy { FavoritesDatabase.getDatabase(this) }
    val historyDatabase: HistoryDatabase by lazy { HistoryDatabase.getDatabase(this) }

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    val retrofit: CocktailDbApi by lazy {
        Retrofit.Builder()
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .baseUrl(CocktailDbApi.BASE_URL)
            .build()
            .create()
    }
}