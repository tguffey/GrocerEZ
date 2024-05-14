package com.example.grocerez.data.model
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

// Thong: this table stores recipe, ingredients will be retrieved using a join table with ids
@Entity(
    tableName = "recipes",
    indices = [Index(value = ["name"], unique = true)]
)
data class Recipe(
    @PrimaryKey(autoGenerate = true)

    @ColumnInfo(name = "recipe_id")
    val recipeId: Long = 0,
    val name: String,
    val instruction: String

)
