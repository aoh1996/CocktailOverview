package com.example.cocktailoverview.ui.categories

import android.util.Log
import androidx.lifecycle.*
import com.example.cocktailoverview.data.Cocktail
import com.example.cocktailoverview.data.Repository
import com.example.cocktailoverview.data.Status
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.lang.Exception

private const val TAG = "CatsItemsVW"

class CategoryItemsViewModel(private val repository: Repository) : ViewModel() {

    private val _categoryItemsLiveData = MutableLiveData<ArrayList<Cocktail>>()
    val categoryItemsLiveData: LiveData<ArrayList<Cocktail>> = _categoryItemsLiveData

    private val _statusLivaData = MutableLiveData<Status>()
    val statusLivaData: LiveData<Status> = _statusLivaData

    private val remoteRepo = repository.RemoteRepo()

    init {
        _statusLivaData.value = Status.UNDEFINED
    }

    fun fetchCategoryItems(category: String) {
        val cocktails = ArrayList<Cocktail>()

        if (_statusLivaData.value != Status.LOADING) {

            viewModelScope.launch {
                _statusLivaData.value = Status.LOADING
                delay(100)

                try {
                    val cocktailList = remoteRepo.getCategoryItems(category)
                    if(cocktailList.isNullOrEmpty()) {
                        _statusLivaData.value = Status.NOT_FOUND
                        Log.d(TAG, "${_statusLivaData.value}")
                        this.cancel()
                    }
                    for (cocktail in cocktailList!!) {
                        cocktails.add(cocktail)
                    }
                    _categoryItemsLiveData.value = cocktails
                    _statusLivaData.value = Status.OK
                } catch (e: Exception) {
                    _statusLivaData.value = Status.ERROR
                    _categoryItemsLiveData.value = arrayListOf()
                    Log.d(TAG, "${e.message}")
                }
            }
        }
    }
}

class CategoryItemsViewModelFactory(private val repository: Repository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CategoryItemsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CategoryItemsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}