package com.example.grocerez.data

data class PantryItemUpdate(
    val itemName: String? = null,
    val amountFromInputDate: Float? = null,
    val inputDate: String? = null,
    val shelfLifeFromInputDate: Int? = null
)
