package com.example.cocktailoverview.data.network

import com.example.cocktailoverview.data.Cocktail
import com.example.cocktailoverview.data.ResponseData
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

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

    @GET("search.php")
    suspend fun searchByName(@Query("s") s: String): ResponseData

    @GET("list.php?c=list")
    suspend fun getCategories(): ResponseData

    @GET("filter.php")
    suspend fun getCategoryItems(@Query("c") c: String): ResponseData

    @GET("lookup.php")
    suspend fun getCocktailById(@Query("i") i: String): ResponseData
}

object CocktailDbApi{
    val retrofitService: CocktailDbApiService by lazy { retrofit.create(CocktailDbApiService::class.java) }
}