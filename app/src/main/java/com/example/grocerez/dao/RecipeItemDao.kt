package com.example.grocerez.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.grocerez.data.model.RecipeItem

@Dao
interface RecipeItemDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertRecipeItem(recipeItem: RecipeItem)
    @Delete
    suspend fun deleteRecipeItem(recipeItem: RecipeItem)

    @Update
    suspend fun updateRecipeItem(recipeItem: RecipeItem)

    @Query("SELECT * FROM recipe_items")
    fun getAllRecipeItems(): LiveData<List<RecipeItem>>


}