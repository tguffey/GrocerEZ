package com.example.grocerez.ui.recipes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.grocerez.R
import com.example.grocerez.databinding.FragmentMyRecipesBinding

class MyRecipesFragment : Fragment(), RecipeItemClickListener{

    private var _binding : FragmentMyRecipesBinding? = null

    // ViewModel for managing recipes
    private lateinit var recipesViewModel: RecipesViewModel

    // Original unfiltered list of recipe items
    private var originalRecipeList: List<RecipeItem> = emptyList()

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        // Initialize ViewModel
        recipesViewModel = ViewModelProvider(this.requireActivity())[RecipesViewModel::class.java]

        // Inflate the layout for this fragment using view binding
        _binding = FragmentMyRecipesBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Set up RecyclerView
        setRecyclerView()

        recipesViewModel.recipeItems.observe(viewLifecycleOwner) { recipeItems ->
            if (recipeItems != null) {
                originalRecipeList = recipeItems
            }
        }

        val searchView = binding.recipeSearchBar
        searchView.clearFocus()
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // Handle query text submit
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Handle query text change
                newText?.let { filterList(it, recipesViewModel.recipeItems.value.orEmpty()) }
                return true
            }
        })

        // Set OnClickListener for addItemFab
        binding.addItemFab.setOnClickListener {
            // Show New Recipe bottom dialog
            if (findNavController().currentDestination?.id == R.id.myRecipesFragment) {
                findNavController().navigate(R.id.action_myRecipes_to_newRecipeSheet)
            }
        }

        // Set OnClickListener for the back button
        binding.backButton.setOnClickListener {
            // Navigate back
            findNavController().popBackStack()
        }

        return root
    }

    private fun filterList(query: String, recipeItems: List<RecipeItem>) {
        val filteredList = if (query.isNotEmpty()) {
            recipeItems.filter { recipe ->
                recipe.name.contains(query, ignoreCase = true) ||
                        recipe.description.contains(query, ignoreCase = true) ||
                        recipe.ingredients.any { it.name.contains(query, ignoreCase = true) }
            }
        } else {
            originalRecipeList
        }
        // Update RecyclerView adapter with filtered list
        (binding.recipeListRecyclerView.adapter as RecipeItemAdapter).updateRecipeItems(filteredList)
    }

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

            // Initialize the original unfiltered list when it's first received
            originalRecipeList = newRecipeItems.orEmpty()
        }
    }

    override fun editRecipeItem(recipeItem: RecipeItem) {
        val bundle = Bundle().apply {
            putParcelable("recipeItem", recipeItem)
        }
        findNavController().navigate(R.id.action_myRecipes_to_newRecipeSheet, bundle)
    }

    // Destroy the view once the user navigates to a different page
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}