package com.example.grocerez.ui.shopping

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.grocerez.data.ShoppingRepository
import com.example.grocerez.data.model.Category
import com.example.grocerez.data.model.Item
import com.example.grocerez.data.model.ShoppingListItem
import com.example.grocerez.data.model.Unit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// Represents all of the data that gets passed to and from the Shop page
// (data that the user sees)
// To Travis: call the addShoppingListItem function to add from recipes
class ShoppingViewModel(val repository: ShoppingRepository) : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Your shopping list is empty"
    }
    val text: LiveData<String> = _text

    lateinit var categoryItems: MutableLiveData<List<CategoryItem>>

    fun loadShoppingList() = viewModelScope.launch(Dispatchers.IO) {
        categoryItems = MutableLiveData(emptyList())
        categoryItems = flowOf(repository.allCategoriesAndShopItems()).asLiveData() as MutableLiveData<List<CategoryItem>>
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
        categoryItems.postValue(repository.allCategoriesAndShopItems())
    }

    fun addShoppingListItem(newGrocery: ShoppingListItem) = viewModelScope.launch(Dispatchers.IO) {
        Log.v("VIEW MODEL", "in add shop item")
        repository.insertShoppingListItem(newGrocery)
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

    fun toggleCheck(shopItem: ShoppingListItem) = viewModelScope.launch(Dispatchers.IO) {
        shopItem.checkbox = !shopItem.checkbox
        repository.updateShoppingListItem(shopItem)
    }

    fun removeCheckedItems() = viewModelScope.launch(Dispatchers.IO) {
        categoryItems.value?.forEach {
            it.shoppingListItems.forEach {
                if (it.isChecked()) {
                    repository.removeShoppingListItem(it)
                }
            }
        }
        updateData()
    }

    class ShoppingModelFactory(private val repository: ShoppingRepository) : ViewModelProvider.Factory
    {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T
        {
            if (modelClass.isAssignableFrom(ShoppingViewModel::class.java))
                return ShoppingViewModel(repository) as T

            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

    /*// adds a new grocery item to the shopping list
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
    }*/
}