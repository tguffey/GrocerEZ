package com.example.grocerez.ui.recipes

import IngredientInputDialog
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.grocerez.R
import com.example.grocerez.data.Ingredient
import com.example.grocerez.databinding.FragmentRecipeViewBinding

class RecipeView : Fragment(), IngredentItemClickListener{

    private var _binding: FragmentRecipeViewBinding? = null
    private val binding get() = _binding!!
    private lateinit var recipeViewModel: RecipesViewModel
    private lateinit var ingredientItemAdapter: RecipeIngredientAdapter


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentRecipeViewBinding.inflate(inflater, container, false)
        val view = binding.root

        // Use the recipeItem data as needed

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recipeViewModel = ViewModelProvider(this.requireActivity()).get(RecipesViewModel::class.java)

        ingredientItemAdapter = RecipeIngredientAdapter(mutableListOf(), this)

        // Retrieve the recipeItem from the arguments bundle
        val args = arguments
        val recipeItem = args?.getParcelable<RecipeItem>("recipeItem")

        if (recipeItem != null) {
            val editable = Editable.Factory.getInstance()
            binding.recipeTitle.text = editable.newEditable(recipeItem.name)
            binding.recipeNotes.text = editable.newEditable(recipeItem.note)
        } else {
            binding.recipeTitle.text = "New Recipe"
        }

        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.useRecipeButton.setOnClickListener {
            null
        }

        binding.toShoppingButton.setOnClickListener {
            null
        }

        binding.editRecipeButton.setOnClickListener{
            if (findNavController().currentDestination?.id == R.id.recipesViewFragment) {
                val bundle = Bundle().apply {
                    putParcelable("recipeItem", recipeItem)
                }
                findNavController().navigate(R.id.action_recipeView_to_newRecipeSheet, bundle)            }
        }

        binding.nutritionButton.setOnClickListener{
            showNutritionFactsDialog()
        }

        binding.ingredientRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = ingredientItemAdapter
        }
    }

    private fun showNutritionFactsDialog() {
        val dialog = NutritionFactsDialog()
        dialog.show(childFragmentManager, "nutrition")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun editIngredientItem(ingredientItem: Ingredient) {
        // Open the dialog window to update the ingredient item
        val dialog = IngredientInputDialog(ingredientItem)
        dialog.ingredientItemAdapter = ingredientItemAdapter
        dialog.show(childFragmentManager, "editIngredientTag")
    }
}