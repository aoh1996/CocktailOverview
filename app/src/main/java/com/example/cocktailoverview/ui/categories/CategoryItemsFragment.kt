package com.example.cocktailoverview.ui.categories

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cocktailoverview.CocktailOverviewApplication
import com.example.cocktailoverview.R
import com.example.cocktailoverview.data.Cocktail
import com.example.cocktailoverview.data.Repository
import com.example.cocktailoverview.data.Status
import com.example.cocktailoverview.databinding.FragmentCategoryItemsBinding
import com.example.cocktailoverview.ui.adapters.CocktailsAdapter
import com.example.cocktailoverview.ui.overview.OverviewActivity
import com.faltenreich.skeletonlayout.Skeleton
import com.faltenreich.skeletonlayout.applySkeleton
import com.google.android.material.divider.MaterialDividerItemDecoration


private const val TAG = "CatItemsFrag"

class CategoryItemsFragment : Fragment() {

    private val viewModel: CategoryItemsViewModel by viewModels {
        CategoryItemsViewModelFactory(repository)
    }

    private val args: CategoryItemsFragmentArgs by navArgs()
    private var _binding: FragmentCategoryItemsBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: CocktailsAdapter
    private lateinit var cocktailList: ArrayList<Cocktail>
    private var category: String? = null
    private lateinit var skeleton: Skeleton
    private lateinit var divider: MaterialDividerItemDecoration
    private lateinit var repository: Repository
    private var scale: Float = 0f
    private var dividerInsetStartPx: Int = 0


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCategoryItemsBinding.inflate(inflater, container, false)
        init()

        return binding.root
    }

    override fun onPause() {
        Log.d(TAG, "onPause")
        super.onPause()
    }

    override fun onStop() {
        Log.d(TAG, "onStop")
        super.onStop()
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy: ")
        super.onDestroy()
    }

    override fun onDetach() {
        Log.d(TAG, "onDetach: ")
        super.onDetach()
    }

    private fun init() {
        binding.categoryItemsRecycler.layoutManager = LinearLayoutManager(context)
        cocktailList = ArrayList()

        repository = Repository.getRepository(activity?.application as CocktailOverviewApplication)

        // adapter setup
        adapter = CocktailsAdapter(requireContext(), cocktailList) {position -> onListItemClick(position)}

        // divider setup
        divider = MaterialDividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL)
        scale = resources.displayMetrics.density
        dividerInsetStartPx = (85 * scale + 0.5f).toInt()
        divider.dividerInsetStart = dividerInsetStartPx
        divider.dividerThickness = 1

        // recycler setup
        binding.categoryItemsRecycler.addItemDecoration(divider)
        binding.categoryItemsRecycler.adapter = adapter

        // skeleton setup
        skeleton = binding.categoryItemsRecycler.applySkeleton(R.layout.cocktail_item, 7)
        skeleton.maskCornerRadius = 80.0f
        skeleton.maskColor = ContextCompat.getColor(requireContext(), R.color.lightGray)
        skeleton.shimmerColor = ContextCompat.getColor(requireContext(), R.color.gray)
        skeleton.showShimmer = true
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        category = args.category
        category?.let { viewModel.fetchCategoryItems(it) }
        viewModel.statusLivaData.observe(viewLifecycleOwner) { status ->
            Log.d(TAG, "categories load status: $status")
            when (status!!) {
                Status.UNDEFINED -> {
                    binding.skeletonLayout.visibility = View.GONE
                }
                Status.OK -> {
                    viewModel.categoryItemsLiveData.observe(viewLifecycleOwner) { cocktails ->
                        cocktailList = cocktails
                        Log.d(TAG, "$cocktailList")
                        adapter.updateList(cocktailList)
                    }
                    skeleton.showOriginal()
                }
                Status.LOADING -> {
                    skeleton.showSkeleton()
                }
                Status.ERROR -> {
                    binding.skeletonLayout.visibility = View.GONE
                }
            }
        }


    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d(TAG, "onDestroyView")
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