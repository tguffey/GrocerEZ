package com.example.grocerez.data

import android.util.Log
import androidx.annotation.WorkerThread
import com.example.grocerez.dao.CategoryDao
import com.example.grocerez.dao.ItemDao
import com.example.grocerez.dao.RecipeDao
import com.example.grocerez.dao.RecipeItemDao
import com.example.grocerez.dao.UnitDao
import com.example.grocerez.data.model.Category
import com.example.grocerez.data.model.Item
import com.example.grocerez.data.model.Recipe
import com.example.grocerez.data.model.RecipeItem
import com.example.grocerez.data.model.ShoppingListItem
import com.example.grocerez.data.model.Unit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RecipeRepository(
    private val recipeDao: RecipeDao,
    private val itemDao: ItemDao,
    private val recipeItemDao: RecipeItemDao,
    private val unitDao: UnitDao,
    private val categoryDao: CategoryDao
)
{
    @WorkerThread
    suspend fun findRecipeByName(recipe: Recipe): Recipe?{
        return withContext(Dispatchers.IO){
            recipeDao.findRecipeByName(recipe.name)
        }
    }

    @WorkerThread
    suspend fun insertRecipe(recipe: Recipe) {
        return withContext(Dispatchers.IO) {
            Log.v("REPO", "adding item...")
            recipeDao.insertRecipe(recipe)
            Log.v("REPO", "done adding")
        }
    }

    @WorkerThread
    suspend fun updateRecipe(recipe: Recipe) {
        return withContext(Dispatchers.IO) {
            recipeDao.updateRecipe(recipe)
        }
    }

    @WorkerThread
    suspend fun getAllRecipes(): List<Recipe> {
        return withContext(Dispatchers.IO){
            return@withContext recipeDao.getAllRecipes()
        }
    }

    @WorkerThread
    suspend fun deleteRecipe(recipe: Recipe) {
        return withContext(Dispatchers.IO) {
            recipeDao.deleteRecipe(recipe)
        }
    }

    @WorkerThread
    suspend fun getIngredientsForRecipe(recipeId: Long): List<Ingredient> {
        return withContext(Dispatchers.Default) {
            recipeItemDao.getIngredientsForRecipe(recipeId)
        }
    }

    @WorkerThread
    suspend fun getAllRecipeItems(): List<RecipeItem> {
        return withContext(Dispatchers.Default) {
            return@withContext recipeItemDao.getAllRecipeItems()
        }
    }

    @WorkerThread
    suspend fun insertRecipeItem(recipeItem: RecipeItem) {
        return withContext(Dispatchers.IO) {
            Log.v("REPO", "adding item...")
            recipeItemDao.insertRecipeItem(recipeItem)
            Log.v("REPO", "done adding")
        }
    }


    @WorkerThread
    suspend fun deleteRecipe(recipeItem: RecipeItem) {
        return withContext(Dispatchers.IO) {
            recipeItemDao.deleteRecipeItem(recipeItem)
        }
    }

    @WorkerThread
    suspend fun updateRecipeItem(recipeItem: RecipeItem) {
        return withContext(Dispatchers.IO) {
            recipeItemDao.updateRecipeItem(recipeItem)
        }
    }

    @WorkerThread
    suspend fun insertItem(item: Item) {
        return withContext(Dispatchers.IO) {
            itemDao.insertItem(item)
        }
    }

    @WorkerThread
    suspend fun insertUnit(unit: Unit) {
        return withContext(Dispatchers.IO) {
            unitDao.insertUnit(unit)
        }
    }

    @WorkerThread
    suspend fun findItemByName(name: String) : Item? {
        return withContext(Dispatchers.IO) {
            return@withContext itemDao.findItemByName(name)
        }
    }

    @WorkerThread
    suspend fun findUnitByName(unit: String): Unit? {
        return withContext(Dispatchers.IO) {
            return@withContext unitDao.findUnitByName(unit)
        }
    }

    @WorkerThread
    suspend fun insertCategory(category: Category) {
        return withContext(Dispatchers.IO) {
            categoryDao.insertCategory(category)
        }
    }

    @WorkerThread
    suspend fun findCategoryByName(category: String): Category? {
        return withContext(Dispatchers.IO) {
            return@withContext categoryDao.findCategoryByName(category)
        }
    }

    @WorkerThread
    suspend fun getItemCategory(shoppingListItem: ShoppingListItem) : String {
        return itemDao.findCategoryByName(shoppingListItem.itemName)
    }

}