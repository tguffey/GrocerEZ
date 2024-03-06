package com.example.grocerez.ui.shopping

import com.example.grocerez.ui.ItemAmount
import java.util.UUID

class GroceryItem (
    var name: String,
    var category: String,
    var quantity: ItemAmount,
    var note: String,
    var id: UUID = UUID.randomUUID()
)