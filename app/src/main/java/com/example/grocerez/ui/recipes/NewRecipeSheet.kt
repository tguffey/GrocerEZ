package com.example.grocerez.ui.recipes

import IngredientInputDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.grocerez.data.Ingredient
import com.example.grocerez.data.model.Item
import com.example.grocerez.data.model.Recipe
import com.example.grocerez.data.model.RecipeItem
import com.example.grocerez.databinding.FragmentNewRecipeSheetBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NewRecipeSheet : Fragment(), IngredentItemClickListener, IngredientInputDialog.IngredientDialogListener{

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

        recipeViewModel = ViewModelProvider(this.requireActivity())[RecipesViewModel::class.java]

        ingredientItemAdapter = RecipeIngredientAdapter(mutableListOf(), this)

        // Retrieve the recipeItem from the arguments bundle
        binding.recipeTitle.text = "New Recipe"


        binding.saveButton.setOnClickListener {
            saveAction()
        }

        binding.cancelButton.setOnClickListener {
            clearFields()
            recipeViewModel.clearTemporaryList()
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

//    private fun populateFields(recipeItem: RecipeItem) {
//        binding.recipeTitle.text = "Edit Recipe"
//        val editable = Editable.Factory.getInstance()
//        binding.name.text = editable.newEditable(recipeItem.name)
//        recipeItem.ingredients.forEach { ingredient ->
//            ingredientItemAdapter.addIngredients(ingredient)
//        }
//        binding.notes.text = editable.newEditable(recipeItem.note)
//    }

    private fun showIngredientInputDialog() {
        val dialog = IngredientInputDialog(null)
        dialog.ingredientItemAdapter = ingredientItemAdapter
        dialog.show(childFragmentManager, "newIngredientTag")
    }

    private suspend fun insertRecipeAndGetId(recipe: Recipe): Long {
        return withContext(Dispatchers.IO) {
            recipeViewModel.addRecipes(recipe)
            return@withContext recipeViewModel.findRecipeByName(recipe.name)?.recipeId ?: -1
        }
    }

    private fun saveAction() {
        val name = binding.name.text.toString()
        val notes = binding.notes.text.toString()

        // Check if name or description is empty
        if (name.isEmpty()) {
            Toast.makeText(requireContext(), "Name cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }

        if (notes.isEmpty()) {
            Toast.makeText(requireContext(), "Add a note to the recipe item", Toast.LENGTH_SHORT).show()
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val newRecipe = Recipe(
                    name = name,
                    instruction = notes
                )
                recipeViewModel.addRecipes(newRecipe)

                val existingRecipe = recipeViewModel.findRecipeByName(name)

                val temporaryIngredientList = recipeViewModel.returnTemporaryList()

                if (existingRecipe != null) {
                    for (ingredient in temporaryIngredientList) {
                        val ingredientName = ingredient.name

                        val newItem = Item(
                            name = ingredientName, category = ingredient.category,
                            unitName = ingredient.unit, useRate = 0.0f
                        )
                        var item = recipeViewModel.findItemByName(ingredientName)

                        if (item == null ){
                            recipeViewModel.addItem(newItem)
                            item = recipeViewModel.findItemByName(ingredientName)
                        }

                        val newRecipeItem = RecipeItem(
                            recipeId = existingRecipe.recipeId,
                            itemId = item!!.item_id,
                            amount = ingredient.amount
                        )
                        recipeViewModel.addRecipeItems(newRecipeItem)
                    }
                }

                recipeViewModel.clearTemporaryList()

                // If everything is successful, clear the fields and navigate back
                withContext(Dispatchers.Main) {
                    clearFields()
                    findNavController().popBackStack()
                }
            } catch (e: Exception) {
                // Handle the exception here
                Log.e("Error", "An error occurred: ${e.message}")
                // Show a toast or a snackbar with the error message
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        context,
                        "An error occurred: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }



    private fun clearFields() {
        binding.name.setText("")
        binding.notes.setText("")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun editIngredientItem(ingredient: Ingredient) {
        // Open the dialog window to update the ingredient item
        val dialog = IngredientInputDialog(ingredient)
        dialog.ingredientItemAdapter = ingredientItemAdapter
        dialog.show(childFragmentManager, "editIngredientTag")
    }

    override fun onIngredientAdded(ingredient: Ingredient) {
        // Add the ingredient to the temporary list
        recipeViewModel.addToTemporaryList(ingredient)
        // Update the RecyclerView to display the new ingredient
        ingredientItemAdapter.addIngredients(ingredient)
    }
}
