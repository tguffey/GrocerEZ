package com.example.grocerez.ui.recipes

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.grocerez.data.Ingredient
import com.example.grocerez.data.RecipeRepository
import com.example.grocerez.data.model.Category
import com.example.grocerez.data.model.Item
import com.example.grocerez.data.model.Recipe
import com.example.grocerez.data.model.RecipeItem
import com.example.grocerez.data.model.Unit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// ViewModel for managing recipe items
class RecipesViewModel (private val repository: RecipeRepository) : ViewModel(){

    // LiveData for storing recipe items
    lateinit var recipes : MutableLiveData<List<Recipe>>
    lateinit var ingredients : MutableLiveData<List<RecipeItem>>
    private var temporaryIngredientList = mutableListOf<Ingredient>()


    fun loadRecipes() = viewModelScope.launch(Dispatchers.IO) {
        recipes = MutableLiveData(emptyList())
        recipes = flowOf(repository.getAllRecipes()).asLiveData() as MutableLiveData<List<Recipe>>
    }

    fun loadIngredients() = viewModelScope.launch(Dispatchers.IO){
        ingredients = MutableLiveData(emptyList())
        ingredients = flowOf(repository.getAllRecipeItems()).asLiveData() as MutableLiveData<List<RecipeItem>>
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

    private fun updateRecipeData() = viewModelScope.launch(Dispatchers.IO) {
        recipes.postValue(repository.getAllRecipes())
    }

    private fun updateRecipeItemData()  = viewModelScope.launch(Dispatchers.IO){
        ingredients.postValue(repository.getAllRecipeItems())
    }

    fun addRecipes(newRecipe: Recipe) = viewModelScope.launch(Dispatchers.IO) {
        Log.v("VIEW MODEL", "in add shop item")
        repository.insertRecipe(newRecipe)
        updateRecipeData()
        Log.v("VIEW MODEL", "added new item")
    }

    fun addRecipeItems (newRecipeItem: RecipeItem) = viewModelScope.launch(Dispatchers.IO){
        Log.v("VIEW MODEL", "in add shop item")
        repository.insertRecipeItem(newRecipeItem)
        updateRecipeData()
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

    fun addToTemporaryList (ingredient: Ingredient){
        temporaryIngredientList.add(ingredient)
    }

    fun clearTemporaryList(){
        temporaryIngredientList.clear()
    }

    suspend fun findRecipeByName(recipe: Recipe): Recipe?{
        return withContext(Dispatchers.IO){
            return@withContext repository.findRecipeByName(recipe)
        }
    }

    fun returnTemporaryList() : MutableList<Ingredient> {
        return temporaryIngredientList
    }

    fun insertCategory(newCategory: Category) = viewModelScope.launch(Dispatchers.IO) {
        repository.insertCategory(newCategory)
    }

    fun insertUnit(newUnit: Unit) = viewModelScope.launch(Dispatchers.IO) {
        repository.insertUnit(newUnit)
    }

    fun insertItem(newItem: Item) = viewModelScope.launch(Dispatchers.IO) {
        repository.insertItem(newItem)
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
