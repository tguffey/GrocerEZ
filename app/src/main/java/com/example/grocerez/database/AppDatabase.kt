package com.example.grocerez.database

// AppDatabase.kt
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.grocerez.data.model.Item
import com.example.grocerez.data.model.Category
import com.example.grocerez.data.model.Unit
import com.example.grocerez.dao.ItemDao
import com.example.grocerez.dao.CategoryDao
import com.example.grocerez.dao.PantryItemDao
import com.example.grocerez.dao.ShoppingListItemDao
import com.example.grocerez.dao.UnitDao
import com.example.grocerez.data.model.PantryItem
import com.example.grocerez.data.model.ShoppingListItem


@Database(entities = [Item::class, Category::class, Unit::class, ShoppingListItem::class, PantryItem::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun itemDao(): ItemDao
    abstract fun categoryDao(): CategoryDao
    abstract fun unitDao(): UnitDao

    abstract fun shoppingListItemDao(): ShoppingListItemDao

    abstract fun pantryItemDao(): PantryItemDao

    //making this a singleton object
    companion object {

        // The volatile notation makes sure that all threads executing can see immediately when the state of this variable has been changed, even if local threading.
        @Volatile
        private var instance: AppDatabase? = null

        // Everywhere: to access the database, use ONLY getInstance()
        fun getInstance(context: Context): AppDatabase {
            // check to see if instance is null with the ?:, if null, perform the part after
            // synchronized makes sure all threads communicate
            return instance ?: synchronized(this) {

                // check again to see if any other threads has created an instance yet.
                instance ?: buildDatabase(context).also { instance = it }

                // if absollutely no thread has craeted an instance, make a new one
                // the also function does some extra stuff after creation, in this case
                // assign that new created database to instance.

            }
        }

        private fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java, "app_database"
            ).build()
        }

    }




}