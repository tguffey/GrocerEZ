package com.example.grocerez.data

import androidx.annotation.WorkerThread
import com.example.grocerez.dao.CategoryDao
import com.example.grocerez.dao.ItemDao
import com.example.grocerez.dao.ShoppingListItemDao
import com.example.grocerez.dao.UnitDao
import com.example.grocerez.data.model.Category
import com.example.grocerez.data.model.Item
import com.example.grocerez.data.model.ShoppingListItem
import com.example.grocerez.data.model.Unit
import com.example.grocerez.ui.shopping.CategoryItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext

class ShoppingRepository(
    private val categoryDao: CategoryDao,
    private val itemDao: ItemDao,
    private val shoppingListItemDao: ShoppingListItemDao,
    private val unitDao: UnitDao
) {

    @WorkerThread
    suspend fun allCategoriesAndShopItems() : List<CategoryItem> {
        return withContext(Dispatchers.IO) {
            val cats = categoryDao.getAllShoppingListCategories()
            val catsWithShopItems = mutableListOf<CategoryItem>()
            cats.forEach {
                catsWithShopItems.add(
                    CategoryItem(
                        it,
                        shoppingListItemDao.findShoppingListItemByCategory(it.name)
                    )
                )
            }
            return@withContext catsWithShopItems.toList()
        }
    }

    @WorkerThread
    suspend fun allShoppingListItems() : Flow<List<ShoppingListItem>> {
        return withContext(Dispatchers.IO) {
            return@withContext shoppingListItemDao.getAllShoppingListItem()
        }
    }

    @WorkerThread
    fun shoppingListItemsInCategory(category: Category) : Flow<List<ShoppingListItem>> {
        return flowOf(shoppingListItemDao.findShoppingListItemByCategory(category.name))
    }

    @WorkerThread
    suspend fun getItemCategory(shoppingListItem: ShoppingListItem) : String {
        return itemDao.findCategoryByName(shoppingListItem.itemName)
    }

    @WorkerThread
    suspend fun insertShoppingListItem(shoppingListItem: ShoppingListItem) {
        return withContext(Dispatchers.IO) {
            shoppingListItemDao.insertShoppingListItem(shoppingListItem)
        }
    }

    @WorkerThread
    suspend fun updateShoppingListItem(shopItem: ShoppingListItem) {
        return withContext(Dispatchers.IO) {
            shoppingListItemDao.updateShoppingListItem(shopItem)
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

    @WorkerThread
    suspend fun removeShoppingListItem(shopItem: ShoppingListItem) {
        return withContext(Dispatchers.IO) {
            shoppingListItemDao.deleteShoppingListItem(shopItem)
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
