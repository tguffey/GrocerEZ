package com.example.grocerez.ui.recipes

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.grocerez.data.RecipeRepository
import com.example.grocerez.data.model.Category
import com.example.grocerez.data.model.Item
import com.example.grocerez.data.model.Recipe
import com.example.grocerez.data.model.Unit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// ViewModel for managing recipe items
class RecipesViewModel (private val repository: RecipeRepository) : ViewModel(){

    // LiveData for storing recipe items
    lateinit var recipes : MutableLiveData<List<Recipe>>

    fun loadRecipes() = viewModelScope.launch(Dispatchers.IO) {
        recipes = MutableLiveData(emptyList())
        recipes = flowOf(repository.getAllRecipes()).asLiveData() as MutableLiveData<List<Recipe>>
    }

    suspend fun findItemByName(name: String) : Item? {
        return withContext(Dispatchers.IO) {
            return@withContext repository.findItemByName(name)
        }
    }

    suspend fun findCategoryByName(cat: String) : Category? {
        return withContext(Dispatchers.IO) {
            return@withContext repository.findCategoryByName(cat)
        }
    }

    suspend fun findUnitByName(unit: String) : Unit? {
        return withContext(Dispatchers.IO) {
            return@withContext repository.findUnitByName(unit)
        }
    }

    suspend fun getItemName(item: Item) : String {
        return withContext(Dispatchers.IO) {
            return@withContext item.getItemName()
        }
    }

    private fun updateData() = viewModelScope.launch(Dispatchers.IO) {
        recipes.postValue(repository.getAllRecipes())
    }

    fun addRecipes(newRecipe: Recipe) = viewModelScope.launch(Dispatchers.IO) {
        Log.v("VIEW MODEL", "in add shop item")
        repository.insertRecipe(newRecipe)
        updateData()
        Log.v("VIEW MODEL", "added new item")
    }

    fun addCategory(newCategory: Category) = viewModelScope.launch(Dispatchers.IO) {
        repository.insertCategory(newCategory)
    }

    fun addItem(newItem: Item) = viewModelScope.launch(Dispatchers.IO) {
        repository.insertItem(newItem)
    }

    fun addUnit(newUnit: com.example.grocerez.data.model.Unit) = viewModelScope.launch(Dispatchers.IO) {
        repository.insertUnit(newUnit)
    }

//    fun toggleCheck(recipeItem: RecipeItem) = viewModelScope.launch(Dispatchers.IO) {
//        RecipeItem.checkbox = !recipeItem.checkbox
//        repository.updateRecipeItem(recipeItem)
//    }

//    fun removeCheckedItems() = viewModelScope.launch(Dispatchers.IO) {
//
//        recipeItems.value?.forEach {
////                if (it.isChecked()) {
//            repository.removeRecipeItem(it)
////                }
//        }
//    }

    class RecipeModelFactory(private val repository: RecipeRepository) : ViewModelProvider.Factory
    {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T
        {
            if (modelClass.isAssignableFrom(RecipesViewModel::class.java))
                return RecipesViewModel(repository) as T

            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

}
