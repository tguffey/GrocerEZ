package com.example.grocerez.ui.recipes
// Added to implement SocketHandler function


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.grocerez.SocketHandler
import com.example.grocerez.data.RecipeRepository
import com.example.grocerez.data.model.Category
import com.example.grocerez.data.model.Item
import com.example.grocerez.data.model.Recipe
import com.example.grocerez.data.model.RecipeItem
import com.example.grocerez.data.model.Unit
import com.example.grocerez.database.AppDatabase
import com.example.grocerez.databinding.RecipeParsingBinding
import io.socket.client.Socket
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject

class RecipeParsingFragment : Fragment() {

    private var _binding: RecipeParsingBinding? = null
    private val binding get() = _binding!!
    // For accessing the backend sockets
    private lateinit var mSocket: Socket
    private lateinit var recipeViewModel: RecipesViewModel
    private lateinit var newRecipe: Recipe


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Initialize SocketHandler and establish connection first
        SocketHandler.setSocket()
        SocketHandler.establishConnection()
        mSocket = SocketHandler.getSocket()
        mSocket.connect()

        val appDatabase = AppDatabase.getInstance(requireContext())

        recipeViewModel = ViewModelProvider(this.requireActivity(),
            RecipesViewModel.RecipeModelFactory(
                RecipeRepository(
                    categoryDao = appDatabase.categoryDao(),
                    itemDao = appDatabase.itemDao(),
                    recipeDao = appDatabase.recipeDao(),
                    recipeItemDao = appDatabase.recipeItemDao(),
                    unitDao = appDatabase.unitDao()
                )
            )).get(RecipesViewModel::class.java)

        recipeViewModel.loadRecipes()

        // Inflate the layout for this fragment using view binding
        _binding = RecipeParsingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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

        // Establish a connection for the socket answer
        setupSocketListeners()
    }

    private fun setupSocketListeners() {
        // Function for getting ingredients and recipe steps
        mSocket.on("ingredients-result") { args ->
            val recipeDataJson = args[0]?.toString()
            try {
                // Initialize a JSONObject with the string
                val jsonObject = JSONObject(recipeDataJson)

                // Get the instructions and format them as a list
                val instructionsJsonArray = jsonObject.getJSONArray("instructions")
                val instructionsList = StringBuilder("\nInstructions:\n")
                for (i in 0 until instructionsJsonArray.length()) {
                    val step = instructionsJsonArray.optString(i)
                    instructionsList.append("Step ${i + 1}: $step\n")
                }

                val recipe = Recipe(
                    name = "Meatballs",
                    instruction = instructionsList.toString()
                )


                CoroutineScope(Dispatchers.Main).launch{
                    recipeViewModel.addRecipes(recipe)
                    newRecipe = recipeViewModel.findRecipeByName(recipe.name)!!
                }

                // Get the ingredients and format them as a list
                val ingredientsJsonArray = jsonObject.getJSONArray("ingredients")
                val ingredientsList = StringBuilder("Ingredients:\n")
                for (i in 0 until ingredientsJsonArray.length()) {
                    val ingredient = ingredientsJsonArray.getJSONObject(i)
                    val amount = convertFractionToDecimal(ingredient.optString("Amount"))
                    val unit = checkUnit(ingredient.optString("Unit"))
                    val name = ingredient.optString("Name")

                    CoroutineScope(Dispatchers.Main).launch{
                        val category = recipeViewModel.findCategoryByName("meatball ingredients")
                        val newCategory = Category(
                            name = "meatball ingredients"
                        )
                        if (category == null){
                            recipeViewModel.addCategory(newCategory)
                        }

                        val unitTest = recipeViewModel.findUnitByName(unit)
                        val newUnit = Unit(
                            name = unit
                        )
                        if (unitTest == null){
                            recipeViewModel.addUnit(newUnit)
                        }

                        val item = recipeViewModel.findItemByName(name)
                        val newItem = Item(
                            name = name,
                            unitName = unit,
                            category = "meatball ingredients",
                            useRate = 0.0f)
                        if (item == null){
                            recipeViewModel.addItem(newItem)
                        }
                        val searchItem = recipeViewModel.findItemByName(name)
                        val newRecipeItem = RecipeItem(
                            amount = amount.toFloat(),
                            itemId = searchItem!!.item_id,
                            recipeId = newRecipe.recipeId,
                        )
                        recipeViewModel.addRecipeItems(newRecipeItem)
                    }
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
            } catch (e: JSONException) {
                e.printStackTrace()
                Log.d("RecipeDataError", "Error parsing recipe data")
            }
        }
        // THIS FUNCTION VERSION IS FOR GETTING INGREDIENTS ONLY
        /*mSocket.on("ingredients-result") { args ->
            val recipeDataJson = args[0]?.toString()
            try {
                // Initialize a JSONObject with the string
                val jsonObject = JSONObject(recipeDataJson)

                // Get the ingredients put in an ingredient list string
                val ingredientsJsonArray = jsonObject.getJSONArray("ingredients")
                val ingredientsList = StringBuilder("Ingredients:\n")
                for (i in 0 until ingredientsJsonArray.length()) {
                    val ingredient = ingredientsJsonArray.getJSONObject(i)
                    val amount = ingredient.optString("Amount")
                    val unit = ingredient.optString("Unit")
                    val name = ingredient.optString("Name")
                    ingredientsList.append("Amount: $amount, Unit: $unit, Name: $name\n")
                }

                // Log the ingredients list to Logcat
                Log.d("RecipeDataResult", ingredientsList.toString())

                // Update the TextView to display the ingredients
                activity?.runOnUiThread {
                    binding.textViewResult.text = ingredientsList.toString()
                }
            } catch (e: JSONException) {
                e.printStackTrace()
                Log.d("RecipeDataError", "Error parsing recipe data")
            }
        }*/

        // Function for getting Nutritional Data from recipe ingredients
        // Currently has errors, need to change JSON type sent from backend
        /*mSocket.on("ingredients-result-for-nutritional-data") { args ->
            val nutritionalDataJson = args[0]?.toString() ?: throw IllegalArgumentException("First argument expected to be a non-null string")

            try {
                val jsonArray = JSONArray(nutritionalDataJson)
                val nutritionalDataList = StringBuilder("Nutritional Data:\n")
                for (i in 0 until jsonArray.length()) {
                    val foodItem = jsonArray.getJSONObject(i)
                    val name = foodItem.optString("Name", "Unknown Ingredient")
                    val description = foodItem.optString("description", "No description available")
                    val myPlateCategory = foodItem.optString("myPlateCategory", "Not classified")

                    nutritionalDataList.append("\n$name:\n")
                    nutritionalDataList.append("Description: $description\n")
                    nutritionalDataList.append("MyPlate Category: $myPlateCategory\n")

                    val nutrients = foodItem.optJSONObject("nutrients")
                    nutrients?.let {
                        for (key in it.keys()) {
                            val value = it.optString(key, "N/A")  // Safely extract string value
                            nutritionalDataList.append("$key: $value\n")
                        }
                    }
                }

                // Update the TextView to display nutritional data
                activity?.runOnUiThread {
                    binding.textViewResult.text = nutritionalDataList.toString()
                }
            } catch (e: JSONException) {
                e.printStackTrace()
                Log.d("NutritionalDataError", "Error parsing nutritional data")
            } catch (e: IllegalArgumentException) {
                e.printStackTrace()
                Log.d("SocketDataError", "Invalid data received from socket")
            }
        }*/

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
        // Clean up the binding
        _binding = null
    }
}
