package com.example.grocerez.ui.recipes

import java.util.UUID

class IngredientItem (
    var name: String, // Name of the ingredient
    var quantity: Double,
    var ingredientUnit: String,
    var id: UUID = UUID.randomUUID() // Unique identifier of the food item
)
{
}