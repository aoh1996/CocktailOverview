package com.example.cocktailoverview.ui.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.cocktailoverview.R
import com.example.cocktailoverview.data.Cocktail
import java.lang.Exception

private const val TAG = "CategorAdapter"

class CategoriesAdapter(cocktails: List<Cocktail>, private val onItemClicked: (position: Int) -> Unit) :
    RecyclerView.Adapter<CategoriesAdapter.CategoriesViewHolder>() {

    private var mList = cocktails

    inner class CategoriesViewHolder(itemView: View, private val onItemClicked: (position: Int) -> Unit) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        init {
            itemView.setOnClickListener(this)
        }

        val textView: TextView = itemView.findViewById(R.id.categoryTextView)

        override fun onClick(p0: View?) {
            val position = adapterPosition
            onItemClicked(position)
        }
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoriesViewHolder {
        return CategoriesViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.category_item, parent, false), onItemClicked
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