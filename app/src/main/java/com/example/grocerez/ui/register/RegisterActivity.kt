package com.example.grocerez.ui.register
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.grocerez.MainActivity
import com.example.grocerez.R

/**
 * I need to implement logic for valid username, password, email, i shall do
 * that later. i just wanted to create the ui.
 */

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Initially load the first fragment
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragmentContainer, RegisterStep1Fragment())
        fragmentTransaction.commit()
    }

    // Function to navigate to the second fragment
    // Thong: we need to pass email and password into step 2,
    // because i do need it all in one place to send at once to backend.
    fun navigateToStep2(email: String, password: String) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()

        // Thong: adding field passing of email and password to step 2 frag
        val fragment = RegisterStep2Fragment().apply {
            arguments = Bundle().apply {
                putString("email", email)
                putString("password", password)
            }
        }

        // THONG: replace RegisterStep2Fragment() with fragment, with appliance of new arguments
        fragmentTransaction.replace(R.id.fragmentContainer, fragment)
        // we will retrieve this in step2 fragemnt

        fragmentTransaction.addToBackStack(null) // Optional: Add to back stack
        fragmentTransaction.commit()
    }

    // Function to finish registration and navigate to MainActivity
    // Function to finish registration and navigate to MainActivity
    // Thong: gonna add email and password to this.
    fun finishRegistration(username: String) {
        // Store the username in SharedPreferences

        val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("username", username)
        editor.apply()

        // Navigate to MainActivity
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

}