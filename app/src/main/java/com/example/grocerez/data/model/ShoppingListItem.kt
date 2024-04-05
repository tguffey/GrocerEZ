package com.example.grocerez.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(

    tableName = "shopping_list_item",
    foreignKeys = [
        ForeignKey(
            entity = Item::class,
            parentColumns = ["name"],
            childColumns = ["itemName"],
            onDelete = ForeignKey.CASCADE // ensure if the field in parent table is delted, all child objects will be delted as well
        )
    ],
    indices = [Index(value = ["itemName"], unique = true)]
)
data class ShoppingListItem(
    @PrimaryKey(autoGenerate = true) val shoppingListItemId: Long = 0,
    val itemName: String,
    var checkbox: Boolean = false,
    var notes: String? = null,
    var quantity: Float,
//    var unit: String,
//    var category: String
    // Thong: we will NOT be having unit and category as part of this data class
    // because it's a static quality of "Item". as long as we know the item name
    // we will also know its category and unit, so we can just retrieve it that way for display.

)