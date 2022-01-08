package com.example.cocktailoverview.ui.main

import android.util.Log
import androidx.lifecycle.*
import com.example.cocktailoverview.data.Cocktail
import com.example.cocktailoverview.data.Status
import com.example.cocktailoverview.data.network.CocktailDbApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.lang.Exception

private const val TAG = "MainVM"

class MainViewModel : ViewModel() {

    private val _cocktailsLiveData = MutableLiveData<ArrayList<Cocktail>>()
    val cocktailsLiveData: LiveData<ArrayList<Cocktail>> = _cocktailsLiveData

    private val _statusLivaData = MutableLiveData<Status>()
    val statusLivaData: LiveData<Status> = _statusLivaData

    val mutex = Mutex()

    init {
        _statusLivaData.value = Status.UNDEFINED
    }

    fun textChanged(query: String) {
        if (query.isEmpty()) {
            _statusLivaData.value = Status.UNDEFINED
        }
        Log.d(TAG, "textChanged: $query")
//        viewModelScope.cancel()
//        _cocktailsLiveData.value?.clear()
        fetchItems(query)
    }

    fun submitPressed(query: String): String? {
        Log.d(TAG, "submitPressed: $query")
        return cocktailsLiveData.value?.get(0)?.id
    }

    private fun fetchItems(query: String) {


        if (_statusLivaData.value != Status.LOADING) {

            viewModelScope.launch {
                mutex.withLock {
                    val loadingJob = launch {
                    delay(100)
                        _statusLivaData.value = Status.LOADING
                    }

                    val cocktails = ArrayList<Cocktail>()

                    try {
                        val cocktailList = CocktailDbApi.retrofitService.searchByName(query)
                        if(cocktailList.responseData.isNullOrEmpty()) {
                            _statusLivaData.value = Status.NOT_FOUND
                            Log.d(TAG, "${_statusLivaData.value}")
                            loadingJob.cancel()
                            this.cancel()
                            return@launch
                        }
                        for (cocktail in cocktailList.responseData) {
                            cocktails.add(cocktail)
                        }
                        _cocktailsLiveData.value = cocktails
                        loadingJob.cancel()
                        Log.d(TAG, "$cocktails")
                        _statusLivaData.value = Status.OK
                    } catch (e: Exception) {
                        loadingJob.cancel()
                        _statusLivaData.value = Status.ERROR
                        Log.d(TAG, "${_statusLivaData.value}")
                        e.message?.let { Log.d(TAG, it) }
                        e.printStackTrace()
                    }
                }
            }
        }
    }
}

class MainViewModelFactory() : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}