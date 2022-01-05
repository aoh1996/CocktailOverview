package com.example.cocktailoverview.ui.favorite

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cocktailoverview.CocktailOverviewApplication
import com.example.cocktailoverview.data.Cocktail
import com.example.cocktailoverview.databinding.FavoritesFragmentBinding
import com.example.cocktailoverview.ui.OverviewActivity
import com.example.cocktailoverview.ui.adapters.CocktailsAdapter

class FavoritesFragment : Fragment() {


    private val viewModel: FavoritesViewModel by viewModels {
        FavoritesViewModelFactory(activity?.application as CocktailOverviewApplication)
    }

    private var _binding: FavoritesFragmentBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: CocktailsAdapter
    private lateinit var cocktailList: List<Cocktail>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FavoritesFragmentBinding.inflate(inflater, container, false)
        binding.favoritesRecycler.layoutManager = LinearLayoutManager(context)
        cocktailList = emptyList()
        adapter = CocktailsAdapter(context!!, cocktailList) {position -> onListItemClick(position)}
        binding.favoritesRecycler.adapter = adapter
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.cocktailsLiveData.observe(viewLifecycleOwner, {cocktails ->
            cocktailList = cocktails
//            Log.d(TAG, "$cocktailList")
            adapter.updateList(cocktailList)
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun onListItemClick(position: Int) {

        val id = cocktailList[position].id
        val intent = Intent(activity, OverviewActivity::class.java)
        intent.putExtra("cocktail_id", id)
        startActivity(intent)

    }

}