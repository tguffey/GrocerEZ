package com.example.grocerez.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "my_plate_item",
    foreignKeys = [
        ForeignKey(
            entity = Category::class,
            parentColumns = ["name"],
            childColumns = ["categoryName"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Unit::class,
            parentColumns = ["name"],
            childColumns = ["unit"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class MyPlateItem(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,

    @ColumnInfo(name = "categoryName")
    val categoryName: String,

    @ColumnInfo(name = "unit")
    val unit: String,

    @ColumnInfo(name = "amount")
    val amount: Float
)


