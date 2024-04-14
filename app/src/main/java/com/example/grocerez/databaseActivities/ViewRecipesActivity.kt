package com.example.grocerez.databaseActivities

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.grocerez.R
import com.example.grocerez.dao.RecipeDao
import com.example.grocerez.dao.RecipeItemDao
import com.example.grocerez.data.Ingredient
import com.example.grocerez.database.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ViewRecipesActivity : AppCompatActivity() {
    private lateinit var recipeNameTofindEdittext: EditText
    private lateinit var findDisplayRecipeBtn: Button
    private lateinit var findDisplayAllRecipeBtn: Button
    private lateinit var viewRecipeTextview: TextView
    private lateinit var recipeDao: RecipeDao
    private lateinit var recipeItemDao: RecipeItemDao
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


        //_____________________________DB stuff
        val appDatabase = AppDatabase.getInstance(this)
        recipeDao = appDatabase.recipeDao()
        recipeItemDao = appDatabase.recipeItemDao()


        //____________________________-__BUTTONS
        findDisplayRecipeBtn.setOnClickListener {

            val recipeName = recipeNameTofindEdittext.text.toString().trim()

            displayOneRecipe()
        }
        findDisplayAllRecipeBtn.setOnClickListener {
            displayAllItems()
        }


    }

    private fun displayAllItems() {

        // we can do this instead of coroutine becasue apparently of that LiveData thing
        recipeDao.getAllRecipes().observe(this) { recipes ->
            val recipeDetails = StringBuilder()
            recipeDetails.append("displaying all recipes now\n")

            for (recipe in recipes) {
                recipeDetails.append("Recipe ID: ${recipe.recipeId},\nName: ${recipe.name}\n\n")
            }
            viewRecipeTextview.text = recipeDetails.toString()
        }

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


                    val ingredients = recipeItemDao.getIngredientsForRecipe(recipe.recipeId)

                    // this function jointostring take wahatever mess that is and join it into a nice nifty lil string
                    // seperated by the thing in the ()
                    val ingredientText = ingredients.joinToString("\n") { "- ${it.name}, ${it.amount} ${it.unit}" }
                    displayText.append(ingredientText)
                    displayText.append("\n\n")

                    withContext(Dispatchers.Main) {
                        viewRecipeTextview.text = displayText
                    }

                } else {
                    withContext(Dispatchers.Main) {
                        viewRecipeTextview.text = "Recipe not found"
                    }
                }

            } else {
                viewRecipeTextview.text = "you must enter the name"
            }

        }

    }
}
