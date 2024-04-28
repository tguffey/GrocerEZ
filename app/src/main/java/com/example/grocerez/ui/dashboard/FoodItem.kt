package com.example.grocerez.ui.dashboard

import java.time.LocalDate
import java.util.UUID

// Represents a food item with its properties
class FoodItem(
    var name: String, // Name of the food item
    var expirationDate: LocalDate?, // Expiration date of the food item
    var startingDate: LocalDate?, // Completed date of the food item
    var id: UUID = UUID.randomUUID() // Unique identifier of the food item
) {
    // Method to calculate the progress based on the starting and expiration dates
    fun calculateProgress(startingDate: LocalDate?, expirationDate: LocalDate?): Int {
        if (startingDate == null || expirationDate == null) {
            return 0
        }

        val currentDate = LocalDate.now()
        val totalDays = expirationDate.toEpochDay() - startingDate.toEpochDay()
        val elapsedDays = currentDate.toEpochDay() - startingDate.toEpochDay()

        return (((totalDays.toDouble() - elapsedDays.toDouble()) / totalDays.toDouble()) * 100).toInt()
    }
}
