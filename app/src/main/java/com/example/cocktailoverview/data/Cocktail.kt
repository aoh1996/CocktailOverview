package com.example.cocktailoverview.data

import com.squareup.moshi.Json

data class ResponseData(
    @Json(name = "drinks") val responseData: List<Cocktail>
)

data class Cocktail(
    @Json(name = "idDrink") val id: String?,
    @Json(name = "strDrink") val name: String?,
    @Json(name = "strDrinkThumb") val thumbnailUrl: String? = null,
    @Json(name = "strCategory") val category: String? = null,
    @Json(name = "strAlcoholic") val alcoholic: String? = null,
    @Json(name = "strGlass") val glass: String? = null,
    @Json(name = "strIngredient1") val ingredient1: String? = null,
    @Json(name = "strIngredient2") val ingredient2: String? = null,
    @Json(name = "strIngredient3") val ingredient3: String? = null,
    @Json(name = "strIngredient4") val ingredient4: String? = null,
    @Json(name = "strIngredient5") val ingredient5: String? = null,
    @Json(name = "strIngredient6") val ingredient6: String? = null,
    @Json(name = "strIngredient7") val ingredient7: String? = null,
    @Json(name = "strIngredient8") val ingredient8: String? = null,
    @Json(name = "strIngredient9") val ingredient9: String? = null,
    @Json(name = "strIngredient10") val ingredient10: String? = null,
    @Json(name = "strIngredient11") val ingredient11: String? = null,
    @Json(name = "strIngredient12") val ingredient12: String? = null,
    @Json(name = "strIngredient13") val ingredient13: String? = null,
    @Json(name = "strIngredient14") val ingredient14: String? = null,
    @Json(name = "strIngredient15") val ingredient15: String? = null,

) {
    val ingredientList = mutableListOf(ingredient1, ingredient2, ingredient3, ingredient4, ingredient5, ingredient6,
        ingredient7, ingredient8, ingredient9, ingredient10, ingredient11, ingredient12, ingredient13, ingredient14, ingredient15)
    var isFavorite: Boolean = false
}
