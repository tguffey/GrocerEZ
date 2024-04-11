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

                // Get the instructions put in an instructions list string
                val instructionsJsonArray = jsonObject.getJSONArray("instructions")
                val instructionsList = StringBuilder("\nInstructions:\n")
                for (i in 0 until instructionsJsonArray.length()) {
                    // Ensure that each instruction is treated as a string
                    val step = instructionsJsonArray.optString(i)
                    instructionsList.append("Step ${i + 1}: $step\n")
                }

                // This combines both ingredients and instructions into one string list
                val combinedResult = ingredientsList.append(instructionsList.toString())

                // Log the combined result to Logcat
                Log.d("RecipeDataResult", combinedResult.toString())
            } catch (e: JSONException) {
                e.printStackTrace()
                Log.d("RecipeDataError", "Error parsing recipe data")
            }
        }
        // Function for getting ingredients only
        mSocket.on("ingredients-result-for-shopping-list") { args ->
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

                // Log the combined result to Logcat as a string
                Log.d("RecipeDataResult", ingredientsList.toString())
            } catch (e: JSONException) {
                e.printStackTrace()
                Log.d("RecipeDataError", "Error parsing recipe data")
            }
        }
        // Function for getting nutritional value from recipes
        /*mSocket.on("ingredients-result-for-nutritional-data") { args ->
            val nutritionalDataJson = args[0]?.toString()
            try {
                // Parse the JSON array received from the socket event
                val jsonArray = JSONArray(nutritionalDataJson)

                val nutritionalDataList = StringBuilder("Nutritional Data:\n")
                for (i in 0 until jsonArray.length()) {
                    val foodItem = jsonArray.getJSONObject(i)

                    val name = foodItem.optString("Name")
                    val description = foodItem.optString("description")
                    val myPlateCategory = foodItem.optString("myPlateCategory")
                    val nutrients = foodItem.optJSONObject("nutrients")

                    nutritionalDataList.append("Name: $name\n")
                    nutritionalDataList.append("Description: $description\n")
                    nutritionalDataList.append("MyPlate Category: $myPlateCategory\n")

                    nutrients?.let {
                        for (key in it.keys()) {
                            // Explicitly telling Kotlin that we expect a String value here.
                            // This approach forces a cast from Any? to String, which should be avoided without checks
                            val nutrientValue: Any? = it.get(key)
                            val value = nutrientValue?.toString() ?: "N/A" // Safely convert to String, providing a fallback
                            nutritionalDataList.append("$key: $value\n")
                        }
                    }

                    nutritionalDataList.append("\n") // Adds a newline for spacing between items
                }

                // Log the combined result to Logcat
                Log.d("NutritionalDataResult", nutritionalDataList.toString())
            } catch (e: JSONException) {
                e.printStackTrace()
                Log.d("NutritionalDataError", "Error parsing nutritional data")
            }
        }*/

    }




    override fun onDestroyView() {
        super.onDestroyView()
        // Clean up the binding
        _binding = null
    }
}
