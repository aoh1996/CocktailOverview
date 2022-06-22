package com.example.cocktailoverview.ui.favorite

import android.util.Log
import androidx.lifecycle.*
import com.example.cocktailoverview.data.Cocktail
import com.example.cocktailoverview.data.Repository
import com.example.cocktailoverview.data.db.toCocktail
import kotlinx.coroutines.launch

private const val TAG = "FavoritesVM"
class FavoritesViewModel(private val repository: Repository) : ViewModel() {

    private val favoritesRepo = repository.FavoritesRepo()

    private val _cocktailsLiveData = MutableLiveData<ArrayList<Cocktail>>()
    val cocktailsLiveData: LiveData<ArrayList<Cocktail>> = _cocktailsLiveData

    private var cocktailList = ArrayList<Cocktail>()

    init {
        getFavorites()
    }

    fun getFavorites() {


        cocktailList = arrayListOf()
        viewModelScope.launch {
            val list = favoritesRepo.getAll()
            for (item in list) {
                cocktailList.add(
                item.toCocktail()
                )
            }
            Log.d(TAG, "$list")
            _cocktailsLiveData.value = cocktailList
        }

    }

    fun removeFromFavorites(id: String?) {
        if (!id.isNullOrEmpty()) {
            viewModelScope.launch {
                val item = favoritesRepo.getById(id.toInt())
                favoritesRepo.remove(item)
            }
        }
    }

}



class FavoritesViewModelFactory(private val repository: Repository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FavoritesViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FavoritesViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}