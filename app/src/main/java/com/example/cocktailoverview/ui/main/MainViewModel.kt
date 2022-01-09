package com.example.cocktailoverview.ui.main

import android.util.Log
import androidx.lifecycle.*
import com.example.cocktailoverview.CocktailOverviewApplication
import com.example.cocktailoverview.data.Cocktail
import com.example.cocktailoverview.data.Status
import com.example.cocktailoverview.data.db.DatabaseItem
import com.example.cocktailoverview.data.db.FavoritesDAO
import com.example.cocktailoverview.data.db.HistoryDAO
import com.example.cocktailoverview.data.network.CocktailDbApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.lang.Exception
import kotlin.math.log

private const val TAG = "MainVM"

class MainViewModel(private val application: CocktailOverviewApplication) : AndroidViewModel(application) {

    private val historyDao: HistoryDAO = application.historyDatabase.historyDao()

    private val _cocktailsLiveData = MutableLiveData<ArrayList<Cocktail>>()
    val cocktailsLiveData: LiveData<ArrayList<Cocktail>> = _cocktailsLiveData

    private val _statusLivaData = MutableLiveData<Status>()
    val statusLivaData: LiveData<Status> = _statusLivaData

    val mutex = Mutex()

    init {
        _statusLivaData.value = Status.UNDEFINED
        getHistory()
    }

    fun textChanged(query: String) {
        Log.d(TAG, "textChanged: $query")
        fetchItems(query)
//        viewModelScope.cancel()
//        _cocktailsLiveData.value?.clear()

    }

    fun submitPressed(query: String): String? {
        Log.d(TAG, "submitPressed: $query")
        cocktailsLiveData.value?.get(0)?.let { addToHistory(it) }
        return cocktailsLiveData.value?.get(0)?.id
    }

    fun addToHistory(cocktail: Cocktail) {
        viewModelScope.launch {
            val item = DatabaseItem(
                cocktail.id!!.toInt(),
                cocktail.name!!,
                cocktail.thumbnailUrl!!,
                null
            )
            historyDao.insertWithTimestamp(item)
            historyDao.removeOldData()
        }
    }

    fun removeFromHistory(id: String?) {
        if (!id.isNullOrEmpty()) {
            viewModelScope.launch {
                val item = historyDao.getCocktail(id.toInt())
                historyDao.delete(item)
            }
        }
    }

    fun getHistory() {
        val cocktailList = ArrayList<Cocktail>()
        viewModelScope.launch {
            val list = historyDao.getCocktails()
            if (list.isNotEmpty()) {
                for (cocktail in list) {
                    cocktailList!!.add(
                        Cocktail(cocktail.id.toString(), cocktail.name, cocktail.thumbnailUrl)
                    )
                }
                _cocktailsLiveData.value = cocktailList
                _statusLivaData.value = Status.OK
            }
        }
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

class MainViewModelFactory(private val application: CocktailOverviewApplication) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}