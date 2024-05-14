package com.example.grocerez.ui.shopping

import java.util.UUID

data class HistoryItem (
    var name: String,
    var checkbox: Boolean,
    var quantity: String,
    var unit: String,
    var id: UUID = UUID.randomUUID()
)