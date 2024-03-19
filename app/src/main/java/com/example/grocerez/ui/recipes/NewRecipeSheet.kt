package com.example.grocerez.ui.recipes

import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.grocerez.databinding.FragmentNewRecipeSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

// BottomSheetDialogFragment for adding a new recipe
class NewRecipeSheet (var recipeItem: RecipeItem?) : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentNewRecipeSheetBinding // View binding for the layout
    private lateinit var recipeViewModel: RecipesViewModel // ViewModel for managing recipe data
    private lateinit var ingredientItemAdapter: RecipeIngredientAdapter

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return BottomSheetDialog(requireContext(), theme)
    }

    // Initialize the view and ViewModel when the view is created
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize ViewModel
        val activity = requireActivity()
        recipeViewModel = ViewModelProvider(activity)[RecipesViewModel::class.java]

        ingredientItemAdapter = RecipeIngredientAdapter(mutableListOf()) // Initialize the adapter here

        // Check if a recipe item is provided for editing
        if (recipeItem != null) {
            binding.recipeTitle.text = "Edit Recipe"
            val editable = Editable.Factory.getInstance()
            binding.name.text = editable.newEditable(recipeItem!!.name)
            binding.description.text = editable.newEditable(recipeItem!!.description)
            // Populate the ingredientItemAdapter with existing ingredients
            recipeItem!!.ingredients.forEach { ingredient ->
                ingredientItemAdapter.addIngredients(ingredient)
            }
            binding.notes.text = editable.newEditable(recipeItem!!.note)
        } else {
            binding.recipeTitle.text = "New Recipe"
        }

        // Set OnClickListener for save button
        binding.saveButton.setOnClickListener{
            saveAction()
        }

        // Set OnClickListener for cancel button
        binding.cancelButton.setOnClickListener{
            clearFields()
        }

        binding.ingredientRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = ingredientItemAdapter
        }

        // Set OnClickListener for add ingredient button
        binding.addIngredientButton.setOnClickListener {
            addIngredient()
        }
    }


    private fun addIngredient() {
        val ingredientName = binding.ingredientName.text.toString()
        if (ingredientName.isNotBlank()) {
            val ingredientItem = IngredientItem(ingredientName)
            ingredientItemAdapter.addIngredients(ingredientItem)
            binding.ingredientName.text?.clear()
        }
    }

    private fun saveAction() {
        val name = binding.name.text.toString()
        val description = binding.description.text.toString()
        val notes = binding.notes.text.toString()

        val ingredients = ingredientItemAdapter.getIngredients().toMutableList()

        if (recipeItem == null) {
            val newRecipe = RecipeItem(name, description, ingredients, notes)
            recipeViewModel.addRecipeItem(newRecipe)
        } else {
            // Update the existing recipeItem with new values
            recipeItem!!.name = name
            recipeItem!!.description = description
            recipeItem!!.note = notes
            // Clear existing ingredient items and replace with new ones
            recipeItem!!.ingredients.clear()
            recipeItem!!.ingredients.addAll(ingredients)
            // Update the recipe item in the ViewModel
            recipeViewModel.updateRecipeItem(recipeItem!!)
        }

        // Clear all input fields
        binding.name.setText("")
        binding.description.setText("")
        binding.notes.setText("")
        dismiss()
    }

    // Clear all input fields
    private fun clearFields() {
        binding.name.setText("")
        binding.description.setText("")
        binding.ingredientName.setText("")
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
