package com.example.grocerez.databaseActivities

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.grocerez.R
import com.example.grocerez.dao.CategoryDao
import com.example.grocerez.dao.ItemDao
import com.example.grocerez.dao.RecipeDao
import com.example.grocerez.dao.RecipeItemDao

import com.example.grocerez.dao.ShoppingListItemDao
import com.example.grocerez.dao.UnitDao
import com.example.grocerez.data.Ingredient
import com.example.grocerez.data.model.ShoppingListItem

import com.example.grocerez.database.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ViewRecipesActivity : AppCompatActivity() {
    private lateinit var recipeNameTofindEdittext: EditText
    private lateinit var findDisplayRecipeBtn: Button
    private lateinit var findDisplayAllRecipeBtn: Button
    private lateinit var sendRecipeToShopBtn: Button

    private lateinit var viewRecipeTextview: TextView

    private lateinit var recipeDao: RecipeDao
    private lateinit var recipeItemDao: RecipeItemDao
    private lateinit var shoppingListItemDao: ShoppingListItemDao

    private lateinit var itemDao: ItemDao
    private lateinit var categoryDao: CategoryDao
    private lateinit var unitDao: UnitDao



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_view_recipes)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        ///_____________________________BINDINGS
        recipeNameTofindEdittext = findViewById(R.id.recipeNameToViewEditText)
        findDisplayRecipeBtn = findViewById(R.id.displayRecipeByNameButton)
        findDisplayAllRecipeBtn = findViewById(R.id.displayAllRecipesButton)
        viewRecipeTextview = findViewById(R.id.viewRecipeTextView)

        sendRecipeToShopBtn = findViewById(R.id.sendRecipeToShopListButton)

        //_____________________________DB stuff
        val appDatabase = AppDatabase.getInstance(this)
        recipeDao = appDatabase.recipeDao()
        recipeItemDao = appDatabase.recipeItemDao()

        categoryDao = appDatabase.categoryDao()
        itemDao = appDatabase.itemDao()
        unitDao = appDatabase.unitDao()

        shoppingListItemDao = appDatabase.shoppingListItemDao()

        //____________________________-__BUTTONS
        findDisplayRecipeBtn.setOnClickListener {

            val recipeName = recipeNameTofindEdittext.text.toString().trim()

            displayOneRecipe()
        }
        findDisplayAllRecipeBtn.setOnClickListener {
            sendRecipeToShopBtn.visibility  = View.INVISIBLE
            displayAllItems()
        }

        sendRecipeToShopBtn.setOnClickListener {
            sendToShopList()
        }


    }

    private fun displayAllItems() {
        val recipeDetails = StringBuilder()
        recipeDetails.append("displaying all recipes now\n")
        // we can do this instead of coroutine becasue apparently of that LiveData thing
        // UPDATE 5/6/2024: Thong here, since we got rid of that live data thing, we will need to use coroutine
        CoroutineScope(Dispatchers.IO).launch {
            val allRecipes = recipeDao.getAllRecipes()
            for (recipe in allRecipes) {
                recipeDetails.append("Recipe ID: ${recipe.recipeId},\nName: ${recipe.name}\n\n")
            }

            withContext(Dispatchers.Main){
                viewRecipeTextview.text = recipeDetails.toString()
            }
        }
//        val allRecipes = recipeDao.getAllRecipes()


    }

    private fun displayOneRecipe() {
//        var ingredients = mutableListOf<Ingredient>()

        val recipeName = recipeNameTofindEdittext.text.toString().trim()

        val displayText = StringBuilder()
        CoroutineScope(Dispatchers.IO).launch {
            if (recipeName.isNotBlank()) {
                val recipe = recipeDao.findRecipeByName(recipeName)
                if (recipe != null) {
                    displayText.append("Recipe ID: ${recipe.recipeId}, \nName: ${recipe.name},\nInstruction: ${recipe.instruction}, \nIngredients:\n")

                    // this one returns a list of ingredients i believe
                    val ingredients = recipeItemDao.getIngredientsForRecipe(recipe.recipeId)

                    // this function jointostring take wahatever mess that is and join it into a nice nifty lil string
                    // seperated by the thing in the ()
                    val ingredientText = ingredients.joinToString("\n") { "- ${it.name}, ${it.amount} ${it.unit}" }
                    displayText.append(ingredientText)
                    displayText.append("\n\n")

                    withContext(Dispatchers.Main) {
                        sendRecipeToShopBtn.visibility  = View.VISIBLE
                        viewRecipeTextview.text = displayText
                    }

                } else {
                    withContext(Dispatchers.Main) {
                        sendRecipeToShopBtn.visibility  = View.INVISIBLE
                        viewRecipeTextview.text = "Recipe not found"
                    }
                }

            } else {
                sendRecipeToShopBtn.visibility  = View.INVISIBLE
                viewRecipeTextview.text = "you must enter the name"
            }

        }

    }


    private fun sendToShopList(){
        val recipeName = recipeNameTofindEdittext.text.toString().trim()

        var displayText = StringBuilder()
        if (recipeName.isNotBlank()) {
            CoroutineScope(Dispatchers.IO).launch {
                val recipe = recipeDao.findRecipeByName(recipeName)

                // if recipe is found, take the ingredient list and add it to shopping list
                // shoppinglist item requires: name (ez to get), checkbox (default false), notes (fill: from which recipes), quantity (from recipeItem)
                // recipe items can infer: item name, unit, category
                if (recipe != null) {


                    // code to add to shopping list
                    // this one retrieves the list of ingredients
                    val ingredients = recipeItemDao.getIngredientsForRecipe(recipe.recipeId)

                    // loop to go throguh the lsit of ingredients
                    for (ingredient in ingredients){
                        // see if item already exists in the shopping list yet right now.
                        val existingItem = shoppingListItemDao.findShoppingListItemByName(ingredient.name)
                        // item is already in the shoppinglist
                        if (existingItem != null) {
// Item with the same name already exists, update the quantity
                            val updatedQuantity = existingItem.quantity + ingredient.amount
                            existingItem.quantity = updatedQuantity
                            shoppingListItemDao.updateShoppingListItem(existingItem)
                        }
                        else{
                            val notes = "Added from $recipeName recipe"
                            val shoppingListItem = ShoppingListItem(
                                itemName = ingredient.name,
                                quantity = ingredient.amount,
                                checkbox = false,
                                notes = notes
                            )
                            shoppingListItemDao.insertShoppingListItem(shoppingListItem)
                        }


                        displayText.append("- ${ingredient.name} : ${ingredient.amount} ${ingredient.unit}\n")
                    }

                    withContext(Dispatchers.Main){
                        viewRecipeTextview.text = "Recipe found. adding these to shopping list:\n" +
                                "$displayText"

                    }


                }
                else {
                    withContext(Dispatchers.Main){
                        sendRecipeToShopBtn.visibility  = View.INVISIBLE
                        viewRecipeTextview.text = "Recipe not found"
                    }
                }
            }
        }
        else {
            sendRecipeToShopBtn.visibility  = View.INVISIBLE
            viewRecipeTextview.text = "you must enter the name"
        }
    }
}
