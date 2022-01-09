package com.example.cocktailoverview.ui.categories

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cocktailoverview.data.Cocktail
import com.example.cocktailoverview.databinding.CategoriesFragmentBinding
import com.example.cocktailoverview.data.Status
import com.example.cocktailoverview.ui.adapters.CategoriesAdapter

private const val TAG = "CategoriesFrag"
class CategoriesFragment : Fragment() {

    private val viewModel: CategoriesViewModel by viewModels {
        CategoriesViewModelFactory()
    }

    private var _binding: CategoriesFragmentBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: CategoriesAdapter
    private lateinit var cocktailList: List<Cocktail>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = CategoriesFragmentBinding.inflate(inflater, container, false)
        binding.categoriesRecycler.layoutManager = LinearLayoutManager(context)
        cocktailList = emptyList()
        adapter = CategoriesAdapter(cocktailList) {position -> onListItemClick(position)}
        binding.categoriesRecycler.adapter = adapter
//        binding.categoriesRecycler.setHasFixedSize(true)

        return binding.root
    }

    private fun onListItemClick(position: Int) {
        Log.d(TAG, "onListItemClick: ${cocktailList[position].category}")

        val category = cocktailList[position].category
        val action = CategoriesFragmentDirections.actionNavigationCategoriesToNavigationCategoryItems(category)
        findNavController().navigate(action)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.statusLivaData.observe(viewLifecycleOwner, { status ->
            Log.d(TAG, "categories load status: $status")
            when(status!!) {
                Status.UNDEFINED -> {
                    binding.categoriesRecycler.visibility = View.GONE
                    binding.categoriesProgressBar.visibility = View.GONE
                    binding.categoriesErrorTextView.visibility = View.GONE
                }
                Status.OK -> {
                    binding.categoriesErrorTextView.visibility = View.GONE
                    binding.categoriesProgressBar.visibility = View.GONE
                    viewModel.categoriesLiveData.observe(viewLifecycleOwner, {cocktails ->
                        cocktailList = cocktails
                        adapter.updateList(cocktailList)
                    })
                    binding.categoriesRecycler.visibility = View.VISIBLE
                }
                Status.LOADING -> {
                    binding.categoriesRecycler.visibility = View.GONE
                    binding.categoriesErrorTextView.visibility = View.GONE
                    binding.categoriesProgressBar.visibility = View.VISIBLE
                }
                Status.ERROR -> {
                    binding.categoriesRecycler.visibility = View.GONE
                    binding.categoriesProgressBar.visibility = View.GONE
                    binding.categoriesErrorTextView.visibility = View.VISIBLE
                }
            }
        })
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}