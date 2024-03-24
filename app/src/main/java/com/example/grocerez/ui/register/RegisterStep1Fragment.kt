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

        // Set up TextWatchers for the EditText fields
        emailEditText.addTextChangedListener(textWatcher)
        passwordEditText.addTextChangedListener(textWatcher)
        confirmEditText.addTextChangedListener(textWatcher)

        val nextButton = view.findViewById<Button>(R.id.register_next_btn)
        nextButton.isEnabled = false  // Initially disable the next button until passwords match

        nextButton.setOnClickListener {
            // Check if the conditions are met before navigating to the next step
            if (areConditionsMet()) {
                (activity as? RegisterActivity)?.navigateToStep2()
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

        // Check conditions for enabling/disabling the next button
        val areConditionsMet = emailEditText?.text?.isNotEmpty() == true &&
                passwordEditText?.text?.isNotEmpty() == true &&
                confirmEditText?.text?.isNotEmpty() == true &&
                passwordEditText?.text.toString() == confirmEditText?.text.toString()

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

