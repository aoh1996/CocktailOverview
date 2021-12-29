package com.example.cocktailoverview.ui

import android.content.res.Configuration
import android.graphics.Color
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.viewModels
import coil.load
import coil.transform.RoundedCornersTransformation
import com.example.cocktailoverview.R
import com.example.cocktailoverview.databinding.ActivityOverviewBinding
import com.example.cocktailoverview.data.Status

class OverviewActivity : AppCompatActivity() {

    private val viewModel by viewModels<CocktailOverviewViewModel>()

    private lateinit var binding: ActivityOverviewBinding
    private lateinit var imageView: ImageView
    private lateinit var nameTextView: TextView
    private lateinit var favoriteImageView: ImageView
    private lateinit var idTextView: TextView
    private lateinit var categoryTextView: TextView
    private lateinit var alcoholicTextView: TextView
    private lateinit var glassTextView: TextView
    private lateinit var ingredientListTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOverviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setFullScreen()
        bind()

    }

    override fun onResume() {

        viewModel.statusLivaData.observe(this, {status ->
            when(status!!) {
                Status.OK -> {
                    findViewById<TextView>(R.id.errorTextView).visibility = View.GONE
                    findViewById<ProgressBar>(R.id.progressBar).visibility = View.GONE
                    findViewById<LinearLayout>(R.id.cocktailInfoLayout).visibility = View.VISIBLE

                    viewModel.randomCocktailLiveData.observe(this, {cocktail ->
                        if(cocktail!!.thumbnailUrl!!.isEmpty()) {
                            imageView.load(R.drawable.cocktail_mockup) {transformations(
                                RoundedCornersTransformation(50f)
                            )}
                        } else {
                            val imageUri = Uri.parse(cocktail.thumbnailUrl)
                            imageView.load(imageUri){
                                placeholder(R.drawable.loading_animation)
                                transformations(RoundedCornersTransformation(50f))
                                error(R.drawable.ic_baseline_broken_image_24)
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
                                favoriteImageView.load(R.drawable.ic_baseline_favorite_border)
                            } else {
                                cocktail.isFavorite = true
                                favoriteImageView.load(R.drawable.ic_baseline_favorite_liked)
                            }
                        }
                        idTextView.text = String.format(getString(R.string.id), cocktail.id)
                        categoryTextView.text = cocktail.category
                        alcoholicTextView.text = cocktail.alcoholic
                        glassTextView.text = String.format(getString(R.string.glass), cocktail.glass)
                        ingredientListTextView.text = ""
                        for (i in 1..cocktail.ingredientList.size) {
                            if (!cocktail.ingredientList[i-1].isNullOrEmpty()) {
                                ingredientListTextView.append("$i. ${cocktail.ingredientList[i-1]} \n")
                            }
                        }
                        ingredientListTextView.text.trim()

                    })
                }
                Status.LOADING -> {
                    findViewById<LinearLayout>(R.id.cocktailInfoLayout).visibility = View.GONE
                    findViewById<TextView>(R.id.errorTextView).visibility = View.GONE
                    findViewById<ProgressBar>(R.id.progressBar).visibility = View.VISIBLE
                }
                Status.ERROR -> {
                    findViewById<LinearLayout>(R.id.cocktailInfoLayout).visibility = View.GONE
                    findViewById<ProgressBar>(R.id.progressBar).visibility = View.GONE
                    findViewById<TextView>(R.id.errorTextView).visibility = View.VISIBLE
                }
            }
        })
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
                }
                Configuration.UI_MODE_NIGHT_UNDEFINED -> {
                    decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                }
            }


            statusBarColor = Color.TRANSPARENT
        }
    }

    private fun bind() {
        binding.swipeRefresh.setOnRefreshListener {
            binding.swipeRefresh.isRefreshing = true
            viewModel.getRandomCocktail()
            binding.swipeRefresh.isRefreshing = false
        }
        imageView = binding.imageView
        nameTextView = binding.tvName
        favoriteImageView = binding.favoriteImageView
        idTextView = binding.tvId
        categoryTextView = binding.tvCategory
        alcoholicTextView = binding.tvAlcoholic
        glassTextView = binding.tvGlass
        ingredientListTextView = binding.tvIngredientsList
    }
}