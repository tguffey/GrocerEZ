package com.example.grocerez.ui.shopping

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ShoppingViewModel : ViewModel(){

    private val _text = MutableLiveData<String>().apply {
        value = "GrocerEZ Shopping Fragment"
    }
    val text: LiveData<String> = _text
}