package com.example.cocktailoverview.ui.categories

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cocktailoverview.data.Cocktail
import com.example.cocktailoverview.data.network.CocktailDbApi
import com.example.cocktailoverview.data.Status
import kotlinx.coroutines.launch
import java.lang.Exception

private const val TAG = "CategoriesVM"

class CategoriesViewModel : ViewModel() {

    private val _categoriesLiveData = MutableLiveData<List<Cocktail>>()
    val categoriesLiveData: LiveData<List<Cocktail>> = _categoriesLiveData

    private val _categoryItemsLiveData = MutableLiveData<List<Cocktail>>()
    val categoryItemsLiveData: LiveData<List<Cocktail>> = _categoryItemsLiveData

    private val _statusCategoriesLivaData = MutableLiveData<Status>()
    val statusCategoriesLivaData: LiveData<Status> = _statusCategoriesLivaData

    private val _statusCocktailsLivaData = MutableLiveData<Status>()
    val statusCocktailsLivaData: LiveData<Status> = _statusCocktailsLivaData

    private val retrofitService = CocktailDbApi.retrofitService

    init {
        Log.d(TAG, "init viewModel")
        _statusCategoriesLivaData.value = Status.ERROR
        _statusCocktailsLivaData.value = Status.ERROR
        viewModelScope.launch {
            getCategories()
        }

    }

    private suspend fun getCategories() {

        val categories = mutableListOf<Cocktail>()
        if (_statusCategoriesLivaData.value == Status.ERROR || _statusCategoriesLivaData.value == Status.OK && categories.isEmpty()) {

                try {

                    _statusCategoriesLivaData.value = Status.LOADING
                    val categoriesCocktailList = retrofitService.getCategories()
                    for (cocktail in categoriesCocktailList.responseData) {
                        categories.add(cocktail)
                        Log.d(TAG, "${cocktail.category}")
                    }
                    Log.d(TAG, "\n\n")
                    _categoriesLiveData.value = categories
                    _statusCategoriesLivaData.value = Status.OK

                } catch (e: Exception) {
                    _statusCategoriesLivaData.value = Status.ERROR
                    e.message?.let {
                        Log.d(TAG, it)
                    }
                }

        }
    }

    suspend fun fetchCategoryItems(category: String) {
        Log.d(TAG, "fetch started")
        val cocktails = mutableListOf<Cocktail>()



        if (_statusCocktailsLivaData.value == Status.OK || _statusCocktailsLivaData.value == Status.ERROR) {

                Log.d(TAG, "fetchCategoryItems: scope launched")
                _statusCocktailsLivaData.value = Status.LOADING
                Log.d(TAG, "status change to LOADING")

                val cocktailList = retrofitService.getCategoryItems(category)
                for (cocktail in cocktailList.responseData) {
//                    Log.d(TAG, "$cocktail")
                    cocktails.add(cocktail)
//                    Log.d(TAG, "${cocktail.category}")
                }
                _categoryItemsLiveData.value = cocktails
                _statusCocktailsLivaData.value = Status.OK
                Log.d(TAG, "status change to OK")

            Log.d(TAG, "exit scope")
        }
    }
}