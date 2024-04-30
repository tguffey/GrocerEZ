package com.example.grocerez.data

import android.util.Log
import androidx.annotation.WorkerThread
import com.example.grocerez.dao.CategoryDao
import com.example.grocerez.dao.ItemDao
import com.example.grocerez.dao.PantryItemDao
import com.example.grocerez.dao.UnitDao
import com.example.grocerez.data.model.Category
import com.example.grocerez.data.model.Item
import com.example.grocerez.data.model.PantryItem
import com.example.grocerez.data.model.Unit
import com.example.grocerez.ui.dashboard.CategoryPantryItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.withContext

class PantryRepository(
    private val categoryDao: CategoryDao,
    private val itemDao: ItemDao,
    private val pantryItemDao: PantryItemDao,
    private val unitDao: UnitDao
) {

    @WorkerThread
    suspend fun allCategoriesAndPantryItems() : List<CategoryPantryItem> {
        return withContext(Dispatchers.IO) {
            val cats = categoryDao.getAllPantryItemCategories()
            val catsWithPantryItems = mutableListOf<CategoryPantryItem>()
            cats.forEach {
                catsWithPantryItems.add(
                    CategoryPantryItem(
                        it,
                        pantryItemDao.findPantryItemByCategory(it.name)
                    )
                )
            }
            return@withContext catsWithPantryItems.toList()
        }
    }

    // Function to get all pantry items
    @WorkerThread
    suspend fun allPantryItems(): List<PantryItem> {
        return withContext(Dispatchers.IO){
            return@withContext pantryItemDao.getAllPantryItemDao()
        }
    }

    @WorkerThread
    suspend fun pantryItemsInCategory(category: Category) : Flow<List<PantryItem>> {
        return flowOf(pantryItemDao.findPantryItemByCategory(category.name))
    }

    @WorkerThread
    suspend fun getItemCategory(pantryItem: PantryItem) : String {
        return itemDao.findCategoryByName(pantryItem.itemName)
    }

    // Function to insert a new pantry item
    @WorkerThread
    suspend fun insertPantryItem(pantryItem: PantryItem) {
        return withContext(Dispatchers.IO) {
            Log.v("REPO", "adding item...")
            pantryItemDao.insertPantryItemDao(pantryItem)
            Log.v("REPO", "done adding")
        }
    }

    // Function to update an existing pantry item
    @WorkerThread
    suspend fun updatePantryItem(pantryItem: PantryItem) {
        return withContext(Dispatchers.IO) {
            pantryItemDao.updatePantryItemDao(pantryItem)
        }
    }

    @WorkerThread
    suspend fun insertCategory(category: Category) {
        return withContext(Dispatchers.IO) {
            categoryDao.insertCategory(category)
        }
    }

    @WorkerThread
    suspend fun insertItem(item: Item) {
        return withContext(Dispatchers.IO) {
            itemDao.insertItem(item)
        }
    }

    @WorkerThread
    suspend fun insertUnit(unit: com.example.grocerez.data.model.Unit) {
        return withContext(Dispatchers.IO) {
            unitDao.insertUnit(unit)
        }
    }

    // Function to delete a pantry item
    @WorkerThread
    suspend fun deletePantryItem(pantryItem: PantryItem) {
        return withContext(Dispatchers.IO) {
            pantryItemDao.deletePantryItemDao(pantryItem)
        }
    }

    @WorkerThread
    suspend fun findItemByName(name: String) : Item? {
        return withContext(Dispatchers.IO) {
            return@withContext itemDao.findItemByName(name)
        }
    }

    @WorkerThread
    suspend fun findCategoryByName(category: String): Category? {
        return withContext(Dispatchers.IO) {
            return@withContext categoryDao.findCategoryByName(category)
        }
    }

    @WorkerThread
    suspend fun findUnitByName(unit: String): Unit? {
        return withContext(Dispatchers.IO) {
            return@withContext unitDao.findUnitByName(unit)
        }
    }

}
