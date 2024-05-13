package com.example.grocerez.ui.settings

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.AppCompatButton
import com.example.grocerez.R
import android.view.LayoutInflater
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import com.example.grocerez.ui.login.LoginActivity
import com.example.grocerez.ui.welcome.WelcomeActivity
import androidx.appcompat.widget.SwitchCompat
import com.anychart.data.View

class SettingsActivity : AppCompatActivity() {
    object CustomToast {
        fun showToast(context: Context, message: String) {
            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val layout = inflater.inflate(R.layout.toast_layout, null)

            val toastTextView: TextView = layout.findViewById(R.id.custom_toast_message)
            toastTextView.text = message

            val toast = Toast(context)
            toast.duration = Toast.LENGTH_SHORT
            toast.view = layout
            toast.show()
        }
    }
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        // Find the RelativeLayout for the "About Us" section
        val aboutUsLayout: RelativeLayout = findViewById(R.id.aboutUsLayout)

        // Set OnClickListener for the "About Us" section
        aboutUsLayout.setOnClickListener {
            // Launch the AboutUsActivity when clicked
            startActivity(Intent(this, AboutUsActivity::class.java))
        }

        // Find the RelativeLayout for the "About Us" section
        val faqLayout: RelativeLayout = findViewById(R.id.faqLayout)

        faqLayout.setOnClickListener {
            // Launch the AboutUsActivity when clicked
            startActivity(Intent(this, FaqActivity::class.java))
        }

        // Find the back button ImageView in the layout
        val backButton: ImageView = findViewById(R.id.back_button)
        // Find the logout button in the layout
        val logoutButton: AppCompatButton = findViewById(R.id.logout_button)
        // Find the notifications switch
        val notificationsSwitch: SwitchCompat = findViewById(R.id.notifications_switch)

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

        notificationsSwitch.setOnCheckedChangeListener { _, isChecked ->
            // Show a custom Toast message only when interacting with the notifications switch
            if (isChecked) {
                CustomToast.showToast(this, "Notifications Enabled")
            } else {
                CustomToast.showToast(this, "Notifications Disabled")
            }
        }

        val sharedPreferences = getSharedPreferences("theme_prefs", Context.MODE_PRIVATE)

        // Get the saved theme preference, default to MODE_NIGHT_FOLLOW_SYSTEM if not found
        val isDarkModeEnabled = sharedPreferences.getBoolean(
            "isDarkModeEnabled",
            AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES
        )

        val nightModeSwitch: SwitchCompat = findViewById(R.id.nightModeSwitch)
        nightModeSwitch.isChecked = isDarkModeEnabled // Set initial state of the switch

        // Set default night mode if dark mode is enabled initially
        if (isDarkModeEnabled) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }

        // Inside your SettingsActivity
        nightModeSwitch.setOnCheckedChangeListener { _, isChecked ->
            val currentMode = AppCompatDelegate.getDefaultNightMode()
            val newMode =
                if (isChecked) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO

            // Check if the current mode is different from the new mode
            if (currentMode != newMode) {
                // Night mode state has changed, apply the new mode
                AppCompatDelegate.setDefaultNightMode(newMode)
                // Recreate the activity to apply the new theme
                recreate()

                // Save the new theme preference into shared preferences
                sharedPreferences.edit().putBoolean("isDarkModeEnabled", isChecked).apply()
            }
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
        val intent = Intent(this, WelcomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish() // Finish the SettingsActivity to prevent going back to it with the back button
    }

    fun onSendMessageClicked(view: android.view.View) {
        // Create an Intent with ACTION_SEND action
        val emailIntent = Intent(Intent.ACTION_SEND).apply {
            // Set the type to "message/rfc822" to ensure it opens email apps
            type = "message/rfc822"
            // Set the recipient email address
            putExtra(Intent.EXTRA_EMAIL, arrayOf("muke.exe@gmail.com"))
            // Set the subject line
            putExtra(Intent.EXTRA_SUBJECT, "GrocerEZ Reach Out(User)")
            // You can also include the email body if needed
            putExtra(Intent.EXTRA_TEXT, "Please include: username")
        }

        // Create an Intent to show the app chooser dialog
        val chooserIntent = Intent.createChooser(emailIntent, "Send email via")

        // Verify that there is an app to handle the intent
        if (chooserIntent.resolveActivity(packageManager) != null) {
            // Start the activity with the app chooser dialog
            startActivity(chooserIntent)
        } else {
            // Handle the case where no email app is available
            CustomToast.showToast(this, "No message apps found")
        }
    }

}