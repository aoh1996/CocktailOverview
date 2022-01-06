package com.example.cocktailoverview.ui.favorite

import android.util.Log
import androidx.lifecycle.*
import com.example.cocktailoverview.CocktailOverviewApplication
import com.example.cocktailoverview.data.Cocktail
import com.example.cocktailoverview.data.db.FavoritesDAO
import kotlinx.coroutines.launch

private const val TAG = "FavoritesVM"
class FavoritesViewModel(private val application: CocktailOverviewApplication) : AndroidViewModel(application) {

    private val favoritesDao: FavoritesDAO = application.favoritesDatabase.favoritesDao()

    private val _cocktailsLiveData = MutableLiveData<List<Cocktail>>()
    val cocktailsLiveData: LiveData<List<Cocktail>> = _cocktailsLiveData

    private var cocktailList: MutableList<Cocktail>? = null
    private var cocktailFlow: MutableList<Cocktail>? = null

    init {
        getFavorites()
    }

    fun getFavorites() {
        cocktailList = mutableListOf()
        viewModelScope.launch {
            val list = favoritesDao.getCocktails()
            for (cocktail in list) {
                cocktailList!!.add(
                    Cocktail(cocktail.id.toString(), cocktail.name, cocktail.thumbnailUrl)
                )
            }
            Log.d(TAG, "$list")
            _cocktailsLiveData.value = cocktailList!!
        }
    }



}

class FavoritesViewModelFactory(private val application: CocktailOverviewApplication) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FavoritesViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FavoritesViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}