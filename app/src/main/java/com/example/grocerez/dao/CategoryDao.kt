// categoryDao.kt
//category Data Access object

package com.example.grocerez.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.grocerez.data.model.Category
import kotlinx.coroutines.flow.Flow
import com.example.grocerez.data.model.Unit


@Dao
interface CategoryDao {
    @Query("SELECT * FROM category")
    fun getAllCategories(): Flow<List<Category>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: Category)

    @Update
    suspend fun updateCategory(category: Category)

    @Delete
    suspend fun deleteCategory(category: Category)
    // Add other CRUD operations as needed

    // Define a query method to search for a unit by its name
    @Query("SELECT * FROM category WHERE name = :categoryName")
    suspend fun findCategoryByName(categoryName: String): Category?

    @Query("SELECT DISTINCT category.name FROM category INNER JOIN items ON category.name = items.category_name INNER JOIN shopping_list_item ON itemName = items.name")
    fun getAllShoppingListCategories(): List<Category>
}