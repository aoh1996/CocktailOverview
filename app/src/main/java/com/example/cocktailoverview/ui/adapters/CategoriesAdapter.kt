package com.example.cocktailoverview.ui.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.cocktailoverview.R
import com.example.cocktailoverview.data.Cocktail
import com.example.cocktailoverview.databinding.CategoryItemBinding
import java.lang.Exception

private const val TAG = "CategorAdapter"

class CategoriesAdapter(private var cocktails: List<Cocktail>) :
    RecyclerView.Adapter<CategoriesAdapter.CategoriesViewHolder>() {

    var mList = cocktails

    inner class CategoriesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        fun bind(cocktail: Cocktail) {
//            binding.categoryTextView.text = cocktail.category
//        }
        val textView: TextView = itemView.findViewById(R.id.categoryTextView)
    }

//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoriesViewHolder {
//        return CategoriesViewHolder(CategoryItemBinding.inflate(LayoutInflater.from(parent.context)))
//    }
//
//    override fun onBindViewHolder(holder: CategoriesViewHolder, position: Int) {
//        holder.bind(cocktails[position])
//    }

    override fun getItemCount(): Int {
        return mList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoriesViewHolder {
        return CategoriesViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.category_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: CategoriesViewHolder, position: Int) {

        try {
            val currentCocktail = mList[position]
            holder.textView.text = currentCocktail.category
        } catch (e: Exception) {
            Log.d(TAG, "${e.message}")
        }

    }

    fun updateList(newData: List<Cocktail>) {
        mList = newData
        notifyDataSetChanged()
    }
}