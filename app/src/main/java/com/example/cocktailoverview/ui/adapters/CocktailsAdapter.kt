package com.example.cocktailoverview.ui.adapters

import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.CircleCropTransformation
import coil.transform.RoundedCornersTransformation
import com.example.cocktailoverview.R
import com.example.cocktailoverview.data.Cocktail
import java.lang.Exception

private const val TAG = "CocktailsAdapter"

class CocktailsAdapter(context: Context, cocktails: List<Cocktail>, private val onItemClicked: (position: Int) -> Unit) :
    RecyclerView.Adapter<CocktailsAdapter.CocktailsViewHolder>(){

    private var mList = cocktails
    private var context = context

    inner class CocktailsViewHolder(itemView: View, private val onItemClicked: (position: Int) -> Unit) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        init {
            itemView.setOnClickListener(this)
        }

        val icon: ImageView = itemView.findViewById(R.id.cocktail_icon)
        val name: TextView = itemView.findViewById(R.id.cocktail_name)
        val id: TextView = itemView.findViewById(R.id.cocktail_id)

        override fun onClick(p0: View?) {
            val position = adapterPosition
            onItemClicked(position)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CocktailsViewHolder {
        Log.d(TAG, "onCreateViewHolder: called")
        return CocktailsViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.cocktail_item, parent, false),
            onItemClicked
        )
    }

    override fun onBindViewHolder(holder: CocktailsViewHolder, position: Int) {
        Log.d(TAG, "onBindViewHolder: called")
        try {
            val currentCocktail = mList[position]
            Log.d(TAG, "current cocktail: $currentCocktail")
            val imageUri = Uri.parse(currentCocktail.thumbnailUrl)
            holder.icon.load(imageUri){
                placeholder(R.drawable.loading_animation)
                transformations(CircleCropTransformation())
                error(R.drawable.ic_baseline_broken_image_24)
            }
            holder.name.text = currentCocktail.name
            holder.id.text = String.format(context.getString(R.string.id), currentCocktail.id)
        } catch (e: Exception) {
            Log.d(TAG, "${e.message}")
        }
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    fun updateList(newData: List<Cocktail>) {
        Log.d(TAG, "updateList: called")
        mList = newData
        Log.d(TAG, "updated with:\n $mList")
        notifyDataSetChanged()
    }

}