package com.example.cocktailoverview.ui

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
import java.util.*

private const val TAG = "CocktailOverviewVM"
class CocktailOverviewViewModel : ViewModel() {

    private val _randomCocktailLiveData = MutableLiveData<Cocktail>()
    val randomCocktailLiveData: LiveData<Cocktail> = _randomCocktailLiveData

    private val _statusLivaData = MutableLiveData<Status>()
    val statusLivaData: LiveData<Status> = _statusLivaData

    private val retrofitService = CocktailDbApi.retrofitService

    private var ingredients: LinkedList<String?> = LinkedList()

    init {
        _statusLivaData.value = Status.OK
        try {
            getRandomCocktail()
        } catch (e: Exception) {
            e.message?.let { Log.d(TAG, it) }
        }
    }
    fun getRandomCocktail() {
        Log.d(TAG, "getRandomCocktail: started")
        viewModelScope.launch {
            try {
                if (_statusLivaData.value == Status.OK || _statusLivaData.value == Status.ERROR) {
                    _statusLivaData.value = Status.LOADING
                    Log.d("STATUS", "LOADING")
                    ingredients.clear()
                    val randomCocktailList = retrofitService.getRandom()
                    val currentCocktail = randomCocktailList.responseData[0]
                    _randomCocktailLiveData.value = currentCocktail
                    ingredients.apply {
                        add(currentCocktail.ingredient1)
                        add(currentCocktail.ingredient2)
                        add(currentCocktail.ingredient3)
                        add(currentCocktail.ingredient4)
                        add(currentCocktail.ingredient5)
                        add(currentCocktail.ingredient6)
                        add(currentCocktail.ingredient7)
                        add(currentCocktail.ingredient8)
                        add(currentCocktail.ingredient9)
                        add(currentCocktail.ingredient10)
                        add(currentCocktail.ingredient11)
                        add(currentCocktail.ingredient12)
                        add(currentCocktail.ingredient13)
                        add(currentCocktail.ingredient14)
                        add(currentCocktail.ingredient15)
                    }
                    _statusLivaData.value = Status.OK
                    Log.d("STATUS", "OK")
                }

            } catch (e: Exception) {
                _statusLivaData.value = Status.ERROR
                Log.d("STATUS", "ERROR")
                e.message?.let {
                    Log.d(TAG, it)
                }

                Log.d(TAG, "getRandomCocktail: finished")
            }
        }
    }
}