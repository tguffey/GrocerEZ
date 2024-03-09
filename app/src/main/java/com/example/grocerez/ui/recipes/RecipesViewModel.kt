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
    fun updateRecipeItem(id: UUID, name: String, description: String, ingredients: String, notes: String){
        val list = recipeItems.value
        val recipe = list!!.find{it.id == id}
        recipe!!.name = name
        recipe.description = description
        recipe.ingredients = ingredients
        recipe.note = notes
        recipeItems.postValue(list)
    }

}
