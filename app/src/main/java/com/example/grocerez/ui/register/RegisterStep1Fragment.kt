package com.example.grocerez.ui.register

import androidx.fragment.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import com.example.grocerez.R
import com.example.grocerez.SocketHandler
import java.util.regex.Pattern

// RegisterStep1Fragment.kt
class RegisterStep1Fragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_register_step1, container, false)

        val emailEditText = view.findViewById<EditText>(R.id.register_email_entry)
        val passwordEditText = view.findViewById<EditText>(R.id.register_pswd_entry)
        val confirmEditText = view.findViewById<EditText>(R.id.register_confirm_pswd_entry)

        val mSocket = SocketHandler.getSocket()
        mSocket.emit("hello") // test to see if it works on the server


        // Set up TextWatchers for the EditText fields
        emailEditText.addTextChangedListener(textWatcher)
        passwordEditText.addTextChangedListener(textWatcher)
        confirmEditText.addTextChangedListener(textWatcher)

        val nextButton = view.findViewById<Button>(R.id.register_next_btn)
        nextButton.isEnabled = false  // Initially disable the next button

        nextButton.setOnClickListener {
            // Check if the conditions are met before navigating to the next step


            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()


            // check to see if conditions: ea
            if (areConditionsMet()) {
                (activity as? RegisterActivity)?.navigateToStep2(email, password)
            }
        }

        return view
    }

    private val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            // Call the method to check conditions and enable/disable the next button
            checkConditionsAndUpdateButtonState()
        }

        override fun afterTextChanged(s: Editable?) {}
    }

    private fun checkConditionsAndUpdateButtonState() {
        // Get references to the EditText fields
        val emailEditText = view?.findViewById<EditText>(R.id.register_email_entry)
        val passwordEditText = view?.findViewById<EditText>(R.id.register_pswd_entry)
        val confirmEditText = view?.findViewById<EditText>(R.id.register_confirm_pswd_entry)

        // REGEX to validate email with the form of < 1 characters>@<1 characters>.< 2-5 characters>
        val EMAIL_PATTERN = Pattern.compile(
            "^([a-zA-Z0-9_\\-\\.]+)@([a-zA-Z0-9_\\-\\.]+)\\.([a-zA-Z]{2,5})$"
        )
        val isEmailValid = EMAIL_PATTERN.matcher(emailEditText?.text.toString()).matches()

        // checks to see if paswword entered is greater than 8 characters
        val isPasswordValid = passwordEditText?.text?.length ?: 0 > 8
        // val arePasswordsMatching = passwordEditText?.text.toString() == confirmEditText?.text.toString()


        // Check conditions for enabling/disabling the next button
        val areConditionsMet = emailEditText?.text?.isNotEmpty() == true &&
                passwordEditText?.text?.isNotEmpty() == true &&
                confirmEditText?.text?.isNotEmpty() == true &&
                passwordEditText?.text.toString() == confirmEditText?.text.toString() &&
                isPasswordValid && isEmailValid

        // Enable/disable the next button based on conditions
        val nextButton = view?.findViewById<Button>(R.id.register_next_btn)
        nextButton?.isEnabled = areConditionsMet
    }

    private fun areConditionsMet(): Boolean {
        // You can add more conditions if needed
        // For example, check the validity of the email, password, etc.
        return true
    }
}

