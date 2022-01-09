package com.example.cocktailoverview.ui.favorite

import android.util.Log
import androidx.lifecycle.*
import com.example.cocktailoverview.CocktailOverviewApplication
import com.example.cocktailoverview.data.Cocktail
import com.example.cocktailoverview.data.db.DatabaseItem
import com.example.cocktailoverview.data.db.FavoritesDAO
import kotlinx.coroutines.launch

private const val TAG = "FavoritesVM"
class FavoritesViewModel(private val application: CocktailOverviewApplication) : AndroidViewModel(application) {

    private val favoritesDao: FavoritesDAO = application.favoritesDatabase.favoritesDao()

    private val _cocktailsLiveData = MutableLiveData<ArrayList<Cocktail>>()
    val cocktailsLiveData: LiveData<ArrayList<Cocktail>> = _cocktailsLiveData

    private var cocktailList = ArrayList<Cocktail>()

    init {
        getFavorites()
    }

    fun getFavorites() {
        cocktailList = arrayListOf()
        viewModelScope.launch {
            val list = favoritesDao.getCocktails()
            for (cocktail in list) {
                cocktailList.add(
                    Cocktail(cocktail.id.toString(), cocktail.name, cocktail.thumbnailUrl)
                )
            }
            Log.d(TAG, "$list")
            _cocktailsLiveData.value = cocktailList
        }
    }

    fun removeFromFavorites(id: String?) {
        if (!id.isNullOrEmpty()) {
            viewModelScope.launch {
                val item = favoritesDao.getCocktail(id.toInt())
                favoritesDao.delete(item)
            }
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