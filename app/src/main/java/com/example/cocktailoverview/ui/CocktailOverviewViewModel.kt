package com.example.cocktailoverview.ui

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.util.Log
import androidx.lifecycle.*
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.example.cocktailoverview.CocktailOverviewApplication
import com.example.cocktailoverview.data.Cocktail
import com.example.cocktailoverview.data.network.CocktailDbApi
import com.example.cocktailoverview.data.Status
import com.example.cocktailoverview.data.db.DatabaseItem
import com.example.cocktailoverview.data.db.FavoritesDAO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception
import java.util.*

private const val TAG = "CocktailOverviewVM"

class CocktailOverviewViewModel(private val application: CocktailOverviewApplication) : AndroidViewModel(application) {

    private val favoritesDao: FavoritesDAO = application.favoritesDatabase.favoritesDao()

    private val _cocktailLiveData = MutableLiveData<Cocktail>()
    val cocktailLiveData: LiveData<Cocktail> = _cocktailLiveData

    private val _statusLivaData = MutableLiveData<Status>()
    val statusLivaData: LiveData<Status> = _statusLivaData

    private val retrofitService = CocktailDbApi.retrofitService

    private var databaseItem: DatabaseItem? = null

    var thumbnailBitmap: Bitmap? = null

    init {
        _statusLivaData.value = Status.UNDEFINED
    }

    fun getCocktailById(id: String) {

        if (_statusLivaData.value != Status.LOADING) {

            viewModelScope.launch {
                _statusLivaData.value = Status.LOADING
                databaseItem = null
                thumbnailBitmap = null
                try {
                    val cocktailList = retrofitService.getCocktailById(id)
                    if(cocktailList.responseData.isNullOrEmpty()) {
                        _statusLivaData.value = Status.ERROR
                        Log.d(TAG, "${_statusLivaData.value}")
                        this.cancel()
                        return@launch
                    }
                    val currentCocktail = cocktailList.responseData[0]

                    withContext(Dispatchers.IO) {
                        val loader = ImageLoader(application.applicationContext)
                        val request = ImageRequest.Builder(application.applicationContext)
                            .data(currentCocktail.thumbnailUrl)
                            .allowHardware(false) // Disable hardware bitmaps.
                            .build()

                        val result = (loader.execute(request) as SuccessResult).drawable
                        thumbnailBitmap = (result as BitmapDrawable).bitmap
                        currentCocktail.isFavorite = favoritesDao.isRowExist(currentCocktail.id?.toInt()!!)
                    }

                    _cocktailLiveData.value = currentCocktail
                    databaseItem = createDatabaseItem()

                    _statusLivaData.value = Status.OK
                } catch (e: Exception) {
                    databaseItem = null
                    thumbnailBitmap = null
                    _statusLivaData.value = Status.ERROR
                    e.printStackTrace()
                    e.message?.let {
                        Log.d(TAG, it)
                    }
                }
            }
        }
    }


    private fun createDatabaseItem(): DatabaseItem {
        val localIngredientsList = LinkedList<String>()
        for (ingredient in cocktailLiveData.value?.ingredientList!!) {
            if (!ingredient.isNullOrEmpty()) {
                localIngredientsList.add(ingredient)
            }
        }
        val databaseItem = DatabaseItem(
            cocktailLiveData.value?.id?.toInt()!!,
            cocktailLiveData.value?.name!!,
            cocktailLiveData.value?.thumbnailUrl!!,
            null
//            cocktailLiveData.value?.alcoholic!!,
//            cocktailLiveData.value?.category!!,
//            cocktailLiveData.value?.glass!!,
//            localIngredientsList
        )
        return databaseItem
    }

    fun addToFavorite() {
        viewModelScope.launch {
            databaseItem?.let { favoritesDao.insertWithTimestamp(it) }
        }
    }

    fun deleteFromFavorite() {
        viewModelScope.launch {
            databaseItem?.let { favoritesDao.delete(it) }
        }
    }

}

/**
 * Factory class to instantiate the [ViewModel] instance.
 */
class CocktailOverviewViewModelFactory(private val application: CocktailOverviewApplication) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CocktailOverviewViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CocktailOverviewViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}