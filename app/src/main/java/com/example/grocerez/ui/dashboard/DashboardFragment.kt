package com.example.grocerez.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.grocerez.R
import com.example.grocerez.databinding.FragmentDashboardBinding

class DashboardFragment : Fragment(), FoodItemClickListener {

    private var _binding: FragmentDashboardBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var itemViewModel: DashboardViewModel
    private var isExpanded = false

    // Animation variables
    private lateinit var fabOpen: Animation
    private lateinit var fabClose: Animation
    private lateinit var fromBottomBg: Animation
    private lateinit var toBottomBg: Animation

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Call the superclass onCreateView method
        super.onCreateView(inflater, container, savedInstanceState)

        // Initialize the ViewModel
        itemViewModel = ViewModelProvider(this.requireActivity())[DashboardViewModel::class.java]

        // Inflate the layout using view binding
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val context = requireContext()
        fabOpen = AnimationUtils.loadAnimation(context, R.anim.from_bottom_fab)
        fabClose = AnimationUtils.loadAnimation(context, R.anim.to_bottom_fab)
        fromBottomBg = AnimationUtils.loadAnimation(context, R.anim.from_bottom_anim)
        toBottomBg = AnimationUtils.loadAnimation(context, R.anim.to_bottom_anim)

        // Determine whether to open or close edit options when the edit button is clicked
        binding.editItemFab.setOnClickListener {
            if (isExpanded) {
                // Close the edit options if the options are expanded
                shrinkFab()
            } else {
                // Open the edit options if the options are not expanded
                expandFab()
            }
        }

        // Set OnClickListener for the newItemButton
        binding.addItemFab.setOnClickListener {
            // Show the NewTaskSheet dialog
            NewTaskSheet(null).show(parentFragmentManager, "newItemTag")
            shrinkFab()
        }
        setRecyclerView()

        // Return the root view
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Set the binding variable to null to avoid memory leaks
        _binding = null
    }

    // Close the edit option buttons
    private fun shrinkFab() {
        binding.transparentBg.startAnimation(toBottomBg)
        binding.addCategoryFab.startAnimation(fabClose)
        binding.addItemFab.startAnimation(fabClose)
        binding.scanBarcode.startAnimation(fabClose)

        // Toggle isExpanded
        isExpanded = !isExpanded
    }

    // Open the edit option buttons
    private fun expandFab() {

        binding.transparentBg.startAnimation(fromBottomBg)
        binding.addCategoryFab.startAnimation(fabOpen)
        binding.addItemFab.startAnimation(fabOpen)
        binding.scanBarcode.startAnimation(fabOpen)

        // Toggle isExpanded
        isExpanded = !isExpanded
    }

    private fun setRecyclerView() {
        // Initialize the adapter
        val foodItemAdapter = FoodItemAdapter(mutableListOf(), this)

        // Set the layout manager and adapter for the RecyclerView
        binding.foodListRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = foodItemAdapter
        }

        // Observe changes in the list of food items in the ViewModel
        itemViewModel.foodItems.observe(viewLifecycleOwner) { newFoodItems ->
            // Convert the MutableList to List before passing it to the adapter
            val foodItemsList: List<FoodItem> = newFoodItems.orEmpty()
            // Update the adapter with the new list of food items
            foodItemAdapter.updateFoodItems(foodItemsList)

            // Notify the RecyclerView about the change in the dataset
            foodItemAdapter.notifyDataSetChanged()
        }
    }



    // Method called when a food item is edited
    override fun editFoodItem(foodItem: FoodItem) {
        // Show the NewTaskSheet dialog for editing the food item
        NewTaskSheet(foodItem).show(parentFragmentManager, "newFoodTag")
    }

}
