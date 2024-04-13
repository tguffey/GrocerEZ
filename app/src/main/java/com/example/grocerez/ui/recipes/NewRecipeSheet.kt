package com.example.grocerez.ui.recipes

import IngredientInputDialog
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.grocerez.databinding.FragmentNewRecipeSheetBinding

class NewRecipeSheet : Fragment(), IngredentItemClickListener {

    private var _binding: FragmentNewRecipeSheetBinding? = null
    private val binding get() = _binding!!

    private lateinit var recipeViewModel: RecipesViewModel
    private lateinit var ingredientItemAdapter: RecipeIngredientAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentNewRecipeSheetBinding.inflate(inflater, container, false)
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
            binding.recipeTitle.text = "Edit Recipe"
            val editable = Editable.Factory.getInstance()
            binding.name.text = editable.newEditable(recipeItem.name)
            binding.description.text = editable.newEditable(recipeItem.description)
            recipeItem.ingredients.forEach { ingredient ->
                ingredientItemAdapter.addIngredients(ingredient)
            }
            binding.notes.text = editable.newEditable(recipeItem.note)
        } else {
            binding.recipeTitle.text = "New Recipe"
        }

        binding.saveButton.setOnClickListener {
            saveAction()
        }

        binding.cancelButton.setOnClickListener {
            clearFields()
            findNavController().popBackStack()
        }

        binding.addIngredientButton.setOnClickListener {
            showIngredientInputDialog()
        }

        binding.ingredientRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = ingredientItemAdapter
        }
    }

    private fun populateFields(recipeItem: RecipeItem) {
        binding.recipeTitle.text = "Edit Recipe"
        val editable = Editable.Factory.getInstance()
        binding.name.text = editable.newEditable(recipeItem.name)
        binding.description.text = editable.newEditable(recipeItem.description)
        recipeItem.ingredients.forEach { ingredient ->
            ingredientItemAdapter.addIngredients(ingredient)
        }
        binding.notes.text = editable.newEditable(recipeItem.note)
    }

    private fun showIngredientInputDialog() {
        val dialog = IngredientInputDialog(null)
        dialog.ingredientItemAdapter = ingredientItemAdapter
        dialog.show(childFragmentManager, "newIngredientTag")
    }

    private fun saveAction() {
        val name = binding.name.text.toString()
        val description = binding.description.text.toString()
        val notes = binding.notes.text.toString()

        // Check if name or description is empty
        if (name.isEmpty()) {
            Toast.makeText(requireContext(), "Name cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }

        if (description.isEmpty()) {
            Toast.makeText(requireContext(), "Description cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }

        val ingredients = ingredientItemAdapter.getIngredients().toMutableList()

        // Check if ingredients list is empty
        if (ingredients.isEmpty()) {
            Toast.makeText(requireContext(), "Please add at least one ingredient", Toast.LENGTH_SHORT).show()
            return
        }

        if (notes.isEmpty()) {
            Toast.makeText(requireContext(), "Add a note the recipe item", Toast.LENGTH_SHORT).show()
            return
        }

        val args = arguments
        val recipeItem = args?.getParcelable<RecipeItem>("recipeItem")


        if (recipeItem == null) {
            val newRecipe = RecipeItem(name, description, ingredients, notes)
            recipeViewModel.addRecipeItem(newRecipe)
        } else {
            recipeItem.name = name
            recipeItem.description = description
            recipeItem.note = notes
            recipeItem.ingredients.clear()
            recipeItem.ingredients.addAll(ingredients)
            recipeViewModel.updateRecipeItem(recipeItem)
        }
        clearFields()
        findNavController().popBackStack()
    }

    private fun clearFields() {
        binding.name.setText("")
        binding.description.setText("")
        binding.notes.setText("")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun editIngredientItem(ingredientItem: IngredientItem) {
        // Open the dialog window to update the ingredient item
        val dialog = IngredientInputDialog(ingredientItem)
        dialog.ingredientItemAdapter = ingredientItemAdapter
        dialog.show(childFragmentManager, "editIngredientTag")
    }

    fun setOnCancelListener(function: () -> Unit) {
    }
}
