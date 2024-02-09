package com.example.grocerez.ui.myplate

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MyPlateViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "GrocerEZ MyPlate Fragment"
    }
    val text: LiveData<String> = _text
}