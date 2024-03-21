package com.example.grocerez.data.model
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import androidx.room.ForeignKey
import androidx.room.Index

// TODO: create dao objects in a new folder for these.
// for fundamental testing of database, item will only have name and category
@Entity(tableName = "items",
    // adding list of foreign keys, parent is what's in that table, child is on this
    foreignKeys = [
        ForeignKey(
            entity = Category::class,
            parentColumns = ["name"],
            childColumns = ["category_name"]
        ),
        ForeignKey(
            entity = Unit::class,
            parentColumns = ["name"],
            childColumns = ["unit_name"]
        )
                  ],

    // add all the foreign keys field
    // TODO: what does indices do?
    indices = [
        //Index(value = ["name", "category"], unique = true) // Unique constraint, ensure that no combination is the same.
        Index(value = ["name"], unique = true), // Ensure name is unique
        Index(value = ["category_name"]),
        Index(value = ["unit_name"])
    ]

)
data class Item(
    @PrimaryKey(autoGenerate = true) val item_id: Long = 0,
    val name: String,
    val useRate: Float,
    @ColumnInfo(name = "category_name") val category: String,
    @ColumnInfo(name = "unit_name") val unitName: String
)

