package com.example.grocerez.ui.myplate

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.grocerez.R // Replace with the correct package name
import com.google.android.material.textfield.TextInputEditText
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.grocerez.ui.myplate.MyPlateViewModel
import com.example.grocerez.data.model.MyPlateItem
import com.example.grocerez.database.AppDatabase
import com.example.grocerez.dao.CategoryDao
import com.example.grocerez.dao.MyPlateDao
import com.example.grocerez.dao.UnitDao
import com.example.grocerez.data.model.Category
import com.example.grocerez.data.model.Unit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MyPlateSettingsFragment : Fragment(), GoalAdapter.GoalClickListener {
    private lateinit var sexSpinner: Spinner
    private lateinit var weightSpinner: Spinner
    private lateinit var heightSpinner: Spinner
    private lateinit var ageText: TextInputEditText
    private lateinit var weightText: TextInputEditText
    private lateinit var heightText: TextInputEditText
    private lateinit var paSpinner: Spinner
    private lateinit var editButton: Button
    private var isInEditMode = false

    private val viewModel: MyPlateViewModel by viewModels()
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: GoalAdapter

    // Initialize shared view model
    private val sharedModel: MyPlateViewModel by activityViewModels()

    private lateinit var textViewFeedback: TextView
    private lateinit var myPlateDao: MyPlateDao
    private lateinit var categoryDao: CategoryDao
    private lateinit var unitDao: UnitDao

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_myplate_settings, container, false)
        // Find the back_button ImageView
        val backButton: ImageView = view.findViewById(R.id.back_button)
        textViewFeedback = view.findViewById(R.id.textViewMyPlateFeedback)
        // Set OnClickListener for the back_button
        backButton.setOnClickListener {
            // Navigate back to the MyPlateFragment
            findNavController().navigateUp()
        }

        val appDatabase = AppDatabase.getInstance(requireContext())
        myPlateDao = appDatabase.myPlateDao()
        categoryDao = appDatabase.categoryDao()
        unitDao = appDatabase.unitDao()

        // Find the Spinner and Button views
        sexSpinner = view.findViewById(R.id.sexSpinner)
        weightSpinner = view.findViewById(R.id.weightSpinner)
        heightSpinner = view.findViewById(R.id.heightSpinner)
        ageText = view.findViewById(R.id.ageText)
        weightText = view.findViewById(R.id.weightText)
        heightText = view.findViewById(R.id.heightText)
        paSpinner = view.findViewById(R.id.physActSpinner)
        editButton = view.findViewById(R.id.edit_button)

        // Initialize spinner state
        updateInputState()

        // Set OnClickListener for the Edit button
        editButton.setOnClickListener {
            // Toggle the edit mode
            toggleEditMode()

            // Store the user-entered data into the MyPlateViewModel
            if (!isInEditMode) {
                // Read data from the text boxes and spinners
                val age = ageText.text.toString().toInt()
                val weight = weightText.text.toString().toDouble()
                val weightUnit = weightSpinner.selectedItem.toString()
                val weightInKilograms = convertWeightToKilograms(weight, weightUnit)
                val height = heightText.text.toString().toDouble()
                val sex = sexSpinner.selectedItemPosition
                val physicalActivityLevel = paSpinner.selectedItemPosition

                if (isAnyFieldEmpty()) {
                    // If any field is empty, stay in edit mode
                    //isInEditMode = true
                    editButton.isEnabled = false

                } else{
                    //HERE U GONNA SEND THE FRIKEN WEIGHT AND WEIGHT UNIT SUCKER
                    //Update the ViewModel with the new user data
                    viewModel.updateUserData(age, weightInKilograms, height, sex, physicalActivityLevel)
                    updateInputState()
                    editButton.isEnabled = true

                    // Initialize RecyclerView and Adapter
                    recyclerView = view.findViewById(R.id.recyclerView)
                    adapter = GoalAdapter(viewModel.goals, this)
                    recyclerView.adapter = adapter
                    recyclerView.layoutManager = LinearLayoutManager(requireContext())
                }

            }
        }

        return view
    }

    fun convertWeightToKilograms(weight: Double, unit: String): Double {
        return if (unit == "lbs") {
            // Convert pounds to kilograms (1 pound = 0.453592 kilograms)
            weight * 0.453592
        } else {
            // Weight is already in kilograms, so return it as is
            weight
        }
    }

    // Call this function whenever you need to update the spinner state, for example, when the Edit button is clicked.
    private fun updateInputState() {
        // Set the enabled state of the Spinners based on the edit mode
        sexSpinner.isEnabled = isInEditMode
        weightSpinner.isEnabled = isInEditMode
        heightSpinner.isEnabled = isInEditMode
        ageText.isEnabled = isInEditMode
        weightText.isEnabled = isInEditMode
        heightText.isEnabled = isInEditMode
        paSpinner.isEnabled = isInEditMode
    }

    private fun toggleEditMode() {
        // Toggle the edit mode
        isInEditMode = !isInEditMode

        // Update spinner state
        updateInputState()

        // Change appearance of the dropdown arrow based on the edit mode
        val arrowColor =
            if (isInEditMode) R.color.green001 else R.color.black
        // Set the tint color of the dropdown arrow
        // (You may need to use a different method based on your setup)
        sexSpinner.background.setTint(ContextCompat.getColor(requireContext(), arrowColor))
        weightSpinner.background.setTint(ContextCompat.getColor(requireContext(), arrowColor))
        heightSpinner.background.setTint(ContextCompat.getColor(requireContext(), arrowColor))
        paSpinner.background.setTint(ContextCompat.getColor(requireContext(), arrowColor))

        // Change the text of the Edit button to Save or vice versa
        editButton.text = if (isInEditMode) getString(R.string.save_button_text) else getString(R.string.edit)
    }
    fun determineMyPlateInfo(totalCaloricExpenditure: Int): MyPlateViewModel.MyPlateInfo {
        return when (totalCaloricExpenditure.toInt()) {
            in 1600..1699 -> MyPlateViewModel.MyPlateInfo(1.5, 2.0, 5.0, 5.0, 3.0)
            in 1700..1899 -> MyPlateViewModel.MyPlateInfo(1.5, 2.5, 6.0, 5.0, 3.0)
            in 1900..2099 -> MyPlateViewModel.MyPlateInfo(2.0, 2.5, 6.0, 5.5, 3.0)
            in 2100..2299 -> MyPlateViewModel.MyPlateInfo(2.0, 3.0, 7.0, 6.0, 3.0)
            in 2300..2499 -> MyPlateViewModel.MyPlateInfo(2.0, 3.0, 8.0, 6.5, 3.0)
            in 2500..2699 -> MyPlateViewModel.MyPlateInfo(2.0, 3.5, 9.0, 6.5, 3.0)
            in 2700..2899 -> MyPlateViewModel.MyPlateInfo(2.5, 3.5, 10.0, 7.0, 3.0)
            in 2900..3099 -> MyPlateViewModel.MyPlateInfo(2.5, 4.0, 10.0, 7.0, 3.0)
            else -> MyPlateViewModel.MyPlateInfo(2.5, 4.0, 10.0, 7.0, 3.0)
        }
    }
    private fun isAnyFieldEmpty(): Boolean {
        return ageText.text.isNullOrEmpty() ||
                weightText.text.isNullOrEmpty() ||
                heightText.text.isNullOrEmpty()
    }

    /*Upon clicking on a goal, the proper recommendations will be calculated and sent to sharedModel
    * where myplateFragment can access in order to display amounts to the user.*/
    override fun onGoalClicked(goal: MyPlateViewModel.Goal) {
        val info = determineMyPlateInfo(goal.calories)
        sharedModel.updateFoodAmounts(info)

        val categoryNameList = arrayListOf("fruit", "vegetable", "grains", "protein", "dairy")
        val unitList = arrayListOf("cup", "cup", "oz", "oz", "cup") // All strings

        for (i in categoryNameList.indices) {
            val amount = when (i) {
                0 -> info.fruitAmount
                1 -> info.vegetableAmount
                2 -> info.grainAmount
                3 -> info.proteinAmount
                4 -> info.dairyAmount
                else -> 0.0 // Handle default case
            }
            addToMyPlate(categoryNameList[i], amount.toFloat(), unitList[i])
        }
    }



    private fun addToMyPlate(categoryName: String, amount: Float, unitName: String){
        // Perform database operation to add the MyPlate item
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Check if the category exists in the category table
                var category = categoryDao.findCategoryByName(categoryName = categoryName)

                // If the category doesn't exist, insert it into the category table
                if (category == null) {
                    category = Category(categoryName)
                    categoryDao.insertCategory(category)
                }

                // Check if the unit exists in the unit table
                var unit = unitDao.findUnitByName(unitName)

                // If the unit doesn't exist, insert it into the unit table
                if (unit == null) {
                    unit = Unit(unitName)
                    unitDao.insertUnit(unit)
                }

                // Now that we have ensured that the category and unit exist, add the MyPlateItem
                val myPlateItem = MyPlateItem(
                    categoryName = categoryName,
                    amount = amount,
                    unit = unitName
                )
                myPlateDao.insertMyPlateItem(myPlateItem)

                withContext(Dispatchers.Main) {
                    textViewFeedback.text = "Item added to My Plate successfully."
                }
            }
            catch (e:Exception){
                withContext(Dispatchers.Main) {
                    textViewFeedback.text = "Error adding item to My Plate: ${e.message}"
                }

            }
        }
    }
}


