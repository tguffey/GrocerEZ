package com.example.grocerez.ui.recipes

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.util.UUID

// ViewModel for managing recipe items
class RecipesViewModel : ViewModel(){

    // LiveData for storing recipe items
    var recipeItems = MutableLiveData<MutableList<RecipeItem>?>()

    // Initialize the recipeItems LiveData with an empty mutable list
    init {
        recipeItems.value = mutableListOf()
    }

    // Function to add a new recipe item
    fun addRecipeItem(newRecipe: RecipeItem){
        val list = recipeItems.value
        list!!.add(newRecipe)
        recipeItems.postValue(list)
    }

    // Function to update an existing recipe item
    // Function to update an existing recipe item
    fun updateRecipeItem(recipeItem: RecipeItem) {
        val list = recipeItems.value
        val index = list!!.indexOfFirst { it.id == recipeItem.id }
        if (index != -1) {
            list[index] = recipeItem
            recipeItems.postValue(list)
        }
    }


}
