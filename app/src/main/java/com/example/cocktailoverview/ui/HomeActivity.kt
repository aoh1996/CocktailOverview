package com.example.cocktailoverview.ui

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.cocktailoverview.R

private val TAG = "HomeActivity"

class HomeActivity : AppCompatActivity() {

    private lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        prefs = getSharedPreferences("com.example.cocktailoverview", MODE_PRIVATE)

    }


    override fun onResume() {
        super.onResume()
        if (prefs.getBoolean("firstrun", true)) {
            startActivity(Intent(this, OnboardingActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_ACTIVITY_CLEAR_TOP))
            finish()
        } else {
            startActivity(Intent(this, OverviewActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_ACTIVITY_CLEAR_TOP))
            finish()
        }
    }
}