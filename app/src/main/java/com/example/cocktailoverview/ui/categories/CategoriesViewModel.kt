package com.example.cocktailoverview.ui.categories

import android.util.Log
import androidx.lifecycle.*
import com.example.cocktailoverview.data.Cocktail
import com.example.cocktailoverview.data.Repository
import com.example.cocktailoverview.data.network.CocktailDbApi
import com.example.cocktailoverview.data.Status
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.lang.Exception

private const val TAG = "CategoriesVM"

class CategoriesViewModel(private val repository: Repository) : ViewModel() {

    private val _categoriesLiveData = MutableLiveData<List<Cocktail>>()
    val categoriesLiveData: LiveData<List<Cocktail>> = _categoriesLiveData

    private val _statusLivaData = MutableLiveData<Status>()
    val statusLivaData: LiveData<Status> = _statusLivaData

//    private val retrofitService = CocktailDbApi.retrofitService

    private val remoteRepo = repository.RemoteRepo()

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
                    delay(200)
                    _statusLivaData.value = Status.LOADING
                }
                try {


                    val categoriesCocktailList = remoteRepo.getCategories()
                    if(categoriesCocktailList.isNullOrEmpty()) {
                        _statusLivaData.value = Status.NOT_FOUND
                        Log.d(TAG, "${_statusLivaData.value}")
                        this.cancel()
                    }
                    for (cocktail in categoriesCocktailList!!) {
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
                    e.printStackTrace()
                    e.message?.let {
                        Log.d(TAG, it)
                    }
                }

            }

        }
    }

}
class CategoriesViewModelFactory(private val repository: Repository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CategoriesViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CategoriesViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}