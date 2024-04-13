package com.example.grocerez.data.model
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import androidx.room.ForeignKey
import androidx.room.Index

// Thong: this table stores recipe, ingredients will be retrieved using a join table with ids
@Entity(tableName = "recipes")
data class Recipe(
    @PrimaryKey(autoGenerate = true)
    val recipe_id: Long = 0,
    val name: String,
    val instruction: String

)
