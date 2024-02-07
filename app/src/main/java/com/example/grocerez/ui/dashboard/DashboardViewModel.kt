package com.example.grocerez.ui.dashboard

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class DashboardViewModel : ViewModel() {

//    private val _text = MutableLiveData<String>().apply {
//        value = "GrocerEZ Dashboard Fragment"
//    }
//    val text: LiveData<String> = _text
    var name = MutableLiveData<String>()
    var value = MutableLiveData<Int>()

}