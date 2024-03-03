package com.example.grocerez
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
@Entity(tableName = "items")
data class Item(
    @PrimaryKey @ColumnInfo(name = "name") val name: String,
    val category: String
)

