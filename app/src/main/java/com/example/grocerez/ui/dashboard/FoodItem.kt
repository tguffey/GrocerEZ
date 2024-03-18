package com.example.grocerez.ui.dashboard

import java.time.LocalDate
import java.time.LocalTime
import java.util.UUID

// Represents a food item with its properties
class FoodItem(
    var name: String, // Name of the food item
    var prog: Int, // Progress of the food item
    var dueTime: LocalTime?, // Due time of the food item
    var completedDate: LocalDate?, // Completed date of the food item
    var id: UUID = UUID.randomUUID() // Unique identifier of the food item
) {
}
