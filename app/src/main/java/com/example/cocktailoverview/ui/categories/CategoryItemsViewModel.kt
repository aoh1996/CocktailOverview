package com.example.cocktailoverview.ui.categories

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cocktailoverview.data.Cocktail
import com.example.cocktailoverview.data.Status
import com.example.cocktailoverview.data.network.CocktailDbApi.retrofitService
import kotlinx.coroutines.launch
import java.lang.Exception

private const val TAG = "CatsItemsVW"

class CategoryItemsViewModel() : ViewModel() {

    private val _categoryItemsLiveData = MutableLiveData<List<Cocktail>>()
    val categoryItemsLiveData: LiveData<List<Cocktail>> = _categoryItemsLiveData

    private val _statusLivaData = MutableLiveData<Status>()
    val statusLivaData: LiveData<Status> = _statusLivaData

    init {
        _statusLivaData.value = Status.ERROR
    }

    fun fetchCategoryItems(category: String) {
        Log.d(TAG, "fetch started")

        val cocktails = mutableListOf<Cocktail>()

        if (_statusLivaData.value == Status.OK || _statusLivaData.value == Status.ERROR) {

            viewModelScope.launch {
                Log.d(TAG, "fetchCategoryItems: scope launched")
                _statusLivaData.value = Status.LOADING
                Log.d(TAG, "status change to LOADING")

                try {
                    val cocktailList = retrofitService.getCategoryItems(category)
                    for (cocktail in cocktailList.responseData) {
                        cocktails.add(cocktail)
                    }
                    _categoryItemsLiveData.value = cocktails
                    _statusLivaData.value = Status.OK
                } catch (e: Exception) {
                    _statusLivaData.value = Status.ERROR
                    _categoryItemsLiveData.value = emptyList()
                    Log.d(TAG, "${e.message}")
                }

                Log.d(TAG, "status change to OK")
            }

            Log.d(TAG, "exit scope")
        }
    }
}