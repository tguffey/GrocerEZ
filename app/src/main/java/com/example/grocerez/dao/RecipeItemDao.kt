package com.example.grocerez.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.grocerez.data.Ingredient
import com.example.grocerez.data.model.RecipeItem

@Dao
interface RecipeItemDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipeItem(recipeItem: RecipeItem)
    @Delete
    suspend fun deleteRecipeItem(recipeItem: RecipeItem)

    @Update
    suspend fun updateRecipeItem(recipeItem: RecipeItem)

    @Query("SELECT * FROM recipe_items")
    fun getAllRecipeItems(): List<RecipeItem>

    @Query("SELECT i.name, ri.amount, i.category_name AS category, i.unit_name AS unit " +
            "FROM recipe_items ri " +
            "INNER JOIN items i ON ri.item_id = i.item_id " +
            "WHERE ri.recipe_id = :recipeId")
    suspend fun getIngredientsForRecipe(recipeId: Long): List<Ingredient>


}