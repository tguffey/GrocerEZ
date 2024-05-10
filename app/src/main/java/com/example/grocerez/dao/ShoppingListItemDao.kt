// categoryDao.kt
//category Data Access object

package com.example.grocerez.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.grocerez.data.model.Category
import com.example.grocerez.data.model.Item
import com.example.grocerez.data.model.ShoppingListItem
import kotlinx.coroutines.flow.Flow

// Dao for ShoppingListItem
@Dao
interface ShoppingListItemDao {
    @Query("SELECT * FROM shopping_list_item")
    fun getAllShoppingListItem(): Flow<List<ShoppingListItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertShoppingListItem(shoppingListItem: ShoppingListItem)

    @Update
    suspend fun updateShoppingListItem(shoppingListItem: ShoppingListItem)

    @Delete
    suspend fun deleteShoppingListItem(shoppingListItem: ShoppingListItem)
    // Add other CRUD operations as needed

    @Query("SELECT * FROM shopping_list_item WHERE itemName = :itemName")
    suspend fun findShoppingListItemByName(itemName: String): ShoppingListItem?

    @Query("SELECT shoppingListItemId, itemName, checkbox, notes, quantity FROM shopping_list_item INNER JOIN items ON itemName = name WHERE category_name = :catName")
    fun findShoppingListItemByCategory(catName: String): List<ShoppingListItem>

/*    @Query("SELECT DISTINCT name FROM shopping_list_item INNER JOIN items ON itemName = name")
    fun getAllShoppingListCategories(): List<Category>*/
}