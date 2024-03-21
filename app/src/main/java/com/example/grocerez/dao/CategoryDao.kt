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


@Dao
interface CategoryDao {
    @Query("SELECT * FROM category")
    suspend fun getAllCategories(): List<Category>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUnit(category: Category)

    @Update
    suspend fun updateUnit(category: Category)

    @Delete
    suspend fun deleteUnit(category: Category)
    // Add other CRUD operations as needed
}