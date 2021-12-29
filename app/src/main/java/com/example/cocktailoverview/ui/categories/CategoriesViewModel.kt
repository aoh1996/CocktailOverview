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

    private val _statusLivaData = MutableLiveData<Status>()
    val statusLivaData: LiveData<Status> = _statusLivaData

    private val retrofitService = CocktailDbApi.retrofitService

    init {
        _statusLivaData.value = Status.ERROR
        getCategories()
    }

    fun getCategories() : List<Cocktail> {

        val categories = mutableListOf<Cocktail>()

        viewModelScope.launch {
            try {
                if (_statusLivaData.value == Status.ERROR || _statusLivaData.value == Status.OK) {
                    _statusLivaData.value = Status.LOADING
                    val categoriesCocktailList = retrofitService.getCategories()
                    for(cocktail in categoriesCocktailList.responseData) {
                        categories.add(cocktail)
                        Log.d(TAG, "${cocktail.category}")
                    }
                    Log.d(TAG, "\n\n")
                    _categoriesLiveData.value = categories
                    _statusLivaData.value = Status.OK
                }
            } catch (e: Exception) {
                _statusLivaData.value = Status.ERROR
                e.message?.let {
                    Log.d(TAG, it)
                }
            }
        }
        return categories
    }
}