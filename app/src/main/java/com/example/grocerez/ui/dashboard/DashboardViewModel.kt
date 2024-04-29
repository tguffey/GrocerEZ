package com.example.grocerez.ui.dashboard

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.time.LocalDate
import java.util.UUID

class DashboardViewModel : ViewModel() {

    var foodItems = MutableLiveData<MutableList<FoodItem>?>()
    var categoryPantryItems = MutableLiveData<MutableList<CategoryPantryItem>?>()

    init{
        foodItems.value =  mutableListOf()
        categoryPantryItems.value = mutableListOf()
    }

    fun addFoodItem(newFood: FoodItem){
        val list = foodItems.value
        list!!.add(newFood)
        foodItems.postValue(list)
    }

    fun addCategoryItem(newCategory: CategoryPantryItem) {
        val list = categoryPantryItems.value
        list!!.add(newCategory)
        categoryPantryItems.postValue(list)
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
}