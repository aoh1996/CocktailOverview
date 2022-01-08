package com.example.cocktailoverview.ui.categories

import android.util.Log
import androidx.lifecycle.*
import com.example.cocktailoverview.data.Cocktail
import com.example.cocktailoverview.data.Status
import com.example.cocktailoverview.data.network.CocktailDbApi.retrofitService
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.lang.Exception

private const val TAG = "CatsItemsVW"

class CategoryItemsViewModel() : ViewModel() {

    private val _categoryItemsLiveData = MutableLiveData<List<Cocktail>>()
    val categoryItemsLiveData: LiveData<List<Cocktail>> = _categoryItemsLiveData

    private val _statusLivaData = MutableLiveData<Status>()
    val statusLivaData: LiveData<Status> = _statusLivaData

    init {
        _statusLivaData.value = Status.UNDEFINED
    }

    fun fetchCategoryItems(category: String) {
        Log.d(TAG, "fetch started")

        val cocktails = mutableListOf<Cocktail>()

        if (_statusLivaData.value == Status.OK || _statusLivaData.value == Status.ERROR || _statusLivaData.value == Status.UNDEFINED) {

            viewModelScope.launch {
                val loadingJob = launch {
                    delay(200)
                    _statusLivaData.value = Status.LOADING
                }

                try {
                    val cocktailList = retrofitService.getCategoryItems(category)
                    if(cocktailList.responseData.isNullOrEmpty()) {
                        _statusLivaData.value = Status.NOT_FOUND
                        Log.d(TAG, "${_statusLivaData.value}")
                        this.cancel()
                    }
                    for (cocktail in cocktailList.responseData!!) {
                        cocktails.add(cocktail)
                    }
                    _categoryItemsLiveData.value = cocktails
                    loadingJob.cancel()
                    _statusLivaData.value = Status.OK
                } catch (e: Exception) {
                    loadingJob.cancel()
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

class CategoryItemsViewModelFactory() : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CategoryItemsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CategoryItemsViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}