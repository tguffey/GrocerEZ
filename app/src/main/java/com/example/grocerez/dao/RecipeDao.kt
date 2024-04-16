package com.example.grocerez.dao
import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.grocerez.data.model.Recipe
import com.example.grocerez.data.model.ShoppingListItem

@Dao
interface RecipeDao {

    @Query("SELECT * FROM recipes WHERE name = :recipeName")
    suspend fun findRecipeByName(recipeName: String): Recipe?
//    suspend fun findRecipeByName(recipeName: String): LiveData<Recipe>

    @Query("SELECT * FROM recipes")
    fun getAllRecipes(): LiveData<List<Recipe>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipe(recipe: Recipe)

    @Update
    suspend fun updateRecipe(recipe: Recipe)

    @Delete
    suspend fun deleteRecipe(recipe: Recipe)



}