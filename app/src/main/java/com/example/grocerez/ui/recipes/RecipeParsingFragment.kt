package com.example.grocerez.ui.recipes
// Added to implement SocketHandler function

import com.example.grocerez.SocketHandler
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.grocerez.data.model.Category
import com.example.grocerez.data.model.Item
import com.example.grocerez.data.model.Recipe
import com.example.grocerez.data.model.RecipeItem
import com.example.grocerez.data.model.Unit
import com.example.grocerez.databinding.RecipeParsingBinding
import io.socket.client.Socket
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class RecipeParsingFragment : Fragment() {

    private var _binding: RecipeParsingBinding? = null
    private val binding get() = _binding!!
    // For accessing the backend sockets
    private lateinit var mSocket: Socket
    private lateinit var recipeViewModel: RecipesViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Initialize SocketHandler and establish connection first
        SocketHandler.setSocket()
        SocketHandler.establishConnection()
        mSocket = SocketHandler.getSocket()
        mSocket.connect()

        // Inflate the layout for this fragment using view binding
        _binding = RecipeParsingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recipeViewModel = ViewModelProvider(this.requireActivity())[RecipesViewModel::class.java]

        // Initialize Socket instance
        mSocket = SocketHandler.getSocket()

        // Set OnClickListener for the back button
        binding.backButton.setOnClickListener {
            // Navigate back
            findNavController().popBackStack()
        }

        // Set OnClickListener for parsing button
        binding.parseButton.setOnClickListener {
            // Retrieve the link entered by the user
            val url = binding.linkEditText.text.toString()

            // Perform parsing with the link
            if (url.isNotEmpty()) {
                mSocket.emit("get-ingredients", url)
            } else {
                mSocket.emit("get-ingredients", "https://www.budgetbytes.com/homemade-meatballs/")
            }
        }

        // Set OnClickListener for add to shopping button
        binding.addToShoppingButton.setOnClickListener {
            // Retrieve the link entered by the user
            val url = binding.linkEditText.text.toString()

            // Perform parsing with the link
            if (url.isNotEmpty()) {
                mSocket.emit("get-ingredients-for-shopping-list", url)
            } else {
                mSocket.emit("get-ingredients-for-shopping-list", "https://www.budgetbytes.com/homemade-meatballs/")
            }
        }

        // Set OnClickListener for ingredients to nutritional data button
        binding.getNutriFromUrlButton.setOnClickListener {
            // Retrieve the link entered by the user
            val url = binding.linkEditText.text.toString()

            // Perform parsing with the link
            if (url.isNotEmpty()) {
                mSocket.emit("get-ingredients-for-nutritional-data", url)
            } else {
                mSocket.emit("get-ingredients-for-nutritional-data", "https://www.budgetbytes.com/homemade-meatballs/")
            }
        }

        // Set OnClickListener for barcode lookup button
        binding.barcodeLookupButton.setOnClickListener {
            // Retrieve the barcode entered by the user
            val barcode = binding.linkEditText.text.toString()

            // Perform parsing with the link
            if (barcode.isNotEmpty()) {
                mSocket.emit("get-barcode-info", barcode)
            } else {
                mSocket.emit("get-barcode-info", "0024100106851")
            }
        }

        // Set OnClickListener for view online recipes button
        binding.viewRecipesButton.setOnClickListener {
            // Retrieve the link entered by the user
            val url = binding.linkEditText.text.toString()

            // Send command to access recipes from online SQL database server
             mSocket.emit("get-server-recipes")
        }


        // These can be used when entering text to fields for uploading
        //val recipeName = binding.recipeNameEditText.text.toString()
        //val link = binding.linkEditText.text.toString()
        //val items = parseItems(binding.itemsEditText.text.toString())

        // Set OnClickListener for adding a recipe to the online SQL database
        binding.addRecipeButton.setOnClickListener {
            val recipeName = "TestR"
            val link = "TestL"
            val itemsJson = """
        [
            {"name": "TestItem1", "unit": "kg", "amount": "1"},
            {"name": "TestItem2", "unit": "kg", "amount": "2"}
        ]
    """.trimIndent()

            try {
                if (recipeName.isNotEmpty() && itemsJson.isNotEmpty()) {
                    Log.d("AddRecipe", "Sending recipe data: $recipeName, $link, $itemsJson")
                    mSocket.emit("add-recipe", recipeName, link, itemsJson)
                } else {
                    Log.d("RecipeDataError", "Recipe name or items are empty")
                }
            } catch (e: JSONException) {
                Log.e("RecipeDataError", "Error creating items JSON array: ${e.message}")
            }
        }

        // Establish a connection for the socket answer
        setupSocketListeners()
    }

    private fun setupSocketListeners() {
        // Function for getting ingredients and recipe steps
        mSocket.on("ingredients-result") { args ->
            val recipeDataJson = args[0]?.toString()
            // JSON DATA TEST
            Log.d("RecipeDataJSON", "Received JSON: $recipeDataJson")
            try {
                // Initialize a JSONObject with the string
                val jsonObject = JSONObject(recipeDataJson)

                // Get the recipe name and format it into a list
                // This is currently done to easily match the format of the other data
                val recipeNameJsonArray = jsonObject.getJSONArray("recipeName")
                val recipeNameList = StringBuilder("Recipe Name:\n")

                val recipeTitle = StringBuilder()
                for (i in 0 until recipeNameJsonArray.length()) {
                    val recipeName = recipeNameJsonArray.optString(i)
                    recipeTitle.append(recipeName)
                    recipeNameList.append("$recipeName\n")
                }

                // Get the instructions and format them as a list
                val instructionsJsonArray = jsonObject.getJSONArray("instructions")
                val instructionsList = StringBuilder("\nInstructions:\n")
                for (i in 0 until instructionsJsonArray.length()) {
                    val step = instructionsJsonArray.optString(i)
                    instructionsList.append("Step ${i + 1}: $step\n")
                }

                val recipe = Recipe(
                    name = recipeTitle.toString(),
                    instruction = instructionsList.toString()
                )

                CoroutineScope(Dispatchers.IO).launch{
                    try{
                        val recipeID = recipeViewModel.addRecipeAndGetId(recipe)
                        Log.d("threading", "we got the recipe ID: $recipeID")
                        var item: Item?
                        var newItem: Item
                        var newRecipeItem: RecipeItem

                        // Get the ingredients and format them as a list
                        val ingredientsJsonArray = jsonObject.getJSONArray("ingredients")
                        val ingredientsList = StringBuilder("Ingredients:\n")

                        var ingredient: JSONObject
                        var amount: String
                        var unit: String
                        var name: String
                        var itemID: Long

                        var category: String
                        var defaultUnit: String


                        for (i in 0 until ingredientsJsonArray.length()) {
                            ingredient = ingredientsJsonArray.getJSONObject(i)
                            amount = convertFractionToDecimal(ingredient.optString("Amount"))
                            unit = checkUnit(ingredient.optString("Unit"))
                            name = ingredient.optString("Name")

                            Log.d("threading", "getting ingredient from json: $amount, $unit, $name")

                            // Default category and unit to "uncategorized" and "count" if not provided
                            category = ingredient.optString("Category", "uncategorized")
                            defaultUnit = ingredient.optString("Unit", "count")


                            var existingCategory = recipeViewModel.findCategoryByName(category)
                            if (existingCategory == null){
                                val newCategory = Category(category)
                                recipeViewModel.insertCategory(newCategory)
                            }

                            var existingUnit = recipeViewModel.findUnitByName(unit)
                            if (existingUnit == null) {
                                var newUnit = Unit(unit)
                                recipeViewModel.insertUnit(newUnit)
                            }

                            newItem = Item(
                                name = name,
                                category = category,
                                unitName = unit,
                                useRate = 0.0f
                            )

                            Log.d("threading", "now adding item to the database")
                            itemID = recipeViewModel.addItemAndGetId(newItem)
                            Log.d("threading", "supposedly i have the item id now")

                            Log.d("threading", "check if ${newItem.name} is null or not")
//                            if (item == null ){
//                                recipeViewModel.addItem(newItem)
//                                item = recipeViewModel.findItemByName(name)
//                                Log.d("threading", "Item IS NULL (BAD)")
//                            }

                            Log.d("threading", "creating new Recipe item")
                            newRecipeItem = RecipeItem(
                                recipeId = recipeID,
                                itemId = itemID,
                                amount = amount.toFloat()
                            )

                            Log.d("threading", "created newRecipeItem object, " +
                                    "recipe id${newRecipeItem.recipeId}, " +
                                    "recipe itemid: ${newRecipeItem.itemId}, " +
                                    "amount: ${newRecipeItem.amount}")

                            recipeViewModel.addRecipeItems(newRecipeItem)

                            ingredientsList.append("Amount: $amount, Unit: $unit, Name: $name\n")
                        }

                        // Combine ingredients and instructions into one StringBuilder
                        ingredientsList.append(instructionsList)

                        // Log the combined result to Logcat
                        Log.d("RecipeDataResult", ingredientsList.toString())

                        // Update the TextView to display both ingredients and instructions
                        activity?.runOnUiThread {
                            binding.textViewResult.text = ingredientsList.toString()
                        }
                } catch (e: Exception) {
                        // Handle the exception here
                        Log.e("Error", "An error occurred: ${e.message}")
                        // Show a toast or a snackbar with the error message
                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                context,
                                "An error occurred: ${e.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            } catch (e: JSONException) {
                e.printStackTrace()
                Log.d("RecipeDataError", "Error parsing recipe data")
            }
        }

        // Function for getting Nutritional Data from recipe ingredients
        // Currently has errors, need to change JSON type sent from backend
        mSocket.on("ingredients-result-for-nutritional-data") { args ->
            val nutritionalDataJson = args[0]?.toString() ?: throw IllegalArgumentException("First argument expected to be a non-null string")
            // JSON DATA TEST
            Log.d("NutriDataJSON", "Received JSON: $nutritionalDataJson")

            try {
                val jsonArray = JSONArray(nutritionalDataJson)
                val nutritionalDataList = StringBuilder("Nutritional Data:\n")
                for (i in 0 until jsonArray.length()) {
                    val foodItem = jsonArray.getJSONObject(i)
                    val name = foodItem.optString("Name", "Unknown Ingredient")
                    val description = foodItem.optString("Description", "No description available")
                    val myPlateCategory = foodItem.optString("MyPlateCategory", "Not classified")

                    nutritionalDataList.append("\n$name:\n")
                    nutritionalDataList.append("Description: $description\n")
                    nutritionalDataList.append("MyPlate Category: $myPlateCategory\n")

                    val nutrients = foodItem.optJSONObject("Nutrients")
                    if (nutrients != null) {
                        for (key in nutrients.keys()) {
                            var value = nutrients.optString(key.toString(), "N/A")
                            // Check for "NaN" values and replace them with "Not available"
                            if (value.contains("NaN")) {
                                value = "Not available"
                            }
                            nutritionalDataList.append("$key: $value\n")
                        }
                    } else {
                        nutritionalDataList.append("Nutrients: Not Available\n")
                    }
                }

                // Update the TextView to display nutritional data
                activity?.runOnUiThread {
                    binding.textViewResult.text = nutritionalDataList.toString()
                }
            } catch (e: JSONException) {
                e.printStackTrace()
                Log.d("NutritionalDataError", "Error parsing nutritional data: ${e.message}")
            } catch (e: IllegalArgumentException) {
                e.printStackTrace()
                Log.d("SocketDataError", "Invalid data received from socket: ${e.message}")
            }
        }


        // Function for grabbing barcode data
        mSocket.on("productInfo") { args ->
            val productName = args[0]?.toString() ?: "Unknown product"
            try {
                // Log the product name to Logcat
                Log.d("ProductInfoResult", productName)

                // Update the TextView to display the product name
                activity?.runOnUiThread {
                    binding.textViewResult.text = productName
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Log.d("ProductInfoError", "Error displaying product info")
            }
        }

        // Function for getting recipes from online SQL database
        // Function for getting recipes from the server
        mSocket.on("server-recipes-result") { args ->
            if (args.isNotEmpty()) {
                val recipesJson = args[0]?.toString()
                Log.d("RecipeDataJSON", "Received JSON: $recipesJson")
                try {
                    val recipesArray = JSONArray(recipesJson)
                    val recipeMap = mutableMapOf<Int, Pair<String, MutableList<String>>>()

                    // Aggregate recipes and their items
                    for (i in 0 until recipesArray.length()) {
                        val recipe = recipesArray.getJSONObject(i)
                        val recipeId = recipe.getInt("recipe_id")
                        val recipeName = recipe.optString("recipe_name", "No Name")
                        val link = recipe.optString("link", "No Link Provided")
                        val itemName = recipe.optString("item_name", "No Name")
                        val amount = recipe.optInt("amount", 0)
                        val unit = recipe.optString("unit", "No Unit")
                        val itemDescription = " - $itemName: $amount $unit"


                        recipeMap.putIfAbsent(recipeId, Pair(recipeName + "\nLink: " + (link ?: "No Link"), mutableListOf()))
                        recipeMap[recipeId]?.second?.add(itemDescription)
                    }

                    // Build the display string
                    val recipesList = StringBuilder("Recipes:\n")
                    recipeMap.forEach { (id, pair) ->
                        recipesList.append("\n${pair.first}\nItems:\n${pair.second.joinToString("\n")}\n")
                    }


                    // Update the TextView to display the recipes
                    activity?.runOnUiThread {
                        binding.textViewResult.text = recipesList.toString()
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                    Log.d("RecipeDataError", "Error parsing recipe data: ${e.message}")
                }
            }
        }

        mSocket.on("add-recipe-result") { args ->
            val resultJson = args[0]?.toString()
            try {
                val result = JSONObject(resultJson)
                val message = result.optString("message", "Unknown result")
                Log.d("AddRecipeResult", message)
                activity?.runOnUiThread {
                    binding.textViewResult.text = message
                }
            } catch (e: JSONException) {
                e.printStackTrace()
                Log.d("AddRecipeError", "Error parsing add recipe result")
            }
        }
    }

    private fun convertFractionToDecimal(fraction: String): String {
        if (!fraction.contains("/")) {
            // If the string does not contain a fraction, return it as is
            return fraction
        }

        val parts = fraction.split("/")
        if (parts.size != 2) {
            // Invalid fraction format, return original string
            return fraction
        }

        try {
            val numerator = parts[0].toDouble()
            val denominator = parts[1].toDouble()

            // Calculate decimal value
            val decimalValue = numerator / denominator

            // Format decimal value to two decimal places
            return String.format("%.2f", decimalValue)
        } catch (e: NumberFormatException) {
            // Error occurred during conversion, return original string
            return fraction
        }
    }

    private fun checkUnit(unit: String): String {
        return if (unit.isEmpty()) {
            "count"
        } else {
            unit
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}