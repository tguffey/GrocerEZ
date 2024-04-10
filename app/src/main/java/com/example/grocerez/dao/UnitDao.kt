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
    suspend fun getAllUnits(): List<Unit>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUnit(unit: Unit)

    @Update
    suspend fun updateUnit(unit: com.example.grocerez.data.model.Unit)

    @Delete
    suspend fun deleteUnit(unit: Unit)
    // Add other CRUD operations as needed

    // Define a query method to search for a unit by its name
    @Query("SELECT * FROM unit WHERE name = :unitName")
    suspend fun findUnitByName(unitName: String): Unit?
}