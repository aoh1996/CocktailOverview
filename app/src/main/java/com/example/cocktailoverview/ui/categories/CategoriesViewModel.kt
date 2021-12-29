package com.example.cocktailoverview.ui.categories

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cocktailoverview.network.Cocktail
import com.example.cocktailoverview.network.CocktailDbApi
import com.example.cocktailoverview.network.Status
import kotlinx.coroutines.launch
import java.lang.Exception

private const val TAG = "CategoriesVM"
class CategoriesViewModel : ViewModel() {

    private val _categoriesLiveData = MutableLiveData<List<String?>>()
    val categoriesLiveData: LiveData<List<String?>> = _categoriesLiveData

    private val _statusLivaData = MutableLiveData<Status>()
    val statusLivaData: LiveData<Status> = _statusLivaData

    private val retrofitService = CocktailDbApi.retrofitService

    init {
        _statusLivaData.value = Status.ERROR
        getCategories()
    }

    fun getCategories() {

        val categories = mutableListOf<String?>()

        viewModelScope.launch {
            try {
                if (_statusLivaData.value == Status.ERROR || _statusLivaData.value == Status.OK) {
                    _statusLivaData.value = Status.LOADING
                    val categoriesCocktailList = retrofitService.getCategories()
                    for(cocktail in categoriesCocktailList.responseData) {
                        categories.add(cocktail.category!!)
                    }
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
    }
}