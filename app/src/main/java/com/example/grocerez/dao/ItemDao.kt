package com.example.grocerez.dao
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.grocerez.data.model.Item
import com.example.grocerez.data.model.Unit
import kotlinx.coroutines.flow.Flow


/* Item has:
    string name
    string category (foreign key)
    float use_rate (per week)
    string unit (foreign key)
*/

@Dao
interface ItemDao {
    @Query("SELECT * FROM items")
    fun getAllItems(): Flow<List<Item>>


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: Item)

    @Delete
    suspend fun deleteItem(item: Item)
    // Add other CRUD operations as needed

    @Update
    suspend fun updateItem(item: Item)

    @Query("SELECT * FROM items WHERE name = :itemName")
    suspend fun findItemByName(itemName: String): Item?

    @Query("SELECT category_name FROM items WHERE name = :itemName")
    suspend fun findCategoryByName(itemName: String): String

    @Query("SELECT * FROM items WHERE category_name = :catName")
    fun findItemsByCategory(catName: String) : Flow<List<Item>>

    // In your DAO class
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItemAndGetId(newItem: Item): Long
}