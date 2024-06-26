package com.example.grocerez.ui.dashboard

import android.os.Parcel
import android.os.Parcelable
import java.time.LocalDate
import java.util.UUID

// Represents a food item with its properties
class FoodItem(
    var name: String, // Name of the food item
    var expirationDate: LocalDate?, // Expiration date of the food item
    var startingDate: LocalDate?, // Completed date of the food item
    var id: UUID = UUID.randomUUID() // Unique identifier of the food item
) : Parcelable {

    constructor(parcel: Parcel?) : this(
        parcel?.readString() ?: "",
        parcel?.readSerializable() as? LocalDate?,
        parcel?.readSerializable() as? LocalDate?,
        UUID.fromString(parcel?.readString() ?: "") // Parse UUID from String
    )

    // Method to calculate the progress based on the starting and expiration dates
    fun calculateProgress(startingDate: LocalDate?, expirationDate: LocalDate?): Int {
        if (startingDate == null || expirationDate == null) {
            return 0
        }

        val currentDate = LocalDate.now()
        val totalDays = expirationDate!!.toEpochDay() - startingDate!!.toEpochDay()
        val elapsedDays = currentDate.toEpochDay() - startingDate!!.toEpochDay()

        return (((totalDays.toDouble() - elapsedDays.toDouble()) / totalDays.toDouble()) * 100).toInt()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeSerializable(expirationDate)
        parcel.writeSerializable(startingDate)
        parcel.writeString(id.toString()) // Convert UUID to String
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<FoodItem> {
        override fun createFromParcel(source: Parcel?): FoodItem? {
            return FoodItem(source)
        }

        override fun newArray(size: Int): Array<FoodItem?> {
            return arrayOfNulls(size)
        }

    }
}

