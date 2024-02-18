package com.example.grocerez.ui.shopping

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.grocerez.R
import com.example.grocerez.databinding.FragmentShoppingBinding

class ShoppingFragment : Fragment() {

    private var _binding : FragmentShoppingBinding? = null
    private lateinit var shoppingViewModel: ShoppingViewModel
    // Tracks whether or not the edit options are expanded or not
    private var isExpanded = false

    // Animation variables
    private lateinit var fabOpen: Animation
    private lateinit var fabClose: Animation
    private lateinit var fromBottomBg: Animation
    private lateinit var toBottomBg: Animation

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    // Create the UI view
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        shoppingViewModel = ViewModelProvider(this).get(ShoppingViewModel::class.java)

        _binding = FragmentShoppingBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Label the shopping list as empty
        val textView: TextView = binding.textShopping
        shoppingViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

        // Animate the expandable Edit Item button
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
        // Make a new sheet when the Add Item button is pressed
        binding.addItemFab.setOnClickListener {
            // Show New Grocery Item bottom dialog
            NewGrocerySheet().show(parentFragmentManager, "newItemTag")
        }

        return root
    }

    // Destroy the view once the user navigates to a different page
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // Close the edit option buttons
    private fun shrinkFab() {
        binding.transparentBg.startAnimation(toBottomBg)
        binding.clearListFab.startAnimation(fabClose)
        binding.addItemFab.startAnimation(fabClose)
        binding.addCategoryFab.startAnimation(fabClose)

        // Toggle isExpanded
        isExpanded = !isExpanded
    }

    // Open the edit option buttons
    private fun expandFab() {

        binding.transparentBg.startAnimation(fromBottomBg)
        binding.clearListFab.startAnimation(fabOpen)
        binding.addItemFab.startAnimation(fabOpen)
        binding.addCategoryFab.startAnimation(fabOpen)

        // Toggle isExpanded
        isExpanded = !isExpanded
    }

}