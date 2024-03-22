package com.example.grocerez.ui.recipes

import NewRecipeSheet
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.grocerez.R
import com.example.grocerez.databinding.FragmentRecipesBinding


// Fragment for displaying and managing recipes
class RecipesFragment : Fragment(), RecipeItemClickListener {

    // View binding instance
    private var _binding : FragmentRecipesBinding? = null

    // ViewModel for managing recipes
    private lateinit var recipesViewModel: RecipesViewModel

    // Flag to track whether the edit options are expanded or not
    private var isExpanded = false

    // Animation variables
    private lateinit var fabOpen: Animation
    private lateinit var fabClose: Animation
    private lateinit var fromBottomBg: Animation
    private lateinit var toBottomBg: Animation

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    // Create the UI view
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)

        // Initialize ViewModel
        recipesViewModel = ViewModelProvider(this.requireActivity())[RecipesViewModel::class.java]

        // Inflate the layout for this fragment using view binding
        _binding = FragmentRecipesBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Load animations
        val context = requireContext()
        fabOpen = AnimationUtils.loadAnimation(context, R.anim.from_bottom_fab)
        fabClose = AnimationUtils.loadAnimation(context, R.anim.to_bottom_fab)
        fromBottomBg = AnimationUtils.loadAnimation(context, R.anim.from_bottom_anim)
        toBottomBg = AnimationUtils.loadAnimation(context, R.anim.to_bottom_anim)

        // Set up RecyclerView
        setRecyclerView()

        // Set OnClickListener for editItemFab
        binding.editItemFab.setOnClickListener {
            if (isExpanded) {
                // Close the edit options if the options are expanded
                shrinkFab()
            } else {
                // Open the edit options if the options are not expanded
                expandFab()
            }
        }

        // Set OnClickListener for addItemFab
        binding.addItemFab.setOnClickListener {
            // Show New Recipe bottom dialog
            NewRecipeSheet(null).show(parentFragmentManager, "newRecipeTag")
            shrinkFab()
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

        // Toggle isExpanded
        isExpanded = !isExpanded
    }

    // Open the edit option buttons
    private fun expandFab() {
        binding.transparentBg.startAnimation(fromBottomBg)
        binding.clearListFab.startAnimation(fabOpen)
        binding.addItemFab.startAnimation(fabOpen)

        // Toggle isExpanded
        isExpanded = !isExpanded
    }

    // Set up the RecyclerView with adapter and observer
    private fun setRecyclerView(){
        val recipeItemAdapter = RecipeItemAdapter(mutableListOf(), this)

        binding.recipeListRecyclerView.apply{
            layoutManager = LinearLayoutManager(requireContext())
            adapter = recipeItemAdapter
        }

        // Observe changes in recipe items and update the RecyclerView
        recipesViewModel.recipeItems.observe(viewLifecycleOwner) {newRecipeItems ->
            val recipeItemList: List<RecipeItem> = newRecipeItems.orEmpty()
            recipeItemAdapter.updateRecipeItems(recipeItemList)
            recipeItemAdapter.notifyDataSetChanged()
        }
    }

    // Handle the edit action for a recipe item
    override fun editRecipeItem(recipeItem: RecipeItem) {
        NewRecipeSheet(recipeItem).show(parentFragmentManager, "newRecipeTag")
    }

}
