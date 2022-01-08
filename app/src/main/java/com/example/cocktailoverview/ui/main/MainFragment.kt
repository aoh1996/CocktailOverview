package com.example.cocktailoverview.ui.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cocktailoverview.R
import com.example.cocktailoverview.data.Cocktail
import com.example.cocktailoverview.data.Status
import com.example.cocktailoverview.databinding.MainFragmentBinding
import com.example.cocktailoverview.ui.OverviewActivity
import com.example.cocktailoverview.ui.adapters.CocktailsAdapter

private const val TAG = "MainFragment"

class MainFragment : Fragment() {

    private val viewModel: MainViewModel by viewModels {
        MainViewModelFactory()
    }

    private var _binding: MainFragmentBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: CocktailsAdapter
    private lateinit var cocktailList: ArrayList<Cocktail>
    private lateinit var searchView: SearchView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = MainFragmentBinding.inflate(inflater, container, false)
        binding.mainRecycler.layoutManager = LinearLayoutManager(context)
        cocktailList = ArrayList()
        adapter = CocktailsAdapter(context!!, cocktailList) {position -> onListItemClick(position)}
        binding.mainRecycler.adapter = adapter
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.statusLivaData.observe(viewLifecycleOwner, {status ->

            when(status!!) {
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
                    viewModel.cocktailsLiveData.observe(viewLifecycleOwner, {cocktails ->
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
        menu.clear()
        inflater?.inflate(R.menu.appbar_menu, menu)

        val searchItem = menu.findItem(R.id.app_bar_search).apply {
            setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW or MenuItem.SHOW_AS_ACTION_IF_ROOM)
        }
        searchView = searchItem.actionView as SearchView
        searchView.queryHint = "Enter cocktail name..."

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                val id = viewModel.submitPressed(query)
                if (!id.isNullOrEmpty()) {
                    val intent = Intent(activity, OverviewActivity::class.java)
                    intent.putExtra("cocktail_id", id)
                    startActivity(intent)
                }
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                Log.d(TAG, "onQueryTextChange: called")
                if (newText.isEmpty()) {
                    val size = cocktailList.size
                    cocktailList.clear()
                    adapter.notifyItemRangeRemoved(0, size)
                } else viewModel.textChanged(newText)
                return false
            }
        })
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


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}