package com.example.cocktailoverview.data

import com.squareup.moshi.Json

data class ResponseData(
    @Json(name = "drinks") val responseData: List<Cocktail>
)

data class Cocktail(
    @Json(name = "idDrink") val id: String?,
    @Json(name = "strDrink") val name: String?,
    @Json(name = "strDrinkThumb") val thumbnailUrl: String?,
    @Json(name = "strCategory") val category: String?,
    @Json(name = "strAlcoholic") val alcoholic: String?,
    @Json(name = "strGlass") val glass: String?,
    @Json(name = "strIngredient1") val ingredient1: String?,
    @Json(name = "strIngredient2") val ingredient2: String?,
    @Json(name = "strIngredient3") val ingredient3: String?,
    @Json(name = "strIngredient4") val ingredient4: String?,
    @Json(name = "strIngredient5") val ingredient5: String?,
    @Json(name = "strIngredient6") val ingredient6: String?,
    @Json(name = "strIngredient7") val ingredient7: String?,
    @Json(name = "strIngredient8") val ingredient8: String?,
    @Json(name = "strIngredient9") val ingredient9: String?,
    @Json(name = "strIngredient10") val ingredient10: String?,
    @Json(name = "strIngredient11") val ingredient11: String?,
    @Json(name = "strIngredient12") val ingredient12: String?,
    @Json(name = "strIngredient13") val ingredient13: String?,
    @Json(name = "strIngredient14") val ingredient14: String?,
    @Json(name = "strIngredient15") val ingredient15: String?,
    var isFavorite: Boolean = false
) {
    val ingredientList = mutableListOf(ingredient1, ingredient2, ingredient3, ingredient4, ingredient5, ingredient6,
        ingredient7, ingredient8, ingredient9, ingredient10, ingredient11, ingredient12, ingredient13, ingredient14, ingredient15)
}
