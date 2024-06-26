package com.example.grocerez.ui.login

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import android.util.Patterns
import androidx.lifecycle.viewModelScope
import com.example.grocerez.data.LoginRepository
import com.example.grocerez.data.Result

import com.example.grocerez.R
import kotlinx.coroutines.launch

class LoginViewModel(private val loginRepository: LoginRepository) : ViewModel() {

    private val _loginForm = MutableLiveData<LoginFormState>()
    val loginFormState: LiveData<LoginFormState> = _loginForm

    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> = _loginResult

    // THONG: assuming passowrd is sent through https, plaintext is sent fine, for now. passowrd is hashed on backend.
    fun login(username: String, password: String) {
        // check for backdoor combination
        if (username == "developer" && password == "backdoor123") {
            // If backdoor combination is detected, set login result to success
            _loginResult.value = LoginResult(
                success = LoggedInUserView(
                    displayName = "developer",
                    username = username,
                    password = password
                )
            )
            return  // Exit the function
        }

        // can be launched in a separate asynchronous job
        val result = loginRepository.login(username, password)

        when (result) {
            is Result.Success -> {
                // Include username and password in LoggedInUserView
                _loginResult.value = LoginResult(
                    success = LoggedInUserView(
                        displayName = result.data.displayName,
                        username = username,
                        password = password
                    )
                )
            }
            is Result.Error -> {
                _loginResult.value = LoginResult(error = R.string.login_failed)
            }
        }
    }

    fun loginDataChanged(username: String, password: String) {
        if (!isUserNameValid(username)) {
            _loginForm.value = LoginFormState(usernameError = R.string.invalid_username)
        } else if (!isPasswordValid(password)) {
            _loginForm.value = LoginFormState(passwordError = R.string.invalid_password)
        } else {
            _loginForm.value = LoginFormState(isDataValid = true)
        }
    }

    // A placeholder username validation check
    private fun isUserNameValid(username: String): Boolean {
        return if (username.contains('@')) {
            Patterns.EMAIL_ADDRESS.matcher(username).matches()
        } else {
            username.isNotBlank()
        }
    }

    // A placeholder password validation check
    private fun isPasswordValid(password: String): Boolean {
        return password.length > 5
    }

}