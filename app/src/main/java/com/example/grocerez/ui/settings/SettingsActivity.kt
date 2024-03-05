package com.example.grocerez.ui.settings

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatButton
import com.example.grocerez.R
import com.example.grocerez.ui.login.LoginActivity

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        // Find the back button ImageView in the layout
        val backButton: ImageView = findViewById(R.id.back_button)
        // Find the logout button in the layout
        val logoutButton: AppCompatButton = findViewById(R.id.logout_button)

        // Set a click listener for the back button
        backButton.setOnClickListener {
            // Handle back button click here
            finish()
        }
        // Set a click listener for the logout button
        logoutButton.setOnClickListener {
            // Handle logout button click here
            logoutUser()
        }
    }

    private fun logoutUser() {
        // Clear user credentials from SharedPreferences
        val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.remove("username")
        editor.remove("password")
        editor.apply()

        // Optionally, you can navigate back to the login screen or perform any other logout-related actions
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish() // Finish the SettingsActivity to prevent going back to it with the back button
    }
}