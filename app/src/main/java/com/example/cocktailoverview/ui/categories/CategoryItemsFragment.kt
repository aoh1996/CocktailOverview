package com.example.cocktailoverview.ui.categories

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cocktailoverview.R
import com.example.cocktailoverview.data.Cocktail
import com.example.cocktailoverview.data.Status
import com.example.cocktailoverview.databinding.CategoriesFragmentBinding
import com.example.cocktailoverview.databinding.FragmentCategoryItemsBinding
import com.example.cocktailoverview.ui.OverviewActivity
import com.example.cocktailoverview.ui.adapters.CategoriesAdapter
import com.example.cocktailoverview.ui.adapters.CocktailsAdapter


private const val TAG = "CatItemsFrag"

class CategoryItemsFragment : Fragment() {

    private val viewModel by viewModels<CategoryItemsViewModel>()
    private val args: CategoryItemsFragmentArgs by navArgs()

    private var _binding: FragmentCategoryItemsBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: CocktailsAdapter
    private lateinit var cocktailList: List<Cocktail>
    private var category: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        _binding = FragmentCategoryItemsBinding.inflate(inflater, container, false)
        binding.categoryItemsRecycler.layoutManager = LinearLayoutManager(context)
        cocktailList = emptyList()
        adapter = CocktailsAdapter(cocktailList) {position -> onListItemClick(position)}
        binding.categoryItemsRecycler.overScrollMode
        binding.categoryItemsRecycler.adapter = adapter
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        category = args.category
        category?.let { viewModel.fetchCategoryItems(it) }
        viewModel.statusLivaData.observe(viewLifecycleOwner, {status ->
            Log.d(TAG, "categories load status: $status")
            when(status!!) {
                Status.OK -> {
                    viewModel.categoryItemsLiveData.observe(viewLifecycleOwner, {cocktails ->
                        cocktailList = cocktails
                        Log.d(TAG, "$cocktailList")
                        adapter.updateList(cocktailList)
                    })
                }
            }
        })


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun onListItemClick(position: Int) {
        Log.d(TAG, "onListItemClick: ${cocktailList[position].category}")

        val id = cocktailList[position].id
        val intent = Intent(activity, OverviewActivity::class.java)
        intent.putExtra("cocktail_id", id)
        startActivity(intent)
//        val action = CategoriesFragmentDirections.actionNavigationCategoriesToNavigationCategoryItems(category)
//        findNavController().navigate(action)

    }
}