package com.example.cocktailoverview.ui.overview

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.util.Log
import androidx.lifecycle.*
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.example.cocktailoverview.CocktailOverviewApplication
import com.example.cocktailoverview.data.Cocktail
import com.example.cocktailoverview.data.Repository
import com.example.cocktailoverview.data.Status
import com.example.cocktailoverview.data.db.DatabaseItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception
import java.util.*

private const val TAG = "CocktailOverviewVM"

class CocktailOverviewViewModel(repository: Repository, private val application: CocktailOverviewApplication) : AndroidViewModel(application) {


    private val favoritesRepo = repository.FavoritesRepo()
    private val remoteRepo = repository.RemoteRepo()

    private val _cocktailLiveData = MutableLiveData<Cocktail>()
    val cocktailLiveData: LiveData<Cocktail> = _cocktailLiveData

    private val _statusLivaData = MutableLiveData<Status>()
    val statusLivaData: LiveData<Status> = _statusLivaData


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
                    val currentCocktail = remoteRepo.getById(id)

                    if(currentCocktail == null) {
                        _statusLivaData.value = Status.ERROR
                        Log.d(TAG, "${_statusLivaData.value}")
                        this.cancel()
                        return@launch
                    }
//                    val currentCocktail = cocktailList.responseData[0]

                    withContext(Dispatchers.IO) {
                        val loader = ImageLoader(application.applicationContext)
                        val request = ImageRequest.Builder(application.applicationContext)
                            .data(currentCocktail.thumbnailUrl)
                            .allowHardware(false) // Disable hardware bitmaps.
                            .build()

                        val result = (loader.execute(request) as SuccessResult).drawable
                        thumbnailBitmap = (result as BitmapDrawable).bitmap
                        currentCocktail.isFavorite = favoritesRepo.isFavorite(currentCocktail.id?.toInt()!!)
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
        val localIngredientsList = ArrayList<String>()
        for (ingredient in cocktailLiveData.value?.ingredientList!!) {
            if (!ingredient.isNullOrEmpty()) {
                localIngredientsList.add(ingredient)
            }
        }
        return DatabaseItem(
            cocktailLiveData.value?.id?.toInt()!!,
            cocktailLiveData.value?.name!!,
            cocktailLiveData.value?.thumbnailUrl!!,
            null
        )
    }

    fun addToFavorite() {
        viewModelScope.launch {
            databaseItem?.let { favoritesRepo.add(it) }
        }
    }

    fun deleteFromFavorite() {
        viewModelScope.launch {
            databaseItem?.let { favoritesRepo.remove(it) }
        }
    }

}

/**
 * Factory class to instantiate the [ViewModel] instance.
 */
class CocktailOverviewViewModelFactory(private val repository: Repository, private val application: CocktailOverviewApplication) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CocktailOverviewViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CocktailOverviewViewModel(repository, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}