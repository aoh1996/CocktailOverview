package com.example.cocktailoverview.ui.categories

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cocktailoverview.data.Cocktail
import com.example.cocktailoverview.data.Status
import com.example.cocktailoverview.databinding.FragmentCategoryItemsBinding
import com.example.cocktailoverview.ui.OverviewActivity
import com.example.cocktailoverview.ui.adapters.CocktailsAdapter


private const val TAG = "CatItemsFrag"

class CategoryItemsFragment : Fragment() {

    private val viewModel: CategoryItemsViewModel by viewModels {
        CategoryItemsViewModelFactory()
    }

    private val args: CategoryItemsFragmentArgs by navArgs()

    private var _binding: FragmentCategoryItemsBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: CocktailsAdapter
    private lateinit var cocktailList: List<Cocktail>
    private var category: String? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        _binding = FragmentCategoryItemsBinding.inflate(inflater, container, false)
        binding.categoryItemsRecycler.layoutManager = LinearLayoutManager(context)
        cocktailList = emptyList()
        adapter = CocktailsAdapter(context!!, cocktailList) {position -> onListItemClick(position)}
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
                Status.UNDEFINED -> {
                    binding.categoryItemsRecycler.visibility = View.GONE
                    binding.categoryItemsProgressBar.visibility = View.GONE
                    binding.categoryItemsErrorTextView.visibility = View.GONE
                }
                Status.OK -> {
                    binding.categoryItemsErrorTextView.visibility = View.GONE
                    binding.categoryItemsProgressBar.visibility = View.GONE
                    viewModel.categoryItemsLiveData.observe(viewLifecycleOwner, {cocktails ->
                        cocktailList = cocktails
                        Log.d(TAG, "$cocktailList")
                        adapter.updateList(cocktailList)
                    })
                    binding.categoryItemsRecycler.visibility = View.VISIBLE
                }
                Status.LOADING -> {
                    binding.categoryItemsRecycler.visibility = View.GONE
                    binding.categoryItemsErrorTextView.visibility = View.GONE
                    binding.categoryItemsProgressBar.visibility = View.VISIBLE
                }
                Status.ERROR -> {
                    binding.categoryItemsRecycler.visibility = View.GONE
                    binding.categoryItemsProgressBar.visibility = View.GONE
                    binding.categoryItemsErrorTextView.visibility = View.VISIBLE
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
        if (!id.isNullOrEmpty()) {
            val intent = Intent(activity, OverviewActivity::class.java)
            intent.putExtra("cocktail_id", id)
            startActivity(intent)
        }

    }
}