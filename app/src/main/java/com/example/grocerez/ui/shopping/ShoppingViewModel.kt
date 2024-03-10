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

    fun removeCheckedItems() {
        val list = groceryItems.value
        val iterator = list!!.iterator()
        while(iterator.hasNext()) {
            val item = iterator.next()
            if(item.isChecked){
                iterator.remove()
            }
        }
        groceryItems.postValue(list)
    }

    fun anyChecked() : Boolean {
        val list = groceryItems.value
        list!!.forEach { it ->
            if (it.isChecked) {
                return true
            }
        }
        return false
    }
}