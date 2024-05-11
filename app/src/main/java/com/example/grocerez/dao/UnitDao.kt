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
    @Query("SELECT * FROM units")
    suspend fun getAllUnits(): List<Unit>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUnit(unit: Unit)

    @Update
    suspend fun updateUnit(unit: com.example.grocerez.data.model.Unit)

    @Delete
    suspend fun deleteUnit(unit: Unit)
    // Add other CRUD operations as needed

    // Define a query method to search for a unit by its name
    @Query("SELECT * FROM units WHERE name = :unitName")
    suspend fun findUnitByName(unitName: String): Unit?

    @Query("SELECT units.name FROM units INNER JOIN items ON units.name = items.unit_name WHERE items.name = :itemName")
    suspend fun findUnitByItemName(itemName: String) : Unit?
}