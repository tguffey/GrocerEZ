package com.example.grocerez.data.model


import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date

@Entity(
    tableName = "pantry_item",
    foreignKeys = [
        ForeignKey(
            entity = Item::class,
            parentColumns = ["name"],
            childColumns = ["item_name"],
            onDelete = ForeignKey.CASCADE // ensure if the field in parent table is delted, all child objects will be delted as well
        )
    ],
    indices = [Index(value = ["item_name"], unique = true)]
)
data class PantryItem(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "pantry_item_id")
    val pantryItemId: Long = 0,

    @ColumnInfo(name = "item_name")
    val itemName: String, // Foreign key to Item table

    @ColumnInfo(name = "amount_from_input_date")
    val amountFromInputDate: Float,

    @ColumnInfo(name = "input_date")
    val inputDate: Date,

    @ColumnInfo(name = "shelf_life_from_input_date")
    val shelfLifeFromInputDate: Int, // Calculated in days
    // user can also input expiration date and we calculate this.


    // Thong: we will NOT be doing category and unit
    // we have name as foreign key so we can just get that from the item table.
    // for display purpose. category and unit are static fields of item.
)
