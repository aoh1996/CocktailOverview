package com.example.cocktailoverview.ui.main

import android.util.Log
import androidx.lifecycle.*
import com.example.cocktailoverview.data.Cocktail
import com.example.cocktailoverview.data.Repository
import com.example.cocktailoverview.data.Status
import com.example.cocktailoverview.data.db.DatabaseItem
import com.example.cocktailoverview.data.db.toCocktail
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.lang.Exception

private const val TAG = "MainVM"

class MainViewModel(private val repository: Repository) : ViewModel() {

//    private val historyDao: HistoryDAO = application.historyDatabase.historyDao()

    private val historyRepo = repository.HistoryRepo()
    private val remoteRepo = repository.RemoteRepo()

    private val _cocktailsLiveData = MutableLiveData<ArrayList<Cocktail>>()
    val cocktailsLiveData: LiveData<ArrayList<Cocktail>> = _cocktailsLiveData

    private val _statusLivaData = MutableLiveData<Status>()
    val statusLivaData: LiveData<Status> = _statusLivaData

    var pendingQuery: String = ""

    val mutex = Mutex()

    init {
        _statusLivaData.value = Status.UNDEFINED
        if (pendingQuery.isEmpty()) {
            getHistory()
        } else {
            makeSearch()
        }
    }

    fun makeSearch() {
        Log.d(TAG, "textChanged: $pendingQuery")
        fetchItems(pendingQuery)

    }

    fun submitPressed(): String? {
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
            historyRepo.addToHistory(item)
            historyRepo.deleteOld()
            delay(200)
            getHistory()
        }
    }

    fun removeFromHistory(id: String?) {
        if (!id.isNullOrEmpty()) {
            viewModelScope.launch {
                val item = historyRepo.getById(id.toInt())
                historyRepo.deleteItem(item)
            }
        }
    }

    fun getHistory() {
        Log.d(TAG, "getHistory: called")
        val cocktailList = ArrayList<Cocktail>()
        viewModelScope.launch {
            val list = historyRepo.getHistory()
            if (list.isNotEmpty()) {
                for (cocktailItem in list) {
                    cocktailList.add(
                        cocktailItem.toCocktail()
                    )
                }
                _cocktailsLiveData.value = cocktailList
                Log.d(TAG, "getHistory: LivaData updated")
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
                        val cocktailList = remoteRepo.getByName(query)

                        if(cocktailList.isNullOrEmpty()) {
                            _statusLivaData.value = Status.NOT_FOUND
                            Log.d(TAG, "${_statusLivaData.value}")
                            loadingJob.cancel()
                            this.cancel()
                            return@launch
                        }
                        for (cocktail in cocktailList) {
                            cocktails.add(cocktail)
                        }
                        _cocktailsLiveData.value = cocktails
                        Log.d(TAG, "fetchItems: livaData updated")
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

class MainViewModelFactory(private val repository: Repository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}