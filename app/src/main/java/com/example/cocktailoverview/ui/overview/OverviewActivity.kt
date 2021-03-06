package com.example.cocktailoverview.ui.overview

import android.content.res.Configuration
import android.graphics.Color
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import coil.load
import coil.transform.RoundedCornersTransformation
import com.example.cocktailoverview.CocktailOverviewApplication
import com.example.cocktailoverview.R
import com.example.cocktailoverview.data.Repository
import com.example.cocktailoverview.databinding.ActivityOverviewBinding
import com.example.cocktailoverview.data.Status

class OverviewActivity : AppCompatActivity() {

    private val viewModel: CocktailOverviewViewModel by viewModels {
        CocktailOverviewViewModelFactory(
            repository,
            application as CocktailOverviewApplication
        )

    }

    private var _binding:ActivityOverviewBinding? = null
    private val binding get() = _binding!!
    private lateinit var thumbImageView: ImageView
    private lateinit var nameTextView: TextView
    private lateinit var favoriteImageView: ImageView
    private lateinit var idTextView: TextView
    private lateinit var categoryTextView: TextView
    private lateinit var alcoholicTextView: TextView
    private lateinit var glassTextView: TextView
    private lateinit var ingredientListTextView: TextView
    private lateinit var repository: Repository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityOverviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        repository = Repository.getRepository(application as CocktailOverviewApplication)

        val args = intent.extras
        val cocktailId = args?.getString("cocktail_id")
        if (cocktailId != null) {
            viewModel.getCocktailById(cocktailId)
        }

        setFullScreen()
        bind(cocktailId)

    }

    override fun onResume() {

        viewModel.statusLivaData.observe(this) { status ->
            when (status!!) {
                Status.OK -> {
                    binding.cocktailErrorTextView.visibility = View.GONE
                    binding.cocktailProgressBar.visibility = View.GONE
                    binding.cocktailInfoLayout.visibility = View.VISIBLE

                    viewModel.cocktailLiveData.observe(this) { cocktail ->
                        if (cocktail!!.thumbnailUrl!!.isEmpty()) {
                            thumbImageView.load(R.drawable.cocktail_mockup) {
                                transformations(
                                    RoundedCornersTransformation(50f)
                                )
                            }
                        } else {
                            val imageUri = Uri.parse(cocktail.thumbnailUrl)
                            thumbImageView.load(viewModel.thumbnailBitmap) {
                                placeholder(R.drawable.loading_animation)
                                transformations(RoundedCornersTransformation(50f))
//                                error(R.drawable.ic_baseline_broken_image_24)
                            }
                        }

                        nameTextView.text = cocktail.name
                        if (cocktail.isFavorite) {
                            favoriteImageView.load(R.drawable.ic_baseline_favorite_liked)
                        } else {
                            favoriteImageView.load(R.drawable.ic_baseline_favorite_border)
                        }
                        favoriteImageView.setOnClickListener {
                            if (cocktail.isFavorite) {
                                cocktail.isFavorite = false
                                viewModel.deleteFromFavorite()
                                favoriteImageView.load(R.drawable.ic_baseline_favorite_border)
                            } else {
                                cocktail.isFavorite = true
                                viewModel.addToFavorite()
                                favoriteImageView.load(R.drawable.ic_baseline_favorite_liked)
                            }
                        }
                        idTextView.text = String.format(getString(R.string.id), cocktail.id)
                        categoryTextView.text = cocktail.category
                        alcoholicTextView.text = cocktail.alcoholic
                        glassTextView.text =
                            String.format(getString(R.string.glass), cocktail.glass)
                        ingredientListTextView.text = ""
                        for (i in 1..cocktail.ingredientList.size) {
                            if (!cocktail.ingredientList[i - 1].isNullOrEmpty()) {
                                ingredientListTextView.append("$i. ${cocktail.ingredientList[i - 1]} \n")
                            }
                        }
                        ingredientListTextView.text.trim()

                    }
                }
                Status.LOADING -> {
                    binding.cocktailInfoLayout.visibility = View.GONE
                    binding.cocktailErrorTextView.visibility = View.GONE
                    binding.cocktailProgressBar.visibility = View.VISIBLE
                }
                Status.ERROR -> {
                    binding.cocktailInfoLayout.visibility = View.GONE
                    binding.cocktailProgressBar.visibility = View.GONE
                    binding.cocktailErrorTextView.visibility = View.VISIBLE
                }
            }
        }
        super.onResume()
    }

    private fun setFullScreen() {
        this.window.apply {
            clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)

            when (context.resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK)) {
                Configuration.UI_MODE_NIGHT_YES -> {
                    decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                }
                Configuration.UI_MODE_NIGHT_NO -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
                        decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or
                                View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
                    }
                }
                Configuration.UI_MODE_NIGHT_UNDEFINED -> {
                    decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                }
            }


            statusBarColor = Color.TRANSPARENT
        }
    }

    private fun bind(id: String?) {

        binding.apply {
            swipeRefresh.setOnRefreshListener {
                if (viewModel.statusLivaData.value == Status.ERROR) {
                    swipeRefresh.isRefreshing = true
                    if (id != null) {
                        viewModel.getCocktailById(id)
                    }
                    swipeRefresh.isRefreshing = false
                } else swipeRefresh.isRefreshing = false

            }
            thumbImageView = ivThumbnail
            nameTextView = tvName
            favoriteImageView = ivFavorite
            idTextView = tvId
            categoryTextView = tvCategory
            alcoholicTextView = tvAlcoholic
            glassTextView = tvGlass
            ingredientListTextView = tvIngredientsList
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}

