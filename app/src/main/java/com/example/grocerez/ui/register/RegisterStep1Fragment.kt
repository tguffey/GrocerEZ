package com.example.grocerez.ui.register

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.grocerez.R
import com.example.grocerez.SocketHandler
//import io.socket.client.Socket
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
        val warningTextView = view.findViewById<TextView>(R.id.registerWarningTextview)
        var isEmailUnique = false
        // getting socket connection
        val mSocket = SocketHandler.getSocket()
        mSocket.emit("hello") // test to see if it works on the server


        // Set up TextWatchers for the EditText fields
        emailEditText.addTextChangedListener(textWatcher)
        passwordEditText.addTextChangedListener(textWatcher)
        confirmEditText.addTextChangedListener(textWatcher)

        val nextButton = view.findViewById<Button>(R.id.register_next_btn)
        nextButton.isEnabled = false  // Initially disable the next button

        println("trying to receive socket 1")
        println("trying to receive socket 2")


//        mSocket.on("email_check_success"){args ->
//            println("sucess ocket received")
//            if (args[0] != null) {
//                println("printing a message, email is good, socket received")
//                val message = args[0] as String
//                isEmailUnique = true
//                warningTextView.text = message
//                CoroutineScope(IO).launch {
//                    withContext(Dispatchers.Main){
//                        warningTextView.text = message
//
//                    }
//                }
//            }
//        }


        nextButton.setOnClickListener {
            // Check if the conditions are met before navigating to the next step


            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            mSocket.emit("register_email_check", emailEditText.text.toString())
            // check to see if conditions:
            println("socket has emitted")
            mSocket.on("email_check_error"){args ->
                println("fail socket has been received")
                if (args[0] != null) {
                    val message = args[0] as String
                    isEmailUnique = false
                    warningTextView.text = message
                    CoroutineScope(IO).launch {
                        withContext(Dispatchers.Main){
                            warningTextView.text = message
                        }
                    }
                }
            }

            mSocket.on("email_check_success"){args ->
                println("success socket received")
                if (args[0] != null) {
                    println("printing a message, email is good, socket received")
                    val message = args[0] as String
                    isEmailUnique = true
                    println(isEmailUnique)
                    warningTextView.text = message
                    CoroutineScope(IO).launch {
                        withContext(Dispatchers.Main){
                            warningTextView.text = message

                        }
                    }
                }
                if (areConditionsMet() && isEmailUnique) {
                    (activity as? RegisterActivity)?.navigateToStep2(email, password)
                } else {
                    warningTextView.text = "Something is wrong with the socket maybe"
                }

            }

            mSocket.on("email_check_duplicate_detected"){args ->
                println("dupe email socket received")
                if (args[0] != null) {
                    println("printing a message, email duplicate")
                    val message = args[0] as String
                    isEmailUnique = false
                    println(isEmailUnique)
                    warningTextView.text = message
                    CoroutineScope(IO).launch {
                        withContext(Dispatchers.Main){
                            warningTextView.text = message

                        }
                    }
                }
            }
            println("checking condition")
        }
//        try {
//            println("trying to receive socket 1")
//            mSocket.on("email_check_error"){args ->
//                println("fail socket has been received")
//                if (args[0] != null) {
//                    val message = args[0] as String
//                    isEmailUnique = false
//                    warningTextView.text = message
//                    CoroutineScope(IO).launch {
//                        withContext(Dispatchers.Main){
//                            warningTextView.text = message
//                        }
//                    }
//                }
//            }
//            println("trying to receive socket 2")
//
//            mSocket.on("testing_emits"){ args ->
//                println("receive hello event")
//
//                warningTextView.text = "so we are able to get the testing emit event"
//
//            }
//
//            mSocket.on("hitest"){args ->
//                println("hitest reeieved")
//                val message = args[0] as String
//                println(message)
//                warningTextView.text = "so we are able to get the hitest emit event"
//            }
//            mSocket.on("email_check_success"){args ->
//                println("success socket received")
//                if (args[0] != null) {
//                    println("printing a message, email is good, socket received")
//                    val message = args[0] as String
//                    isEmailUnique = true
//                    warningTextView.text = message
//                    CoroutineScope(IO).launch {
//                        withContext(Dispatchers.Main){
//                            warningTextView.text = message
//
//                        }
//                    }
//                }
//            }
//
//        } catch (e: Exception){
//            println("somethign is definitely wrong with the fucking socket wtf")
//        }

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }
}

