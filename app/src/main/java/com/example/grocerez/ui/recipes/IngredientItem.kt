package com.example.grocerez.ui.recipes
import android.os.Parcel
import android.os.Parcelable
import java.util.UUID

class IngredientItem(
    var name: String, // Name of the ingredient
    var quantity: Double,
    var ingredientUnit: String,
    var category: String,
    var id: UUID = UUID.randomUUID() // Unique identifier of the ingredient item
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readDouble(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        UUID.fromString(parcel.readString()) // Parse UUID from String
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeDouble(quantity)
        parcel.writeString(ingredientUnit)
        parcel.writeString(id.toString()) // Convert UUID to String
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<IngredientItem> {
        override fun createFromParcel(parcel: Parcel): IngredientItem {
            return IngredientItem(parcel)
        }

        override fun newArray(size: Int): Array<IngredientItem?> {
            return arrayOfNulls(size)
        }
    }
}
