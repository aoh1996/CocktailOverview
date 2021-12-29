package com.example.cocktailoverview.ui.categories

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.cocktailoverview.R
import com.example.cocktailoverview.databinding.CategoriesFragmentBinding
import com.example.cocktailoverview.network.Status

class CategoriesFragment : Fragment() {

    private val viewModel by viewModels<CategoriesViewModel>()

    private var _binding: CategoriesFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = CategoriesFragmentBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.statusLivaData.observe(viewLifecycleOwner, {status ->
            when(status!!) {
                Status.OK -> {
                    viewModel.categoriesLiveData.observe(viewLifecycleOwner, {categories ->
                        for (c in categories) {
                            binding.categoriesTextView.append("$c\n")
                        }
                    })
                }
            }
        })
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}