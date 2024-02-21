package com.example.grocerez.ui.dashboard

import java.time.LocalDate
import java.time.LocalTime
import java.util.UUID

class FoodItem (
    var name: String,
    var prog: Int,
    var dueTime: LocalTime?,
    var completedDate: LocalDate?,
    var id: UUID = UUID.randomUUID()
) {

}