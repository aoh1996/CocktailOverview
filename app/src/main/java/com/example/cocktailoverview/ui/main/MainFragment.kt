package com.example.cocktailoverview.ui.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cocktailoverview.CocktailOverviewApplication
import com.example.cocktailoverview.R
import com.example.cocktailoverview.data.Cocktail
import com.example.cocktailoverview.data.Status
import com.example.cocktailoverview.databinding.MainFragmentBinding
import com.example.cocktailoverview.ui.OverviewActivity
import com.example.cocktailoverview.ui.adapters.CocktailsAdapter
import com.example.cocktailoverview.ui.adapters.SwipeToDeleteCallback

private const val TAG = "MainFragment"

class MainFragment : Fragment() {

    private val viewModel: MainViewModel by viewModels {
        MainViewModelFactory(activity?.application as CocktailOverviewApplication)
    }

    private var _binding: MainFragmentBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: CocktailsAdapter
    private lateinit var cocktailList: ArrayList<Cocktail>
    private lateinit var searchView: SearchView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        Log.d(TAG, "onCreate: ")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG, "onCreateView: ")
        _binding = MainFragmentBinding.inflate(inflater, container, false)
        binding.mainRecycler.layoutManager = LinearLayoutManager(context)
        cocktailList = ArrayList()
        adapter =
            CocktailsAdapter(context!!, cocktailList) { position -> onListItemClick(position) }
        binding.mainRecycler.adapter = adapter
        val swipeHandler = object : SwipeToDeleteCallback(context!!) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val adapter = binding.mainRecycler.adapter as CocktailsAdapter
                val position = viewHolder.bindingAdapterPosition
                val id = cocktailList[position].id
                viewModel.removeFromHistory(id)
                adapter.removeAt(position)
            }

        }
        val itemTouchHelper = ItemTouchHelper(swipeHandler)
        itemTouchHelper.attachToRecyclerView(binding.mainRecycler)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.statusLivaData.observe(viewLifecycleOwner, { status ->

            when (status!!) {
                Status.NOT_FOUND -> {
                    binding.mainRecycler.visibility = View.GONE
                    binding.mainProgressBar.visibility = View.GONE
                    binding.mainErrorTextView.visibility = View.GONE
                    binding.mainNotFoundTextView.visibility = View.VISIBLE
                }
                Status.UNDEFINED -> {
                    binding.mainRecycler.visibility = View.GONE
                    binding.mainProgressBar.visibility = View.GONE
                    binding.mainErrorTextView.visibility = View.GONE
                    binding.mainNotFoundTextView.visibility = View.GONE
                }
                Status.OK -> {
                    binding.mainErrorTextView.visibility = View.GONE
                    binding.mainProgressBar.visibility = View.GONE
                    binding.mainNotFoundTextView.visibility = View.GONE
                    viewModel.cocktailsLiveData.observe(viewLifecycleOwner, { cocktails ->
                        cocktailList = cocktails
                        Log.d(TAG, "$cocktailList")
                        adapter.updateList(cocktailList)
                    })
                    binding.mainRecycler.visibility = View.VISIBLE
                }
                Status.LOADING -> {
                    binding.mainRecycler.visibility = View.GONE
                    binding.mainErrorTextView.visibility = View.GONE
                    binding.mainNotFoundTextView.visibility = View.GONE
                    binding.mainProgressBar.visibility = View.VISIBLE
                }
                Status.ERROR -> {
                    binding.mainRecycler.visibility = View.GONE
                    binding.mainProgressBar.visibility = View.GONE
                    binding.mainNotFoundTextView.visibility = View.GONE
                    binding.mainErrorTextView.visibility = View.VISIBLE
                }
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.appbar_menu, menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)

        val searchItem = menu.findItem(R.id.app_bar_search).apply {
            setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW or MenuItem.SHOW_AS_ACTION_ALWAYS)
        }
        searchView = searchItem.actionView as SearchView
        searchView.queryHint = "Enter cocktail name..."

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String): Boolean {
                val id = viewModel.submitPressed()
                if (!id.isNullOrEmpty()) {
                    val intent = Intent(activity, OverviewActivity::class.java)
                    intent.putExtra("cocktail_id", id)
                    startActivity(intent)
                }
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {

                if (newText.isEmpty()) {
                    val size = cocktailList.size
                    cocktailList.clear()
                    adapter.notifyItemRangeRemoved(0, size)
                    viewModel.getHistory()

                } else {
                    viewModel.pendingQuery = newText
                    viewModel.makeSearch()
                }
                return true
            }
        })
    }

    private fun onListItemClick(position: Int) {

        viewModel.addToHistory(cocktailList[position])
        val id = cocktailList[position].id
        val intent = Intent(activity, OverviewActivity::class.java)
        intent.putExtra("cocktail_id", id)
        startActivity(intent)

    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart: ")
        if (viewModel.pendingQuery.isEmpty()) {
            viewModel.getHistory()
        } else {
            viewModel.makeSearch()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}