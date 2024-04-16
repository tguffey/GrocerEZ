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
import com.example.grocerez.data.model.PantryItem

// Dao for PantryItem
@Dao
interface PantryItemDao {
    @Query("SELECT * FROM pantry_item")
    suspend fun getAllPantryItemDao(): List<PantryItem>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPantryItemDao(pantryItem: PantryItem)

    @Update
    suspend fun updatePantryItemDao(pantryItem: PantryItem)

    @Delete
    suspend fun deletePantryItemDao(pantryItem: PantryItem)
    // Add other CRUD operations as needed
}