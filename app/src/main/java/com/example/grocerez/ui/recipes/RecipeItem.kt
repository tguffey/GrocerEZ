package com.example.grocerez.ui.recipes

import android.os.Parcel
import android.os.Parcelable
import java.util.UUID

// Represents a recipe item with its properties
class RecipeItem(
    var name: String, // Name of the food item
    var description: String,
    var ingredients: MutableList<IngredientItem>, // List of ingredient items
    var note: String,
    var id: UUID = UUID.randomUUID() // Unique identifier of the food item
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        mutableListOf<IngredientItem>().apply {
            parcel.readTypedList(this, IngredientItem.CREATOR)
        },
        parcel.readString() ?: "",
        parcel.readSerializable() as UUID
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(description)
        parcel.writeTypedList(ingredients)
        parcel.writeString(note)
        parcel.writeSerializable(id)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<RecipeItem> {
        override fun createFromParcel(parcel: Parcel): RecipeItem {
            return RecipeItem(parcel)
        }

        override fun newArray(size: Int): Array<RecipeItem?> {
            return arrayOfNulls(size)
        }
    }
}
