package com.example.grocerez.ui.shopping

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ShoppingViewModel : ViewModel(){

    private val _text = MutableLiveData<String>().apply {
        value = "Your shopping list is empty"
    }
    val text: LiveData<String> = _text
}