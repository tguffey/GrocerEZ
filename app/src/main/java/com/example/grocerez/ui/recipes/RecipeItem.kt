package com.example.grocerez.ui.recipes

import java.util.UUID

// Represents a food item with its properties
class RecipeItem(
    var name: String, // Name of the food item
    var description: String,
    var ingredients: String,
    var note: String,
    var id: UUID = UUID.randomUUID() // Unique identifier of the food item
) {
}