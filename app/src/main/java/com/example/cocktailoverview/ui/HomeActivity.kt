package com.example.cocktailoverview.ui

import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import com.example.cocktailoverview.MainActivity
import com.example.cocktailoverview.R

private val TAG = "HomeActivity"

class HomeActivity : AppCompatActivity() {

    private lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        prefs = getSharedPreferences("com.example.cocktailoverview", MODE_PRIVATE)

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

    override fun onResume() {
        super.onResume()
        if (prefs.getBoolean("firstrun", true)) {
            startActivity(Intent(this, MainActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_ACTIVITY_CLEAR_TOP))
            prefs.edit().putBoolean("firstrun", false).apply()
            finish()
        }
    }
}