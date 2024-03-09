package com.example.grocerez.ui.recipes

import android.os.Bundle
import android.text.Editable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.example.grocerez.databinding.FragmentNewItemSheetBinding
import com.example.grocerez.databinding.FragmentNewRecipeSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


// BottomSheetDialogFragment for adding a new recipe
class NewRecipeSheet (var recipeItem: RecipeItem?) : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentNewRecipeSheetBinding // View binding for the layout
    private lateinit var recipeViewModel: RecipesViewModel // ViewModel for managing recipe data

    // Initialize the view and ViewModel when the view is created
    override fun onViewCreated(view: View, savedInstanceState: Bundle?){
        super.onViewCreated(view, savedInstanceState)

        // Check if a recipe item is provided for editing
        if(recipeItem != null)
        {
            // Set title and populate fields for editing
            binding.recipeTitle.text = "Edit Recipe"
            val editable = Editable.Factory.getInstance()
            binding.name.text = editable.newEditable(recipeItem!!.name)
            binding.description.text = editable.newEditable(recipeItem!!.description)
            binding.ingredients.text = editable.newEditable(recipeItem!!.ingredients)
            binding.notes.text = editable.newEditable(recipeItem!!.note)
        }
        else{
            // Set title for creating a new recipe
            binding.recipeTitle.text = "New Recipe"
        }

        // Initialize ViewModel
        val activity = requireActivity()
        recipeViewModel = ViewModelProvider(activity)[RecipesViewModel::class.java]

        // Set OnClickListener for save button
        binding.saveButton.setOnClickListener{
            saveAction()
        }

        // Set OnClickListener for cancel button
        binding.cancelButton.setOnClickListener{
            clearFields()
        }

    }

    // Clear all input fields
    private fun clearFields() {
        binding.name.setText("")
        binding.description.setText("")
        binding.ingredients.setText("")
        binding.notes.setText("")
        dismiss()
    }

    // Save action to add or update a recipe item
    private fun saveAction() {
        val name = binding.name.text.toString()
        val description = binding.description.text.toString()
        val ingredients = binding.ingredients.text.toString()
        val notes = binding.notes.text.toString()

        // Add or update the recipe item based on the provided recipeItem
        if (recipeItem == null){
            val newRecipe = RecipeItem(name, description, ingredients, notes)
            recipeViewModel.addRecipeItem(newRecipe)
        }
        else{
            recipeViewModel.updateRecipeItem(recipeItem!!.id, name, description, ingredients, notes)
        }

        // Clear all input fields
        binding.name.setText("")
        binding.description.setText("")
        binding.ingredients.setText("")
        binding.notes.setText("")
        dismiss()
    }

    // Inflate the layout for this fragment and initialize view binding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) : View {
        binding = FragmentNewRecipeSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

}