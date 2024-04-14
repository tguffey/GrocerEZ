package com.example.grocerez.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.grocerez.data.model.RecipeItem

@Dao
interface RecipeItemDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertRecipeItem(recipeItem: RecipeItem)

    @Query("SELECT * FROM recipe_items")
    fun getAllRecipeItems(): LiveData<List<RecipeItem>>
}