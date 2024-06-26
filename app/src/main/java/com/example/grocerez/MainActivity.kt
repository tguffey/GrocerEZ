package com.example.grocerez

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.widget.Button
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.example.grocerez.database.AppDatabase
import com.example.grocerez.databaseActivities.DatabaseActivity
import com.example.grocerez.databinding.ActivityMainBinding
import com.example.grocerez.ui.welcome.WelcomeActivity

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var appDatabase: AppDatabase


    override fun onCreate(savedInstanceState: Bundle?) {
//        // Retrieve the theme preference from shared preferences
//        val themePreferences = getSharedPreferences("theme_prefs", Context.MODE_PRIVATE)
//        val isDarkModeEnabled = themePreferences.getBoolean("isDarkModeEnabled", false)
//
//        // Set the theme based on the saved preference
//        if (isDarkModeEnabled) {
//            setTheme(R.style.Theme_GrocerEZDark)
//        } else {
//            setTheme(R.style.Theme_GrocerEZ)
//        }

        super.onCreate(savedInstanceState)

        // Thong: Initialize the AppDatabase instance. here goes nothing
        appDatabase = AppDatabase.getInstance(applicationContext)
        //WORKS!!!!!!! NO ERRORRR!!!!


        // ESTABLISH SOCKET CONNECTION
//        SocketHandler.setSocket()
        val mSocket = SocketHandler.getSocket()
//        mSocket.connect()
        mSocket.emit("hello")
        // ______________________________

        // Check if the user is already logged in (by checking SharedPreferences)
        val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val username = sharedPreferences.getString("username", null)
        // TODO: sharedpreferences for email and password

        if (username != null) {
            // If username is logged in, do nothing
        } else {
            // User is not logged in, redirect to Welcome screen
            val intent = Intent(this, WelcomeActivity::class.java)
            startActivity(intent)
            finish()
            return  // Finish the activity to prevent further execution
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView
//
//        // I added a condition to check if the user is in dark or light mode
//        val isDarkMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
//
//        // If the user is in dark mode, the navbar will be green001; else, it will be black
//        // I added this because before it was light green even for dark mode
//        if (isDarkMode) {
//            // Dark mode
//            navView.setBackgroundColor(resources.getColor(R.color.black))
//        } else {
//            // Light mode
//            navView.setBackgroundColor(resources.getColor(R.color.green001))
//        }

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top-level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_myplate, R.id.navigation_dashboard, R.id.navigation_home,
                R.id.navigation_recipes, R.id.navigation_shopping
            )
        )

        // THONG: im gonna createa a button to test out the funcitons of the database
        val testDB_btn = findViewById<Button>(R.id.testDB_btn)

        testDB_btn.setOnClickListener {
            val intent = Intent(this, DatabaseActivity::class.java)
            startActivity(intent)
            finish()
        }


        // THIS IS JOCELYN Im deleting this to remove Action Bar bug
        // setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }
}
