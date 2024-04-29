package com.example.grocerez.ui.shopping

import com.example.grocerez.data.model.ShoppingListItem

interface ShoppingItemClickListener {
    fun checkItem(shoppingListItem: ShoppingListItem)
}