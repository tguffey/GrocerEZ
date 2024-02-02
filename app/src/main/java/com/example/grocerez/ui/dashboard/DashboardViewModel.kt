package com.example.grocerez.ui.dashboard

import android.widget.Button
import android.widget.ProgressBar
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class DashboardViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "GrocerEZ Dashboard Fragment"
    }
    val text: LiveData<String> = _text
}