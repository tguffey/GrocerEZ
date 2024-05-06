package com.example.grocerez.ui.register

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.anychart.data.View
import com.example.grocerez.R
import com.example.grocerez.SocketHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// RegisterStep2Fragment.kt

class RegisterStep2Fragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): android.view.View? {
        val view = inflater.inflate(R.layout.fragment_register_step2, container, false)

        // THONG: retrieve these 2 from step1 fragment. assign default value blank.
        val email = arguments?.getString("email", "")
        val password = arguments?.getString("password", "")

        // singleton socket
        val mSocket = SocketHandler.getSocket()

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

            // THONG: we have all 3 required fields here

            if (areConditionsMet()) {


                val username = usernameEditText?.text.toString()

                mSocket.emit("register_username_check", usernameEditText.text.toString())
                mSocket.on("username_check_duplicate_detected"){args ->
                    println("dupe username socket received")

                    // this one runs on main thread to update ui and display a toast
                    Handler(Looper.getMainLooper()).post {
                        Toast.makeText(requireContext(), "a username with the name already exists", Toast.LENGTH_SHORT).show()
                    }
                }

                mSocket.on("username_check_success"){args ->

                    println("username is unique")
                    // this one runs on main thread to update ui and display a toast
                    Handler(Looper.getMainLooper()).post {
                        Toast.makeText(requireContext(), "username is unique!", Toast.LENGTH_SHORT).show()
                    }

                    mSocket.emit("save_signup_info", usernameEditText?.text.toString(), email.toString(), password.toString())
                    (activity as? RegisterActivity)?.finishRegistration(usernameEditText?.text.toString())
                }


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

