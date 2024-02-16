package com.example.grocerez.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.grocerez.databinding.FragmentDashboardBinding

class DashboardFragment : Fragment(), FoodItemClickListener {

    private var _binding: FragmentDashboardBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var itemViewModel: DashboardViewModel

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

        // Set OnClickListener for the newItemButton
        binding.newItemButton.setOnClickListener {
            // Show the NewTaskSheet dialog
            NewTaskSheet(null).show(parentFragmentManager, "newItemTag")
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
        }
    }


    // Method called when a food item is edited
    override fun editFoodItem(foodItem: FoodItem) {
        // Show the NewTaskSheet dialog for editing the food item
        NewTaskSheet(foodItem).show(parentFragmentManager, "newFoodTag")
    }

}
