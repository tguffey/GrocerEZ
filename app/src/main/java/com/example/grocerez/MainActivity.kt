package com.example.grocerez

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.widget.Toast
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.example.grocerez.databinding.ActivityMainBinding
import com.example.grocerez.ui.login.LoginActivity

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Check if the user is already logged in (by checking SharedPreferences)
        val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val username = sharedPreferences.getString("username", null)

        if (username != null) {
            // User is already logged in, show welcome message
            val welcomeMessage = "Welcome back, $username!"
            Toast.makeText(this, welcomeMessage, Toast.LENGTH_SHORT).show()
        } else {
            // User is not logged in, redirect to LoginActivity
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
            return  // Finish the activity to prevent further execution
        }

        // Inflate the binding after checking the user's login status
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        // I added a condition to check if the user is in dark or light mode
        val isDarkMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES

        // If the user is in dark mode, the navbar will be green001; else, it will be black
        // I added this because before it was light green even for dark mode
        if (isDarkMode) {
            // Dark mode
            navView.setBackgroundColor(resources.getColor(R.color.black))
        } else {
            // Light mode
            navView.setBackgroundColor(resources.getColor(R.color.green001))
        }

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top-level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_myplate, R.id.navigation_dashboard, R.id.navigation_home,
                R.id.navigation_recipes, R.id.navigation_shopping
            )
        )
        // THIS IS JOCELYN Im deleting this to remove Action Bar bug
        // setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }
}
