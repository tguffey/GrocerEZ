package com.example.grocerez.ui.dashboard

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class DashboardViewModel : ViewModel() {

    // Live data for item name and value for progress bar
    var name = MutableLiveData<String>()
    var value = MutableLiveData<Int>()

}