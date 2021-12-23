package com.example.cocktailoverview.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.CircleCropTransformation
import coil.transform.RoundedCornersTransformation
import com.example.cocktailoverview.OnboardingItem
import com.example.cocktailoverview.R

class OnboardingItemsAdapter(private val onboardingItems: List<OnboardingItem>) :
        RecyclerView.Adapter<OnboardingItemsAdapter.OnboardingItemsViewHolder>(){

    inner class OnboardingItemsViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val imageOnboarding = view.findViewById<ImageView>(R.id.imageOnboarding)
        val textTitle = view.findViewById<TextView>(R.id.textTitle)
        val textDescription = view.findViewById<TextView>(R.id.textDescription)

        fun bind (onboardingItem: OnboardingItem) {
            imageOnboarding.load(onboardingItem.onboardingImage) {
                transformations(CircleCropTransformation())
            }
            textTitle.text = onboardingItem.title
            textDescription.text = onboardingItem.description
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OnboardingItemsViewHolder {
        return OnboardingItemsViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.onboarding_item_container,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: OnboardingItemsViewHolder, position: Int) {
        holder.bind(onboardingItems[position])
    }

    override fun getItemCount(): Int {
        return onboardingItems.size
    }
}