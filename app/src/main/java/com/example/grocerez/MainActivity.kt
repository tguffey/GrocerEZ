package com.example.grocerez

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.grocerez.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import io.socket.client.Socket
import org.json.JSONObject
import android.widget.Button
import android.widget.TextView
import org.json.JSONArray
import org.json.JSONException

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var mSocket: Socket // Make sure Socket is correctly imported

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize SocketHandler and establish connection first
        SocketHandler.setSocket()
        SocketHandler.establishConnection()
        mSocket = SocketHandler.getSocket()
        mSocket.connect()

        // Setup the layout
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupNavigation()
        setupButtonsAndListeners()
    }

    private fun setupNavigation() {
        val navView: BottomNavigationView = binding.navView
        navView.setBackgroundColor(resources.getColor(R.color.green001))

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        val appBarConfiguration = AppBarConfiguration(
                setOf(
                        R.id.navigation_myplate, R.id.navigation_dashboard, R.id.navigation_home,
                        R.id.navigation_recipes, R.id.navigation_shopping
                )
        )
        navView.setupWithNavController(navController)
    }

    private fun setupButtonsAndListeners() {
        val counterBtn = findViewById<Button>(R.id.counterBtn)
        val countTextView = findViewById<TextView>(R.id.countTextView)
        val getTestBtn = findViewById<Button>(R.id.getTestBtn)
        val postTestBtn = findViewById<Button>(R.id.postTestBtn)
        val sqlSelectAllButton = findViewById<Button>(R.id.sqlSelectAllBtn)
        val scrapeIngredientsBtn = findViewById<Button>(R.id.scrapeIngredientsBtn)
        val nutrientTestBtn = findViewById<Button>(R.id.nutrientTestBtn)

        // Setup button listeners
        counterBtn.setOnClickListener { mSocket.emit("counter") }
        getTestBtn.setOnClickListener { mSocket.emit("hello") }
        postTestBtn.setOnClickListener { mSocket.emit("hello_post") }
        sqlSelectAllButton.setOnClickListener { mSocket.emit("sql_query") }
        scrapeIngredientsBtn.setOnClickListener {
            val url = "https://www.budgetbytes.com/homemade-meatballs/"
            mSocket.emit("get-ingredients", url)
        }
        nutrientTestBtn.setOnClickListener {
            val foodName = "Chicken"
            val quantity = 1
            val unit = "pound"
            mSocket.emit("get-nutritional-data", foodName, quantity, unit)
        }

        // Setup socket event listeners
        setupSocketListeners(countTextView)
    }

    private fun setupSocketListeners(countTextView: TextView) {
        mSocket.on("counter") { args ->
            runOnUiThread { countTextView.text = (args[0] as? Int)?.toString() ?: "Error" }
        }

        mSocket.on("hello") { args ->
            runOnUiThread { countTextView.text = args[0] as? String }
        }

        mSocket.on("hello_post") { args ->
            runOnUiThread { countTextView.text = args[0] as? String }
        }

        mSocket.on("sql_result") { args ->
            runOnUiThread {
                val result = args[0] as? String ?: "Error"
                countTextView.text = result
            }
        }

        mSocket.on("ingredients-result") { args ->
            runOnUiThread {
                // Attempt to parse the ingredients JSON array
                val ingredientsJson = args[0]?.toString()
                try {
                    // Create a JSON array to store ingredients data
                    val jsonArray = JSONArray(ingredientsJson)
                    // For turning the data into a string to be read by kotlin
                    val ingredientsList = StringBuilder()
                    for (i in 0 until jsonArray.length()) {
                        val ingredient = jsonArray.getJSONObject(i)
                        // The web ingredients are passed as "Amount", "Unit", and "Name"
                        // Grab each object with the "Name" value
                        val unit = ingredient.getString("Unit")
                        ingredientsList.append(unit).append("\n")
                        //val name = ingredient.getString("Name")
                        //ingredientsList.append(name).append("\n")
                    }
                    countTextView.text = ingredientsList.toString()
                } catch (e: JSONException) {
                    e.printStackTrace()
                    countTextView.text = "Error parsing ingredients"
                }
            }
        }

        // Socket function for grabbing nutritional data
        mSocket.on("nutritional-data-result") { args ->
            runOnUiThread {
                try {
                    val nutritionalJson = args[0]?.toString() ?: return@runOnUiThread
                    val jsonObject = JSONObject(nutritionalJson)

                    val description = jsonObject.getString("description")
                    val myPlateCategory = jsonObject.getString("myPlateCategory")
                    val nutrients = jsonObject.getJSONObject("nutrients")
                    val nutritionalOutput = StringBuilder()
                    nutritionalOutput.append("$description\nMyPlate Category: $myPlateCategory\n")

                    // Iterate over the keys of the nutrients JSONObject
                    val keysIterator = nutrients.keys()
                    while (keysIterator.hasNext()) {
                        val key = keysIterator.next() as String // Explicitly cast key to String
                        val value = nutrients.getString(key) // Now safe to use getString with key
                        nutritionalOutput.append("$key: $value\n")
                    }

                    countTextView.text = nutritionalOutput.toString()

                } catch (e: JSONException) {
                    e.printStackTrace()
                    countTextView.text = "Error parsing nutritional data"
                }
            }
        }



    }
}
