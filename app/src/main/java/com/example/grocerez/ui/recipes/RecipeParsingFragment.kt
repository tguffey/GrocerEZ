package com.example.grocerez.ui.recipes
// Added to implement SocketHandler function
import com.example.grocerez.SocketHandler


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.grocerez.databinding.RecipeParsingBinding
import io.socket.client.Socket
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class RecipeParsingFragment : Fragment() {

    private var _binding: RecipeParsingBinding? = null
    private val binding get() = _binding!!
    // For accessing the backend sockets
    private lateinit var mSocket: Socket


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
            // JSON DATA TEST
            Log.d("RecipeDataJSON", "Received JSON: $recipeDataJson")
            try {
                // Initialize a JSONObject with the string
                val jsonObject = JSONObject(recipeDataJson)

                // Get the recipe name and format it into a list
                // This is currently done to easily match the format of the other data
                val recipeNameJsonArray = jsonObject.getJSONArray("recipeName")
                val recipeNameList = StringBuilder("Recipe Name:\n")
                for (i in 0 until recipeNameJsonArray.length()) {
                    val recipeName = recipeNameJsonArray.optString(i)
                    recipeNameList.append("$recipeName\n")
                }

                // Get the ingredients and format them as a list
                val ingredientsJsonArray = jsonObject.getJSONArray("ingredients")
                val ingredientsList = StringBuilder("Ingredients:\n")
                for (i in 0 until ingredientsJsonArray.length()) {
                    val ingredient = ingredientsJsonArray.getJSONObject(i)
                    val amount = ingredient.optString("Amount")
                    val unit = ingredient.optString("Unit")
                    val name = ingredient.optString("Name")
                    ingredientsList.append("Amount: $amount, Unit: $unit, Name: $name\n")
                }

                // Get the instructions and format them as a list
                val instructionsJsonArray = jsonObject.getJSONArray("instructions")
                val instructionsList = StringBuilder("\nInstructions:\n")
                for (i in 0 until instructionsJsonArray.length()) {
                    val step = instructionsJsonArray.optString(i)
                    instructionsList.append("Step ${i + 1}: $step\n")
                }


                val recipeData = recipeNameList.append(ingredientsList).append(instructionsList)


                // Log the combined result to Logcat
                Log.d("RecipeDataResult", recipeData.toString())

                // Update the TextView to display both ingredients and instructions
                activity?.runOnUiThread {
                    binding.textViewResult.text = recipeData.toString()
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

    }




    override fun onDestroyView() {
        super.onDestroyView()
        // Clean up the binding
        _binding = null
    }
}
