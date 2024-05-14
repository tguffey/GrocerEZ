package com.example.grocerez.ui.recipes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.grocerez.R
import com.example.grocerez.databinding.FragmentRecipesBinding


// Fragment for displaying and managing recipes
class RecipesFragment : Fragment(){

    // View binding instance
    private var _binding : FragmentRecipesBinding? = null

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    private lateinit var recipesViewModel: RecipesViewModel


    // Create the UI view
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        // Inflate the layout for this fragment using view binding
        _binding = FragmentRecipesBinding.inflate(inflater, container, false)
        val root: View = binding.root

        recipesViewModel = ViewModelProvider(this.requireActivity()).get(RecipesViewModel::class.java)

        // Set OnClickListener for addItemFab
        binding.myRecipesBtn.setOnClickListener {
            // Show New Recipe bottom dialog
            if (findNavController().currentDestination?.id == R.id.navigation_recipes) {
                findNavController().navigate(R.id.action_recipeFragment_to_myRecipes)
            }
        }

        // Set OnClickListener for clearListFab
        binding.parseRecipeBtn.setOnClickListener {
            if (findNavController().currentDestination?.id == R.id.navigation_recipes) {
                findNavController().navigate(R.id.action_recipeFragment_to_recipeParsing)
            }
        }

        // Set OnClickListener for clearListFab
        binding.searchRecipeBtn.setOnClickListener {
            if (findNavController().currentDestination?.id == R.id.navigation_recipes) {
                findNavController().navigate(R.id.action_recipeFragment_to_onlineSearchRecipe)
            }
        }


        return root
    }

    // Destroy the view once the user navigates to a different page
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
