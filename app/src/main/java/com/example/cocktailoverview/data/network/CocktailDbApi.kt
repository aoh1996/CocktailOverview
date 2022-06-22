package com.example.cocktailoverview.data.network

import com.example.cocktailoverview.data.ResponseData
import retrofit2.http.GET
import retrofit2.http.Query


interface CocktailDbApi {

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

    companion object {
        val BASE_URL = "https://www.thecocktaildb.com/api/json/v1/1/"
    }
}
