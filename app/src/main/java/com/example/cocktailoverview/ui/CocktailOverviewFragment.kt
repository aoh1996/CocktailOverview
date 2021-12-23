package com.example.cocktailoverview.ui

import android.net.Uri
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.RoundedCorner
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.viewModels
import coil.load
import coil.transform.CircleCropTransformation
import coil.transform.RoundedCornersTransformation
import com.example.cocktailoverview.R
import com.example.cocktailoverview.databinding.CocktailOverviewFragmentBinding

private const val TAG = "CocktailOverviewFrag"
class CocktailOverviewFragment : Fragment() {

    private val viewModel by viewModels<CocktailOverviewViewModel>()

    private lateinit var imageView: ImageView
    private lateinit var nameTextView: TextView
    private lateinit var favoriteImageView: ImageView
    private lateinit var idTextView: TextView
    private lateinit var categoryTextView: TextView
    private lateinit var alcoholicTextView: TextView
    private lateinit var glassTextView: TextView
    private lateinit var ingredientListTextView: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding = CocktailOverviewFragmentBinding.inflate(inflater)

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



        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        viewModel.statusLivaData.observe(this, {status ->
            when(status!!) {
                Status.OK -> {
                    view.findViewById<TextView>(R.id.errorTextView).visibility = View.GONE
                    view.findViewById<ProgressBar>(R.id.progressBar).visibility = View.GONE
                    view.findViewById<LinearLayout>(R.id.cocktailInfoLayout).visibility = View.VISIBLE

                    viewModel.randomCocktailLiveData.observe(this, {cocktail ->
                        if(cocktail!!.thumbnailUrl.isEmpty()) {
                            imageView.load(R.drawable.cocktail_mockup) {transformations(RoundedCornersTransformation(50f))}
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
                        super.onViewCreated(view, savedInstanceState)
                    })
                }
                Status.LOADING -> {
                    view.findViewById<LinearLayout>(R.id.cocktailInfoLayout).visibility = View.GONE
                    view.findViewById<TextView>(R.id.errorTextView).visibility = View.GONE
                    view.findViewById<ProgressBar>(R.id.progressBar).visibility = View.VISIBLE
                }
                Status.ERROR -> {
                    view.findViewById<LinearLayout>(R.id.cocktailInfoLayout).visibility = View.GONE
                    view.findViewById<ProgressBar>(R.id.progressBar).visibility = View.GONE
                    view.findViewById<TextView>(R.id.errorTextView).visibility = View.VISIBLE
                }
            }
        })





    }

//    override fun onActivityCreated(savedInstanceState: Bundle?) {
//        super.onActivityCreated(savedInstanceState)
//        // TODO: Use the ViewModel
//        viewModel.getRandomCocktail()
//    }

}