package com.example.grocerez

import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.grocerez.databinding.ActivityMainBinding
import android.widget.Button
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView
        //I added the line below to change the nav color to green001
        navView.setBackgroundColor(resources.getColor(R.color.green001))


        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_myplate, R.id.navigation_dashboard, R.id.navigation_home,
                R.id.navigation_recipes, R.id.navigation_shopping
            )
        )
        //THIS IS JOCELYN IM DELETING THIS
        //setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        // The following lines connects the Android app to the server.
        SocketHandler.setSocket()
        SocketHandler.establishConnection()
        // Vals to connect xml object ID's
        val counterBtn = findViewById<Button>(R.id.counterBtn)
        val countTextView = findViewById<TextView>(R.id.countTextView)
        val getTestBtn = findViewById<Button>(R.id.getTestBtn)
        val postTestBtn = findViewById<Button>(R.id.postTestBtn)
        val sqlSelectAllButton = findViewById<Button>(R.id.sqlSelectAllBtn)

        val mSocket = SocketHandler.getSocket()

        counterBtn.setOnClickListener{
            mSocket.emit("counter")
        }

        getTestBtn.setOnClickListener{
            mSocket.emit("hello")
        }

        postTestBtn.setOnClickListener {
            mSocket.emit("hello_post")
        }
        sqlSelectAllButton.setOnClickListener {
            mSocket.emit("sql_query")
        }

        mSocket.on("counter") { args ->
            if (args[0] != null) {
                val counter = args[0] as Int
                runOnUiThread {
                    countTextView.text = counter.toString()
                }
            }
        }

        mSocket.on("hello") { args ->
            val helloMessage = args[0] as String
            runOnUiThread {
                // Update your UI or handle the 'hello' response as needed
                countTextView.text = helloMessage
            }
        }

        // Handle 'hello_post' event
        mSocket.on("hello_post") { args ->
            val postMessage = args[0] as String
            runOnUiThread {
                // Update your UI or handle the 'hello_post' response as needed
                countTextView.text = postMessage
            }
        }
        // Handle 'sql_result' event
        mSocket.on("sql_result") { args ->
            val result = args[0]
            runOnUiThread {
                // Check if the result contains an error
                if (result is Map<*, *> && result.containsKey("error")) {
                    // Handle the error, update UI, etc.
                    countTextView.text = "Database query failed: ${result["error"]}"
                } else {
                    // Process the result and update UI as needed
                    countTextView.text = "Received database query result: $result"
                }
            }
        }
    }
}