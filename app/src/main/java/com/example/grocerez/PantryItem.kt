package com.example.grocerez

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.PrimaryKey


@Entity(
    tableName = "pantry_item",
    foreignKeys = [
        ForeignKey(
            entity = Item::class,
            parentColumns = ["name"],
            childColumns = ["name"],
            onDelete = CASCADE
        )
    ]
    )
data class PantryItem(
    @PrimaryKey(autoGenerate = true) val id: Long,
//    @ForeignKey(
//        entity = Item::class,
//        parentColumns = ["name"], // Use the actual column name from the referenced entity (Item)
//        childColumns = ["name"], // Use the actual column name in this entity (PantryItem)
//        onDelete = CASCADE
//    )
//    @ColumnInfo(name = "amount_on_input_date") val amountOnInputDate: Int,
    val shelfLife: Int,
    val amount: Int,
    val unit: String,
    val name: String
)