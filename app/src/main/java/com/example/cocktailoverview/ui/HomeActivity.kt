package com.example.cocktailoverview.ui

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.cocktailoverview.R
import com.example.cocktailoverview.databinding.ActivityHomeBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

private val TAG = "HomeActivity"

class HomeActivity : AppCompatActivity() {

    private lateinit var prefs: SharedPreferences
    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_home)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_main, R.id.navigation_categories, R.id.navigation_favorite
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)


        prefs = getSharedPreferences("com.example.cocktailoverview", MODE_PRIVATE)

    }

    override fun onSupportNavigateUp(): Boolean {
        return Navigation.findNavController(this, R.id.nav_host_fragment_activity_home).navigateUp()
                || super.onSupportNavigateUp()
    }

    override fun onResume() {
        super.onResume()
        if (prefs.getBoolean("firstrun", true)) {
            startActivity(Intent(this, OnboardingActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_ACTIVITY_CLEAR_TOP))
            finish()
        }
    }
}