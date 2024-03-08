package com.example.grocerez.ui.shopping

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ShoppingViewModel : ViewModel(){

    private val _text = MutableLiveData<String>().apply {
        value = "Your shopping list is empty"
    }
    val text: LiveData<String> = _text

    var groceryItems = MutableLiveData<MutableList<GroceryItem>?>()

    init {
        groceryItems.value = mutableListOf()
    }

    // adds a new grocery item to the shopping list
    fun addGroceryItem(newGrocery: GroceryItem) {
        val list = groceryItems.value
        list!!.add(newGrocery)
        groceryItems.postValue(list)
    }
}