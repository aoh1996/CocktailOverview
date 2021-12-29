package com.example.cocktailoverview.network

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

private const val BASE_URL = "https://www.thecocktaildb.com/api/json/v1/1/"

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val interceptor = HttpLoggingInterceptor()
    .setLevel(HttpLoggingInterceptor.Level.BODY)

private val okHttpClient = OkHttpClient.Builder()
    .addInterceptor(interceptor)

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .client(okHttpClient.build())
    .build()

interface CocktailDbApiService {

    @GET("random.php")
    suspend fun getRandom(): ResponseData

    @GET("search.php?s={param}")
    suspend fun searchByName(@Path("param") param: String): List<Cocktail>

    @GET("list.php?c=list")
    suspend fun getCategories(): ResponseData
}

object CocktailDbApi{
    val retrofitService: CocktailDbApiService by lazy { retrofit.create(CocktailDbApiService::class.java) }
}