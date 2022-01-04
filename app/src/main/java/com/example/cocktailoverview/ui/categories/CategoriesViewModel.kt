package com.example.cocktailoverview.ui.categories

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cocktailoverview.data.Cocktail
import com.example.cocktailoverview.data.network.CocktailDbApi
import com.example.cocktailoverview.data.Status
import kotlinx.coroutines.delay
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
        Log.d(TAG, "init viewModel")
        _statusLivaData.value = Status.UNDEFINED
        getCategories()

    }

    private fun getCategories() {

        val categories = mutableListOf<Cocktail>()
        if (_statusLivaData.value == Status.ERROR || _statusLivaData.value == Status.UNDEFINED ||
            _statusLivaData.value == Status.OK && categories.isEmpty()) {

            viewModelScope.launch {

                val loadingJob = launch {
                    delay(100)
                    _statusLivaData.value = Status.LOADING
                }
                try {


                    val categoriesCocktailList = retrofitService.getCategories()
                    for (cocktail in categoriesCocktailList.responseData) {
                        categories.add(cocktail)
                        Log.d(TAG, "${cocktail.category}")
                    }
                    Log.d(TAG, "\n\n")
                    _categoriesLiveData.value = categories
                    loadingJob.cancel()
                    _statusLivaData.value = Status.OK

                } catch (e: Exception) {
                    loadingJob.cancel()
                    _statusLivaData.value = Status.ERROR
                    e.message?.let {
                        Log.d(TAG, it)
                    }
                }

            }

        }
    }


}