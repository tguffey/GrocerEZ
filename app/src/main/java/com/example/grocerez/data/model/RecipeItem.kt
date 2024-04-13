package com.example.grocerez.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

// Thong: this is the join tbale of recipe and items, has id of both.
@Entity(tableName = "recipe_items",
    foreignKeys = [
        ForeignKey(
            entity = Recipe::class,
            parentColumns = ["recipe_id"],
            childColumns = ["recipe_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Item::class,
            parentColumns = ["item_id"],
            childColumns = ["item_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
)
data class RecipeItem(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "recipe_item_id")
    val recipe_item_id: Long = 0,

    @ColumnInfo(name = "recipe_id")
    val recipe_id: Long,

    @ColumnInfo(name = "item_id")
    val item_id: Long,

    val amount: Float
)
