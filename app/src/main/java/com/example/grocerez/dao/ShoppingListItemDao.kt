// categoryDao.kt
//category Data Access object

package com.example.grocerez.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.grocerez.data.model.Item
import com.example.grocerez.data.model.ShoppingListItem

// Dao for ShoppingListItem
@Dao
interface ShoppingListItemDao {
    @Query("SELECT * FROM shopping_list_item")
    suspend fun getAllShoppingListItem(): List<ShoppingListItem>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertShoppingListItem(shoppingListItem: ShoppingListItem)

    @Update
    suspend fun updateShoppingListItem(shoppingListItem: ShoppingListItem)

    @Delete
    suspend fun deleteShoppingListItem(shoppingListItem: ShoppingListItem)
    // Add other CRUD operations as needed

    @Query("SELECT * FROM shopping_list_item WHERE itemName = :itemName")
    suspend fun findShoppingListItemByName(itemName: String): ShoppingListItem?
}