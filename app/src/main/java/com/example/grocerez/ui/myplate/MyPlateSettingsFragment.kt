package com.example.grocerez.ui.myplate

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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_myplate_settings, container, false)
        // Find the back_button ImageView
        val backButton: ImageView = view.findViewById(R.id.back_button)
        // Set OnClickListener for the back_button
        backButton.setOnClickListener {
            // Navigate back to the MyPlateFragment
            findNavController().navigateUp()
        }
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
                val height = heightText.text.toString().toDouble()
                val sex = sexSpinner.selectedItemPosition
                val physicalActivityLevel = paSpinner.selectedItemPosition

                if (isAnyFieldEmpty()) {
                    // If any field is empty, stay in edit mode
                    //isInEditMode = true
                    editButton.isEnabled = false

                } else{
                    //Update the ViewModel with the new user data
                    viewModel.updateUserData(age, weight, height, sex, physicalActivityLevel)
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

    override fun onGoalClicked(goal: MyPlateViewModel.Goal) {
        val info = determineMyPlateInfo(goal.calories)
        // Update shared view model
        sharedModel.updateFoodAmounts(info)
        println("Recommended servings:")
        println("Fruit: ${info.fruitAmount}")
        println("Vegetable: ${info.vegetableAmount}")
        println("Grain: ${info.grainAmount}")
        println("Protein: ${info.proteinAmount}")
        println("Dairy: ${info.dairyAmount}")
    }
}


