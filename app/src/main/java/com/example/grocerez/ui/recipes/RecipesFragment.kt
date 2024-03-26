package com.example.grocerez.ui.recipes

import NewRecipeSheet
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.grocerez.databinding.FragmentRecipesBinding


// Fragment for displaying and managing recipes
class RecipesFragment : Fragment(), RecipeItemClickListener {

    // View binding instance
    private var _binding : FragmentRecipesBinding? = null

    // ViewModel for managing recipes
    private lateinit var recipesViewModel: RecipesViewModel

    // Original unfiltered list of recipe items
    private lateinit var originalRecipeList: List<RecipeItem>

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

        // Set up RecyclerView
        setRecyclerView()

        // Keep a reference to the original unfiltered list
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
            NewRecipeSheet(null).show(parentFragmentManager, "newRecipeTag")
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

            // Initialize the original unfiltered list when it's first received
            originalRecipeList = newRecipeItems.orEmpty()
        }
    }

    // Handle the edit action for a recipe item
    override fun editRecipeItem(recipeItem: RecipeItem) {
        NewRecipeSheet(recipeItem).show(parentFragmentManager, "newRecipeTag")
    }

    // Destroy the view once the user navigates to a different page
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

