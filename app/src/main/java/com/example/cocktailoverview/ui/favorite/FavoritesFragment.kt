package com.example.cocktailoverview.ui.favorite

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cocktailoverview.CocktailOverviewApplication
import com.example.cocktailoverview.data.Cocktail
import com.example.cocktailoverview.databinding.FavoritesFragmentBinding
import com.example.cocktailoverview.ui.OverviewActivity
import com.example.cocktailoverview.ui.adapters.CocktailsAdapter
import com.example.cocktailoverview.ui.adapters.SwipeToDeleteCallback
import com.google.android.material.divider.MaterialDividerItemDecoration

private const val TAG = "FavFrag"

class FavoritesFragment : Fragment() {


    private val viewModel: FavoritesViewModel by viewModels {
        FavoritesViewModelFactory(activity?.application as CocktailOverviewApplication)
    }

    private var _binding: FavoritesFragmentBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: CocktailsAdapter
    private lateinit var cocktailList: ArrayList<Cocktail>
    private lateinit var divider: MaterialDividerItemDecoration
    private var scale: Float = 0f
    private var dividerInsetStartPx: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FavoritesFragmentBinding.inflate(inflater, container, false)
        init()
        return binding.root
    }

    private fun init() {
        binding.favoritesRecycler.layoutManager = LinearLayoutManager(context)
        cocktailList = arrayListOf()

        //adapter setup
        adapter = CocktailsAdapter(context!!, cocktailList) {position -> onListItemClick(position)}

        //divider setup
        divider = MaterialDividerItemDecoration(context!!, LinearLayoutManager.VERTICAL)
        scale = resources.displayMetrics.density
        dividerInsetStartPx = (85 * scale + 0.5f).toInt()
        divider.dividerInsetStart = dividerInsetStartPx
        divider.dividerThickness = 1

        // recycler setup
        binding.favoritesRecycler.addItemDecoration(divider)
        binding.favoritesRecycler.adapter = adapter

        // swipe to delete
        val swipeHandler = object : SwipeToDeleteCallback(context!!) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val adapter = binding.favoritesRecycler.adapter as CocktailsAdapter
                val position = viewHolder.bindingAdapterPosition
                val id = cocktailList[position].id
                viewModel.removeFromFavorites(id)
                adapter.removeAt(position)
            }

        }
        val itemTouchHelper = ItemTouchHelper(swipeHandler)
        itemTouchHelper.attachToRecyclerView(binding.favoritesRecycler)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.cocktailsLiveData.observe(viewLifecycleOwner, {cocktails ->
            cocktailList = cocktails
            adapter.updateList(cocktailList)
            Log.d(TAG, "onViewCreated: $cocktailList")
        })
    }

    override fun onResume() {
        super.onResume()
        viewModel.getFavorites()
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