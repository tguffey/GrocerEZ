package com.example.grocerez.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "units",
    primaryKeys = ["name"]
)
data class Unit(
    val name: String
){
    // this might solve the error that say "only const val can be used in constant expression"
    // dunno what a companion object is tho
    companion object {
        const val COLUMN_NAME = "name"
    }
}