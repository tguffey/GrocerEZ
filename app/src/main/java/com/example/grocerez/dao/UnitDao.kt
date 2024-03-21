package com.example.grocerez.dao


import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.grocerez.data.model.Unit



@Dao
interface UnitDao {
    @Query("SELECT * FROM unit")
    suspend fun getAllCategories(): List<Unit>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(unit: Unit)

    @Update
    suspend fun updateCategory(unit: com.example.grocerez.data.model.Unit)

    @Delete
    suspend fun deleteCategory(unit: Unit)
    // Add other CRUD operations as needed
}