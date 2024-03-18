package com.example.grocerez.ui.recipes

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.util.UUID

class RecipesViewModel : ViewModel(){

    var recipeItems = MutableLiveData<MutableList<RecipeItem>?>()

    init {
        recipeItems.value = mutableListOf()
    }

    fun addRecipeItem(newRecipe: RecipeItem){
        val list = recipeItems.value
        list!!.add(newRecipe)
        recipeItems.postValue(list)
    }

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