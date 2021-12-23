package com.example.cocktailoverview

import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.WindowCompat
import androidx.core.view.size
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.cocktailoverview.databinding.ActivityMainBinding
import com.example.cocktailoverview.ui.HomeActivity
import com.example.cocktailoverview.ui.OnboardingItemsAdapter
import com.google.android.material.button.MaterialButton
import com.tbuonomo.viewpagerdotsindicator.WormDotsIndicator
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"

    private lateinit var onboardingItemsAdapter: OnboardingItemsAdapter
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewPager: ViewPager2
    private lateinit var buttonGetStarted: MaterialButton
    private lateinit var wormDotsIndicator: WormDotsIndicator
    private lateinit var imageNext: ImageView
    private lateinit var skipTextView: TextView



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)




        setFullScreen()
        bind()
        setOnboardingItems()

    }

    override fun onBackPressed() {
//        finish()
        finish()
        exitProcess(0)
//        super.onBackPressed()
    }

    private fun setFullScreen() {
        this.window.apply {
            clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)

            when (context.resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK)) {
                Configuration.UI_MODE_NIGHT_YES -> {
                    decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                }
                Configuration.UI_MODE_NIGHT_NO -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                    }
                }
                Configuration.UI_MODE_NIGHT_UNDEFINED -> {
                    decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                }
            }


            statusBarColor = Color.TRANSPARENT
        }
    }

    private fun bind() {
        viewPager = binding.onboardingViewPager
        buttonGetStarted = binding.buttonGetStarted
        wormDotsIndicator = binding.wormDotsIndicator
        imageNext = binding.imageNext
        skipTextView = binding.skipTextView

    }

    private fun startHomeActivity() {
        startActivity(Intent(this, HomeActivity::class.java))
    }

    fun setOnboardingItems() {
        onboardingItemsAdapter = OnboardingItemsAdapter(
            listOf(
                OnboardingItem(
                    R.drawable.onboarding_image_1,
                    "Search",
                    ""
                ),
                OnboardingItem(
                    R.drawable.onboarding_image_2,
                    "Mix",
                    ""
                ),
                OnboardingItem(
                    R.drawable.onboarding_image_3,
                    "Have fun!",
                    ""
                )
            )
        )

        viewPager.adapter = onboardingItemsAdapter
        wormDotsIndicator.setViewPager2(viewPager)
        (viewPager.getChildAt(0) as RecyclerView).overScrollMode =
            RecyclerView.OVER_SCROLL_NEVER

        viewPager.registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                if (position + 1 == onboardingItemsAdapter.itemCount) {
                    buttonGetStarted.isEnabled = true
                }
            }
        })

        imageNext.setOnClickListener {
            if (viewPager.currentItem + 1 < onboardingItemsAdapter.itemCount) {
                viewPager.currentItem += 1
            } else {
                startHomeActivity()
            }
        }

        buttonGetStarted.setOnClickListener {
            startHomeActivity()
        }

        skipTextView.setOnClickListener {
            startHomeActivity()
        }
    }
}