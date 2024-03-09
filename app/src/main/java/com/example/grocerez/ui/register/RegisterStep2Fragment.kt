package com.example.grocerez.ui.register

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.anychart.data.View
import com.example.grocerez.R

// RegisterStep2Fragment.kt

class RegisterStep2Fragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): android.view.View? {
        val view = inflater.inflate(R.layout.fragment_register_step2, container, false)

        // Get references to the EditText fields in Step 2
        val firstNameEditText = view.findViewById<EditText>(R.id.register_firstn_entry)
        val lastNameEditText = view.findViewById<EditText>(R.id.register_lastn_entry)
        val usernameEditText = view.findViewById<EditText>(R.id.register_username_entry)

        // Set up TextWatchers for the EditText fields
        firstNameEditText.addTextChangedListener(textWatcher)
        lastNameEditText.addTextChangedListener(textWatcher)
        usernameEditText.addTextChangedListener(textWatcher)

        val finishButton = view.findViewById<Button>(R.id.register_finish_btn)
        finishButton.isEnabled = false  // Initially disable the finish button

        finishButton.setOnClickListener {
            // Check if the conditions are met before finishing registration
            if (areConditionsMet()) {
                val username = usernameEditText?.text.toString()
                (activity as? RegisterActivity)?.finishRegistration(username)
            }
        }


        return view
    }

    private val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            // Call the method to check conditions and enable/disable the finish button
            checkConditionsAndUpdateButtonState()
        }

        override fun afterTextChanged(s: Editable?) {}
    }

    private fun checkConditionsAndUpdateButtonState() {
        // Get references to the EditText fields in Step 2
        val firstNameEditText = view?.findViewById<EditText>(R.id.register_firstn_entry)
        val lastNameEditText = view?.findViewById<EditText>(R.id.register_lastn_entry)
        val usernameEditText = view?.findViewById<EditText>(R.id.register_username_entry)

        // Check conditions for enabling/disabling the finish button
        val areConditionsMet = firstNameEditText?.text?.isNotEmpty() == true &&
                lastNameEditText?.text?.isNotEmpty() == true &&
                usernameEditText?.text?.isNotEmpty() == true

        // Enable/disable the finish button based on conditions
        val finishButton = view?.findViewById<Button>(R.id.register_finish_btn)
        finishButton?.isEnabled = areConditionsMet
    }

    private fun areConditionsMet(): Boolean {
        // You can add more conditions if needed
        // For example, check the validity of first name, last name, username, etc.
        return true
    }
}

