package com.example.grocerez.ui.recipes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.grocerez.R
import com.example.grocerez.databinding.FragmentRecipeViewBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RecipeView : Fragment(){

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

        // Retrieve the recipeItem from the arguments bundle
        val args = arguments
        val recipeItemName = args?.getString("recipeItem")
        CoroutineScope(Dispatchers.IO).launch{
            if (recipeItemName != null){
                val recipeItem = recipeViewModel.findRecipeByName(recipeItemName)
                val recipeIngredients = recipeViewModel.getIngredientsForRecipe(recipeItem!!.recipeId)

                withContext(Dispatchers.IO){
                    binding.recipeTitle.text = recipeItem.name
                    val ingredientDisplayText = recipeIngredients.joinToString("\n") { ingredient ->
                        "- ${ingredient.name}, ${ingredient.amount} ${ingredient.unit}"
                    }
                    binding.recipeIngredients.text = ingredientDisplayText
                    binding.recipeNotes.text = recipeItem.instruction
                }
            }
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
                    putString("recipeItem", recipeItemName)
                }
                findNavController().navigate(R.id.action_recipeView_to_newRecipeSheet, bundle)            }
        }

        binding.nutritionButton.setOnClickListener{
            showNutritionFactsDialog(recipeItemName!!)
        }
    }

    private fun showNutritionFactsDialog(recipeItemName: String) {
        val dialog = NutritionFactsDialog.newInstance(recipeItemName)
        dialog.show(childFragmentManager, "nutrition")
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}