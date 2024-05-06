package com.example.grocerez.ui.dashboard

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.grocerez.data.PantryRepository
import com.example.grocerez.data.model.Category
import com.example.grocerez.data.model.Item
import com.example.grocerez.data.model.PantryItem
import com.example.grocerez.data.model.Unit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.util.UUID

class DashboardViewModel (val repository: PantryRepository) : ViewModel() {

    lateinit var categoryPantryItems: MutableLiveData<List<CategoryPantryItem>>

    var foodItems = MutableLiveData<MutableList<FoodItem>?>()

    fun loadPantryList() = viewModelScope.launch(Dispatchers.IO) {
        categoryPantryItems = MutableLiveData(emptyList())
        categoryPantryItems = flowOf(repository.allCategoriesAndPantryItems()).asLiveData() as MutableLiveData<List<CategoryPantryItem>>
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
        categoryPantryItems.postValue(repository.allCategoriesAndPantryItems())
    }

    fun addFoodItem(newFood: PantryItem) = viewModelScope.launch(Dispatchers.IO){
        Log.v("VIEW MODEL", "in add pantry item")
        repository.insertPantryItem(newFood)
        updateData()
        Log.v("VIEW MODEL", "added new item")
    }

    fun addCategory (newCategory: Category) = viewModelScope.launch(Dispatchers.IO) {
        repository.insertCategory(newCategory)
    }

    fun addItem(newItem: Item) = viewModelScope.launch(Dispatchers.IO) {
        repository.insertItem(newItem)
    }

    fun addUnit(newUnit: com.example.grocerez.data.model.Unit) = viewModelScope.launch(Dispatchers.IO) {
        repository.insertUnit(newUnit)
    }

    fun updateFoodItem(id: UUID, name: String, startingDate: LocalDate?, expireDate: LocalDate?){
        val list = foodItems.value
        val food = list!!.find{it.id == id}
        food!!.name = name
        food.startingDate = startingDate
        food.expirationDate = expireDate
        foodItems.postValue(list)
    }

    fun setCompleted(foodItem: FoodItem){
        val list = foodItems.value
        val food = list!!.find { it.id == foodItem.id }!!
        if(food.startingDate == null)
            food.startingDate = LocalDate.now()
        foodItems.postValue(list)
    }

    class PantryModelFactory(private val repository: PantryRepository) : ViewModelProvider.Factory
    {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T
        {
            if (modelClass.isAssignableFrom(DashboardViewModel::class.java))
                return DashboardViewModel(repository) as T

            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}