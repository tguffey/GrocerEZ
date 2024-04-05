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
import com.example.grocerez.data.model.Unit


@Dao
interface CategoryDao {
    @Query("SELECT * FROM category")
    suspend fun getAllCategories(): List<Category>

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
}