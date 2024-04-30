package com.example.grocerez.ui.login

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import com.example.grocerez.MainActivity
import com.example.grocerez.databinding.ActivityLoginBinding
import com.example.grocerez.R
import com.example.grocerez.SocketHandler
import com.example.grocerez.ui.login.LoginResult


class LoginActivity : AppCompatActivity() {


    private lateinit var loginViewModel: LoginViewModel
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        // Make the status bar transparent
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = Color.TRANSPARENT
        }
//        // ESTABLISH SOCKET CONNECTION
//        SocketHandler.setSocket()
        // singleton object
         val mSocket = SocketHandler.getSocket()
//        mSocket.connect()
         mSocket.emit("hellotest")



        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val username = binding.loginPgUsernameEntry
        val password = binding.loginPgPasswordEntry
        val login = binding.loginpageLoginBtn
        val warning_text = binding.loginWarningTextview
        val loading = binding.loading

        warning_text?.text = "" ?: "where the warning message go"

        loginViewModel = ViewModelProvider(this, LoginViewModelFactory())
            .get(LoginViewModel::class.java)

        loginViewModel.loginFormState.observe(this@LoginActivity, Observer {
            val loginState = it ?: return@Observer

            // disable login button unless both username / password is valid
            login.isEnabled = loginState.isDataValid

            if (loginState.usernameError != null) {
                username.error = getString(loginState.usernameError)
            }
            if (loginState.passwordError != null) {
                password.error = getString(loginState.passwordError)
            }
        })

        loginViewModel.loginResult.observe(this@LoginActivity, Observer {
            val loginResult = it ?: return@Observer

            loading.visibility = View.GONE
            if (loginResult.error != null) {
                showLoginFailed(loginResult.error)
            }
            // this part goes to the main home activity
            if (loginResult.success != null) {
                // CASE -1: BACKDOOR
                if (username.text.toString() == "developer" && password.text.toString() == "backdoor123") {
                    runOnUiThread {
                        updateUiWithUser(loginResult.success)
                    }
                    finish() // Exit observer after backdoor access
                }

                // CASE 0: NO SOCKET CONNECTION
                if (!mSocket.connected()){
                    Toast.makeText(applicationContext, "if not able to log in, check your connection!", Toast.LENGTH_SHORT).show()
                    finish() // Exit observer
                }

                // CASE 1: USER EXISTS, CORRECT PASSWORD
                mSocket.on("success_login"){data ->
                    runOnUiThread {
                        updateUiWithUser(loginResult.success)
                    }
                    finish()
                }

                // CASE 2: USER EXISTS, WRONG PASSWORD
                mSocket.on("fail_wrongPassword"){data ->
                    runOnUiThread {
                        Toast.makeText(
                            applicationContext,
                            "The password is incorrect!",
                            Toast.LENGTH_LONG
                        ).show()
                    }

                }

                // CASE 3: USER DOES NOT EXIST
                mSocket.on("fail_noUserExist"){data ->
                    runOnUiThread {
                        Toast.makeText(
                            applicationContext,
                            "There is no user with that name, check your spelling!",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                // CASE 4: server error
                mSocket.on("loginError"){data ->
                    runOnUiThread {
                        Toast.makeText(
                            applicationContext,
                            "Internal Server Error, try again later!",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    finish()
                }




            }
            setResult(Activity.RESULT_OK)

            // Complete and destroy login activity once successful
//            finish()
        })

        username.afterTextChanged {
            loginViewModel.loginDataChanged(
                username.text.toString(),
                password.text.toString()
            )

        }

        password.apply {
            afterTextChanged {
                loginViewModel.loginDataChanged(
                    username.text.toString(),
                    password.text.toString()
                )
            }

            setOnEditorActionListener { _, actionId, _ ->
                when (actionId) {
                    EditorInfo.IME_ACTION_DONE ->
                        loginViewModel.login(
                            username.text.toString(),
                            password.text.toString()
                        )
                }
                false
            }

            // THIS IS TYLER, GONNA SET UP INFO SENDING TO SERVER
            // TODO: tyler: debug this section and send info to database.
            login.setOnClickListener {
                loading.visibility = View.VISIBLE
//                val email = "genericemail@gmail.com"
                loginViewModel.login(username.text.toString(), password.text.toString())
//                mSocket.emit("save_signup_info",username.getText().toString(), email.toString(), password.getText().toString())
                mSocket.emit("log_in", username.text.toString(), password.text.toString())

            }

//            mSocket.on("success_login"){data ->
//                runOnUiThread {
//                    // Handle the success login event
////                    updateUiWithUser(LoginResult(success = LoggedInUserView(data.toString(), "", "")))
//                }
//            }
        }
    }

    private var isPasswordVisible = false
    fun togglePasswordVisibility(view: View) {
        val passwordEditText = findViewById<EditText>(R.id.login_pg_password_entry)
        isPasswordVisible = !isPasswordVisible

        passwordEditText.transformationMethod =
            if (isPasswordVisible) null else PasswordTransformationMethod.getInstance()

        val eyeButton = findViewById<ImageButton>(R.id.eyeButton)
        eyeButton.setImageResource(
            if (isPasswordVisible) R.drawable.eye_closed_green001 else R.drawable.eye_closed
        )
    }

    /*
    private fun updateUiWithUser(loginResult: LoginResult) {
        val welcome = getString(R.string.welcome)
//        val displayName = model.displayName
        when (loginResult) {
            is LoginResult.success -> {
                // Display a toast message indicating successful login
                Toast.makeText(
                    applicationContext,
                    getString(R.string.welcome, loginResult.success?.displayName ?: ""),
                    Toast.LENGTH_LONG
                ).show()

                // Navigate to the MainActivity upon successful login
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish() // Finish the LoginActivity to prevent going back to it with the back button
            }
            is LoginResult.error -> {
                // Display a toast message indicating login failure
                Toast.makeText(applicationContext, R.string.login_failed, Toast.LENGTH_SHORT).show()
            }
        }
    }

     */


    private fun updateUiWithUser(model: LoggedInUserView) {
        val welcome = getString(R.string.welcome)
        val displayName = model.displayName

        // Save user login information to SharedPreferences
        saveUserToSharedPreferences(model.username, model.password)

        // TODO : initiate successful logged-in experience
        Toast.makeText(
            applicationContext,
            "$welcome $displayName",
            Toast.LENGTH_LONG
        ).show()

        // Intent to navigate to MainActivity upon successful login
        // I added an intent to navigate to Main Activity upon successful login - Jocelyn
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)

        // Finish the Login Activity to prevent going back to it with the back button
        finish()
    }


    private fun saveUserToSharedPreferences(username: String, password: String) {
        val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("username", username)
        editor.putString("password", password)
        editor.apply()
    }

    private fun showLoginFailed(@StringRes errorString: Int) {
        Toast.makeText(applicationContext, errorString, Toast.LENGTH_SHORT).show()
    }
}

/**
 * Extension function to simplify setting an afterTextChanged action to EditText components.
 */
fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(editable.toString())
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    })
}
