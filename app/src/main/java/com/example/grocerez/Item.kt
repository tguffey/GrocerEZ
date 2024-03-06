package com.example.grocerez
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import androidx.room.Index


// for fundamental testing of database, item will only have name and category
@Entity(tableName = "items",
    indices = [
        Index(value = ["name", "category"], unique = true) // Unique constraint, ensure that no combination is the same.
    ]
)
data class Item(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "category") val category: String
)

