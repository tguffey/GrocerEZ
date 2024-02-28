package com.example.grocerez.ui.shopping

import com.example.grocerez.ui.ItemAmount
import java.util.UUID

class GroceryItem (
    var name: String,
    var catgeory: String,
    var quantity: ItemAmount,
    var id: UUID = UUID.randomUUID()
)