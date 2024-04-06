package com.example.grocerez.ui.shopping

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ShoppingViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Your shopping list is empty"
    }
    val text: LiveData<String> = _text

    var groceryItems = MutableLiveData<MutableList<GroceryItem>?>()
    var categoryItems = MutableLiveData<MutableList<CategoryItem>?>()

    init {
        groceryItems.value = mutableListOf()
        categoryItems.value = mutableListOf()
    }

    // adds a new grocery item to the shopping list
    fun addGroceryItem(newGrocery: GroceryItem) {
        val list = groceryItems.value
        list!!.add(newGrocery)
        groceryItems.postValue(list)
    }

    // add a new category item to the shopping list
    fun addCategoryItem(newCategory: CategoryItem) {
        val list = categoryItems.value
        list!!.add(newCategory)
        categoryItems.postValue(list)
    }

    fun removeCheckedItems() {
        val groList = groceryItems.value
        val updatedGroList = groList?.filter { !it.isChecked }
        groceryItems.postValue(updatedGroList as MutableList<GroceryItem>?)

        // Update categoryItems to reflect the changes
        val updatedCategoryItems = categoryItems.value?.map { categoryItem ->
            val updatedGroceries = categoryItem.groceryItems.filter { !it.isChecked }
            CategoryItem(categoryItem.category, updatedGroceries.toMutableList())
        }?.toMutableList()

        // Remove empty categories
        updatedCategoryItems?.removeIf { it.groceryItems.isEmpty() }

        categoryItems.postValue(updatedCategoryItems)
    }

    fun anyChecked() : Boolean {
        val list = groceryItems.value ?: return false
        for (groceryItem in list) {
            if (groceryItem.isChecked) {
                return true
            }
        }
        return false
    }
}