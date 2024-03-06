package com.example.grocerez
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo


// for fundamental testing of database, item will only have name and category
@Entity(tableName = "items")
data class Item(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "category") val category: String
)

