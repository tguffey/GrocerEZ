package com.example.grocerez.ui.login

/**
 * User details post authentication that is exposed to the UI
 */
data class LoggedInUserView(
    val displayName: String,
    val username: String,
    val password: String
    //... other data fields that may be accessible to the UI
)
