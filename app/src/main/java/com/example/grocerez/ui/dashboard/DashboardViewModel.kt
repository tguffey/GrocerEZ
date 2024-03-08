package com.example.grocerez.ui.dashboard

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.time.LocalDate
import java.time.LocalTime
import java.util.UUID

class DashboardViewModel : ViewModel() {

    var foodItems = MutableLiveData<MutableList<FoodItem>?>()

    init{
        foodItems.value =  mutableListOf()
    }

    fun addFoodItem(newFood: FoodItem){
        val list = foodItems.value
        list!!.add(newFood)
        foodItems.postValue(list)
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



    // Live data for item name and value for progress bar
//    var name = MutableLiveData<String>()
//    var value = MutableLiveData<Int>()

}