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
                // Assuming "get-ingredients" is the event. Modify as needed.
                mSocket.emit("get-ingredients", url)
            } else {
                // In input is empty (FOR TESTING PURPOSES)
                mSocket.emit("get-ingredients", "https://www.budgetbytes.com/homemade-meatballs/")
            }
        }
        // Establish a connection for the socket answer
        setupSocketListeners()
    }

    private fun setupSocketListeners() {
        mSocket.on("ingredients-result") { args ->
            // Since we're only logging, no need to run on the UI thread
            val ingredientsJson = args[0]?.toString()
            try {
                val jsonArray = JSONArray(ingredientsJson)
                val ingredientsList = StringBuilder()
                for (i in 0 until jsonArray.length()) {
                    val ingredient = jsonArray.getJSONObject(i)
                    // Extract "Amount", "Unit", and "Name" and append them to the StringBuilder
                    val amount = ingredient.getString("Amount")
                    val unit = ingredient.getString("Unit")
                    val name = ingredient.getString("Name")
                    ingredientsList.append("Amount: $amount, Unit: $unit, Name: $name\n")
                }
                // Log the ingredients list to Logcat
                Log.d("IngredientsResult", ingredientsList.toString())
            } catch (e: JSONException) {
                e.printStackTrace()
                Log.d("IngredientsError", "Error parsing ingredients")
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        // Clean up the binding
        _binding = null
    }
}
