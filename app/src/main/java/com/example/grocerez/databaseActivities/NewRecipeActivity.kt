package com.example.grocerez.databaseActivities

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.grocerez.MainActivity
import com.example.grocerez.R
import com.example.grocerez.dao.CategoryDao
import com.example.grocerez.dao.ItemDao
import com.example.grocerez.dao.RecipeDao
import com.example.grocerez.dao.RecipeItemDao
import com.example.grocerez.dao.UnitDao
import com.example.grocerez.data.Ingredient
import com.example.grocerez.data.model.Category
import com.example.grocerez.data.model.Item
import com.example.grocerez.data.model.Recipe
import com.example.grocerez.data.model.RecipeItem
import com.example.grocerez.data.model.Unit
import com.example.grocerez.database.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.StringBuilder

class NewRecipeActivity : AppCompatActivity() {

    private lateinit var recipeNameEditText: EditText
    private lateinit var recipeInstructionEditText: EditText
    private lateinit var ingredientNameEditText: EditText
    private lateinit var ingredientAmtEditText: EditText
    private lateinit var ingredientCategoryEditText: EditText
    private lateinit var ingredientUnitEditText: EditText

    private lateinit var findItemBtn: Button
    private lateinit var addItem2RecipeBtn: Button
    private lateinit var addRecipeBtn: Button

    private lateinit var recipeTextView: TextView

    private lateinit var categoryDao: CategoryDao
    private lateinit var unitDao: UnitDao
    private lateinit var itemDao: ItemDao
    private lateinit var recipeDao: RecipeDao
    private lateinit var recipeItemDao: RecipeItemDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_new_recipe)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        //_______________________________________DATABASE______________________________
        val appDatabase = AppDatabase.getInstance(applicationContext)
        categoryDao = appDatabase.categoryDao()
        unitDao = appDatabase.unitDao()
        itemDao = appDatabase.itemDao()
        recipeDao = appDatabase.recipeDao()
        recipeItemDao = appDatabase.recipeItemDao()

        //________________________________________BINDINGS_________________________________
        recipeNameEditText = findViewById(R.id.recipeNameEditText)
        recipeInstructionEditText = findViewById(R.id.recipeInstructionEditText)

        //external items_______________________________________________________________
        ingredientNameEditText = findViewById(R.id.ingredientNameEditText)
        ingredientAmtEditText = findViewById(R.id.ingredientAmountEditText)
        ingredientCategoryEditText = findViewById(R.id.ingredientCategoryEditText)
        ingredientUnitEditText = findViewById(R.id.ingredientUnitEditText)

        //buttons
        findItemBtn = findViewById(R.id.recipeFindItemButton)
        addItem2RecipeBtn = findViewById(R.id.recipeAddItemButton)
        addRecipeBtn = findViewById(R.id.addRecipeToDatabase)

        // textview
        recipeTextView = findViewById(R.id.recipePageTextView)
        // ________________________________________ BINDINGS end_________________________________


        //___________________________onclick listensers
        findItemBtn.setOnClickListener {
            searchItemAndPopulateFields()
        }

        // make a temporary list to put the ingredients in before adding it to database
        val temporaryIngredientList = mutableListOf<Ingredient>()

        /// when press add item to recipe button,
        // first make sure text fields are not empty, if itt is require it filled
        // then make sure item exists in the items table, if not add it.
        // then add it to a temporary list.
        // at the end, add the association in the association table.
        addItem2RecipeBtn.setOnClickListener {
            addItemToRecipe(temporaryIngredientList)
        }

        addRecipeBtn.setOnClickListener {
//            var output_string = StringBuilder()
//            println("checking stuff")
//            temporaryIngredientList.forEach { ingredient ->
//                // Access and display each field of the Ingredient object
//                val ingredientText = "Name: ${ingredient.name}\n" +
//                        "Amount: ${ingredient.amount}\n" +
//                        "Category: ${ingredient.category}\n" +
//                        "Unit: ${ingredient.unit}\n\n"
//                output_string.append(ingredientText)
//
//            }
//            recipeTextView.text = "list of ingredients: \n"+output_string
            val recipeName = recipeNameEditText.text.toString().trim()
            val recipeInstructions = recipeInstructionEditText.text.toString().trim()

            addNewRecipe(recipeName, recipeInstructions, temporaryIngredientList)
        }

    }

    private fun addItemToRecipe(temporaryIngredientList: MutableList<Ingredient>){
        val ingredientName = ingredientNameEditText.text.toString().trim()
        val ingredientAmount = ingredientAmtEditText.text.toString().trim().toFloatOrNull() ?: 1.0f
        val ingredientCategory = ingredientCategoryEditText.text.toString().trim()
        val ingredientUnit = ingredientUnitEditText.text.toString().trim()


        val areAllFieldsFilled = ingredientName.isNotBlank()
                && ingredientAmount!=null
                && ingredientCategory.isNotBlank()
                && ingredientUnit.isNotBlank()
        // if fields are all filled:
        if (areAllFieldsFilled){
            // this function checks to see if everything exists yet,
            // if not, will add to item table and handles all of item's foreign key to make sure it exists
            addToItemTable(ingredientName,ingredientCategory,ingredientUnit)
            val ingredient =
                Ingredient(ingredientName, ingredientAmount, ingredientCategory, ingredientUnit)

            // add item to temp list
            temporaryIngredientList.add(ingredient)
            recipeTextView.text = "added to temp list"

            // clearing field for next ingredient
            ingredientNameEditText.text.clear()
            ingredientAmtEditText.text.clear()
            ingredientCategoryEditText.text.clear()
            ingredientUnitEditText.text.clear()

        } else{
            recipeTextView.text = "please fill out all fields"

            // toast is the pop up box that shows up briefly, has context, message, lengthshort is the duration
            Toast.makeText(this, "Please fill in all fields with valid data", Toast.LENGTH_SHORT).show()
        }
        return
    }

    private fun searchItemAndPopulateFields() {
        val itemName = ingredientNameEditText.text.toString().trim()
        // if variable is empty, stop here
        if (itemName.isEmpty()) {
            recipeTextView.text = "Please enter a name"
            return
        }
        // Perform a database query to search for the item by name
        CoroutineScope(Dispatchers.IO).launch {
            try{
                val item = itemDao.findItemByName(itemName)

                // Update UI if object found and catch db error
                withContext(Dispatchers.Main) {
                    if (item != null) {
                        // Item found, update UI
                        ingredientCategoryEditText.setText(item.category)
                        ingredientUnitEditText.setText(item.unitName)

                        recipeTextView.text = "item found: ${item.name} \n" +
                                "ID: ${item.item_id}\n" +
                                "category: ${item.category}\n" +
                                "unit: ${item.unitName}\n" +
                                "use rate: ${item.useRate} "

                    } else {
                        recipeTextView.text = "item not found"
                    }
                }

            } catch (e: Exception){
                recipeTextView.text = "Error: ${e.message}"
            }
        }
    }

    private fun addToItemTable(itemName: String, category: String, unit: String){
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val existingCategory = categoryDao.findCategoryByName(category)
                val existingUnit = unitDao.findUnitByName(unit)

                if (existingCategory == null || existingUnit == null){
                    withContext(Dispatchers.Main){
                        recipeTextView.text = "category or unit does not exist yet. now adding \n"
                        val newCategory = Category(category)
                        val newUnit = Unit(unit)
                        categoryDao.insertCategory(newCategory)
                        unitDao.insertUnit(newUnit)

                    }



                    withContext(Dispatchers.Main){
                        recipeTextView.append("\n new category or unit is inserted \n")
                    }
                    val newItem = Item(name = itemName, category = category, unitName = unit, useRate = 0.0f)
                    itemDao.insertItem(newItem)
                } else {
                    val newItem = Item(name = itemName, category = category, unitName = unit, useRate = 0.0f)
                    itemDao.insertItem(newItem)
                }
                // we can do this because the respective DAO objects does replace on conflict

            }catch (e: Exception){
                recipeTextView.text = "adding item to item table Error: ${e.message}"
            }
        }
    }

    private fun addNewRecipe(recipeName: String, recipeInstructions: String,  temporaryIngredientList: MutableList<Ingredient>){
        val newRecipe = Recipe(name = recipeName, instruction = recipeInstructions)

        var areAllRequiredFieldsFilled = recipeName.isNotBlank()

        if (!areAllRequiredFieldsFilled){
            recipeTextView.text = "one of the required field is missing"
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Call the insertRecipe function from RecipeDao
                recipeDao.insertRecipe(newRecipe)

                // Recipe inserted successfully
                // After inserting, retrieve it immediately so we can get ID
                val insertedRecipe = recipeDao.findRecipeByName(recipeName)
                var outputText = StringBuilder()
                if (insertedRecipe != null){
                    outputText.append("Inserted Recipe: ${insertedRecipe.name} \n ingredient list:\n")


                    for (ingredient in temporaryIngredientList){
                        val ingredient_name = ingredient.name
                        val item = itemDao.findItemByName(ingredient_name)

                        if (item != null){
                            val recipeItem = RecipeItem(
                                recipeId = insertedRecipe.recipeId,
                                itemId = item.item_id,
                                amount = ingredient.amount
                            )

                            recipeItemDao.insertRecipeItem(recipeItem)
                            outputText.append(ingredient_name + "\n")
                        }


                    }
                    withContext(Dispatchers.Main){
                        recipeTextView.text = outputText
                    }
                } else {
                    recipeTextView.text = "something wrong with inserted recipe"
                }


            } catch (e: Exception) {
                withContext(Dispatchers.Main){
                    recipeTextView.text = "inside catch block, something wrong with insert recipe" + e.toString()
                }
                // Handle any exceptions or errors here
                Log.e("error", "Error inserting recipe: ${e.message}")
            }
        }

    }
}