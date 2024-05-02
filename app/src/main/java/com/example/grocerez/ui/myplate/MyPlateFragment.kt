package com.example.grocerez.ui.myplate

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.grocerez.databinding.FragmentMyplateBinding
import com.anychart.AnyChart
import com.anychart.AnyChartView
import com.anychart.chart.common.dataentry.DataEntry
import com.anychart.chart.common.dataentry.ValueDataEntry
import com.example.grocerez.R
import com.example.grocerez.ui.settings.SettingsActivity

class MyPlateFragment : Fragment(){

    private var _binding: FragmentMyplateBinding? = null
    private lateinit var anyChartView: AnyChartView

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    // Initialize shared view model
    private val sharedModel: MyPlateViewModel by activityViewModels()
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyplateBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Find the settings button in the layout
        val createMyPlate: Button = root.findViewById(R.id.btn_createmyplate)

        // Set a click listener for the settings button
        createMyPlate.setOnClickListener {
            // Handle button click here
            findNavController().navigate(R.id.action_myPlateFragment_to_myPlateSettingsFragment)
        }

        // Observe shared view model data
        sharedModel.foodAmounts.observe(viewLifecycleOwner) { foodAmounts ->

            //RECYCLER VIEW
            val foodAmountsArray = arrayOf(
                foodAmounts.fruitAmount,
                foodAmounts.vegetableAmount,
                foodAmounts.grainAmount,
                foodAmounts.proteinAmount,
                foodAmounts.dairyAmount
            )

            /*IM WRITING THE CODE HERE FOR THE RECYCLER VIEW LOGIC
            * To access updated food amounts, use foodAmounts.<categoryAmount>*/
            // Create an ArrayList of FoodAmountsModel
            recyclerView=root.findViewById(R.id.food_recommendations)
            val foodAmountsModelList = ArrayList<FoodAmountModel>()

            val categoryDescription = resources.getStringArray(R.array.myplate_descriptions)
            val categoryImages = intArrayOf(
                R.drawable.fruits_icon,
                R.drawable.vegetables_icon,
                R.drawable.grains_icon,
                R.drawable.protein_icon,
                R.drawable.dairy_icon
            )

            for (i in foodAmountsArray.indices) {
                val foodAmountModel = FoodAmountModel(
                    foodAmountsArray[i].toDouble(), // Assuming foodAmountsArray contains Double values
                    categoryDescription[i],
                    categoryImages[i]
                )
                foodAmountsModelList.add(foodAmountModel)
            }

            // Setting up the adapter
            val adapter = MyPlateRecyclerAdapter(requireContext(), foodAmountsModelList)
            recyclerView.adapter = adapter
            recyclerView.layoutManager = LinearLayoutManager(requireContext())
            // After populating the RecyclerView, hide the settings button
            createMyPlate.visibility = View.GONE
            /*THE RECYCLER VIEW LOGIC WILL END HERE*/
            anyChartView = root.findViewById(R.id.anyChartView)
            setupChartView()
        }

        // Find the settings button in the layout
        val settingsButton: Button = root.findViewById(R.id.btn_settings)

        // Set a click listener for the settings button
        settingsButton.setOnClickListener {
            // Handle button click here
            findNavController().navigate(R.id.action_myPlateFragment_to_myPlateSettingsFragment)
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupChartView() {
        // I added a condition to check if the user is in dark or light mode
        val isDarkMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
        val pie = AnyChart.pie()

        // Set the start angle to -90 degrees
        pie.startAngle(-90)

        // Use the appropriate background color
        if (isDarkMode){
            pie.background().fill("#333333")
        } else {
            pie.background().fill("#FFFFFF")
        }

        // Define the colors
        val colors = listOf("#FF0000", "#00FF00", "#FFA500", "#800080")
        val items = listOf("Fruits", "Vegetables", "Grains", "Protein")
        val values = listOf("2.0", "2.5", "6.0", "5.5")

        // Create your data array
        val dataEntries: MutableList<DataEntry> = ArrayList()

        // Add data points
        for (i in items.indices) {
            val entry = ValueDataEntry(items[i], 1.0) // Set value to 1

            // Set the color for each entry
            entry.apply { setValue("fill", colors[i]) }
            pie.labels().format(values[i])
            dataEntries.add(entry)
        }
        // Set the data to the pie chart
        pie.data(dataEntries)
        // Set the data to the pie chart
        pie.data(dataEntries)

        // Customize label settings
        pie.labels().position("inside") // Set label position to inside the pie slices
        pie.labels().fontSize(14) // Set label font size
        pie.labels().fontColor("#FFFFFF") // Set label font color

        // Customize chart properties as needed
        pie.stroke("6px #F1F1F1")
//        pie.title("My Plate")
//        pie.padding(0, 0, 0, 0)

        // Set the chart to the AnyChartView
        anyChartView.setChart(pie)
    }
}
