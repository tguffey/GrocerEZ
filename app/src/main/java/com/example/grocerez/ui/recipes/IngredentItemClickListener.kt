package com.example.grocerez.ui.recipes

import com.example.grocerez.data.Ingredient


interface IngredentItemClickListener {
    fun editIngredientItem(ingredient: Ingredient)
}