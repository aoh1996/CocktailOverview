package com.example.cocktailoverview.ui.favorite

import android.util.Log
import androidx.lifecycle.*
import com.example.cocktailoverview.CocktailOverviewApplication
import com.example.cocktailoverview.data.Cocktail
import com.example.cocktailoverview.data.db.FavoritesDAO
import kotlinx.coroutines.flow.map
import kotlin.math.log

private const val TAG = "FavoritesVM"
class FavoritesViewModel(private val application: CocktailOverviewApplication) : AndroidViewModel(application) {

    private val favoritesDao: FavoritesDAO = application.favoritesDatabase.favoritesDao()

    private val _cocktailsLiveData = MutableLiveData<List<Cocktail>>()
    val cocktailsLiveData: LiveData<List<Cocktail>> = _cocktailsLiveData

    private var cocktailList: MutableList<Cocktail>? = null
    private var cocktailFlow: MutableList<Cocktail>? = null

    init {
        cocktailList = mutableListOf<Cocktail>()
        Log.d(TAG, "startFlow")
        val flow = favoritesDao.getCocktails()
        Log.d(TAG, "${flow.value}")
//        {
//            Log.d(TAG, "FlowList: $it")
//            for (favItem in it) {
//                cocktailList!!.add(
//                    Cocktail(favItem.id.toString(),
//                                favItem.name,
//                                favItem.thumbnailUrl, null,null,null,null,null,null,
//                    null,null,null,null,null,null,null,null,null,null,
//                    null,null)
//                )
//            }
//        }
//        _cocktailsLiveData.value = cocktailList!!
//        Log.d(TAG, "$cocktailList")
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