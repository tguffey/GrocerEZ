package com.example.grocerez.ui.recipes

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class RecipesViewModel : ViewModel(){

    private val _text = MutableLiveData<String>().apply {
        value = "GrocerEZ Recipes Fragment"
    }
    val text: LiveData<String> = _text
}