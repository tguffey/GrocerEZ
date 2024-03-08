package com.example.grocerez.ui.welcome

import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.AppCompatButton
import com.example.grocerez.R
import com.example.grocerez.ui.login.LoginActivity

class WelcomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        // Make the status bar transparent
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = Color.TRANSPARENT
        }

        val loginButton = findViewById<AppCompatButton>(R.id.welcome_login_btn)

        // Set OnClickListener for the Login button
        loginButton.setOnClickListener {
            // Launch LoginActivity when the button is clicked
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}