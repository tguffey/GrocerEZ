package com.example.grocerez.ui.recipes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.grocerez.data.model.PantryItem
import com.example.grocerez.data.model.ShoppingListItem
import com.example.grocerez.databinding.FragmentRecipeViewBinding
import com.example.grocerez.ui.dashboard.DashboardViewModel
import com.example.grocerez.ui.shopping.ShoppingViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RecipeView : Fragment(){

    private var _binding: FragmentRecipeViewBinding? = null
    private val binding get() = _binding!!
    private lateinit var recipeViewModel: RecipesViewModel
    private lateinit var pantryItemViewModel: DashboardViewModel
    private lateinit var shoppingViewModel: ShoppingViewModel
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
        pantryItemViewModel = ViewModelProvider(this.requireActivity()).get(DashboardViewModel::class.java)
        shoppingViewModel = ViewModelProvider(this.requireActivity()).get(ShoppingViewModel::class.java)

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

        binding.postRecipeButton.setOnClickListener{
            postRecipe()
        }

        binding.useRecipeButton.setOnClickListener {
            useRecipe()
        }

        binding.toShoppingButton.setOnClickListener {
            sendToShoppingCart()
        }

        binding.nutritionButton.setOnClickListener{
            showNutritionFactsDialog(recipeItemName!!)
        }

        binding.deleteRecipeButton.setOnClickListener {
            deleteRecipe()
        }
    }

    private fun deleteRecipe() {
        // First, retrieve the recipe item name from the arguments bundle
        val args = arguments
        val recipeItemName = args?.getString("recipeItem")

        // Next, check if the recipe item name is not null
        if (recipeItemName != null) {
            // Use coroutine scope to perform asynchronous operations
            CoroutineScope(Dispatchers.IO).launch {
                // Retrieve the recipe item from the ViewModel
                val recipeItem = recipeViewModel.findRecipeByName(recipeItemName)
                // Check if the recipe item is not null
                if (recipeItem != null) {
                    // Retrieve the ingredients required for the recipe
                    val recipeIngredients = recipeViewModel.getIngredientsForRecipe(recipeItem.recipeId)

                    // Iterate through each recipe ingredient and check if it's available in the pantry
                    for (ingredient in recipeIngredients) {
                        // Retrieve the pantry item corresponding to the recipe ingredient
                        val pantryIngredients = pantryItemViewModel.findItemByName(ingredient.name)
                        if (pantryIngredients!=null){
                            pantryItemViewModel.deleteItem(pantryIngredients)
                        }
                    }
                    recipeViewModel.deleteRecipe(recipeItem)
                } else {
                    // Show a message indicating that ingredients are missing in the pantry
                    withContext(Dispatchers.Main) {
                        Toast.makeText(requireContext(), "Missing ingredients in the pantry!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        findNavController().popBackStack()
    }


    private fun postRecipe() {
        TODO("Not yet implemented")
    }

    private fun showNutritionFactsDialog() {
        val dialog = NutritionFactsDialog()
        dialog.show(childFragmentManager, "nutrition")
    }

    private fun useRecipe() {
        // First, retrieve the recipe item name from the arguments bundle
        val args = arguments
        val recipeItemName = args?.getString("recipeItem")

        // Next, check if the recipe item name is not null
        if (recipeItemName != null) {
            // Use coroutine scope to perform asynchronous operations
            CoroutineScope(Dispatchers.IO).launch {
                // Retrieve the recipe item from the ViewModel
                val recipeItem = recipeViewModel.findRecipeByName(recipeItemName)
                // Check if the recipe item is not null
                if (recipeItem != null) {
                    // Retrieve the ingredients required for the recipe
                    val recipeIngredients = recipeViewModel.getIngredientsForRecipe(recipeItem.recipeId)

                    // Initialize a boolean flag to track if all ingredients are available in the pantry
                    var allIngredientsAvailable = true

                    // Iterate through each recipe ingredient and check if it's available in the pantry
                    for (ingredient in recipeIngredients) {
                        // Retrieve the pantry item corresponding to the recipe ingredient
                        val pantryItem = pantryItemViewModel.findPantryItemByName(ingredient.name)
                        // Check if the pantry item is not null and if its amount is sufficient
                        if (pantryItem == null || pantryItem.amountFromInputDate < ingredient.amount) {
                            allIngredientsAvailable = false
                            break
                        }
                    }

                    // If all ingredients are available in the pantry, subtract the required amounts
                    if (allIngredientsAvailable) {
                        for (ingredient in recipeIngredients) {
                            // Retrieve the pantry item corresponding to the recipe ingredient
                            val pantryItem = pantryItemViewModel.findPantryItemByName(ingredient.name)
                            // Subtract the required amount from the pantry item
                            withContext(Dispatchers.IO){
                                pantryItem?.let {
                                    val newAmount = it.amountFromInputDate - ingredient.amount
                                    // Update the pantry item in the database
                                    val newPantryItem = PantryItem(
                                        itemName = it.itemName,
                                        amountFromInputDate = newAmount,
                                        inputDate = it.inputDate,
                                        shelfLifeFromInputDate = it.shelfLifeFromInputDate
                                    )

                                    pantryItemViewModel.addFoodItem(newPantryItem)
                                    pantryItemViewModel.deletePantryItem(it)
                                }
                            }
                        }
                        // Show a message indicating that the recipe can be used
                        withContext(Dispatchers.Main) {
                            Toast.makeText(requireContext(), "Recipe used successfully!", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        // Show a message indicating that ingredients are missing in the pantry
                        withContext(Dispatchers.Main) {
                            Toast.makeText(requireContext(), "Missing ingredients in the pantry!", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }

    fun sendToShoppingCart(){
        val args = arguments
        val recipeItemName = args?.getString("recipeItem")

        // Next, check if the recipe item name is not null
        if (recipeItemName != null) {
            // Use coroutine scope to perform asynchronous operations
            CoroutineScope(Dispatchers.IO).launch {
                // Retrieve the recipe item from the ViewModel
                val recipeItem = recipeViewModel.findRecipeByName(recipeItemName)
                // Check if the recipe item is not null
                if (recipeItem != null) {
                    // Retrieve the ingredients required for the recipe
                    val recipeIngredients = recipeViewModel.getIngredientsForRecipe(recipeItem.recipeId)

                    for (ingredient in recipeIngredients) {
                        val existingItem  = shoppingViewModel.findShoppingListItemByName(ingredient.name)
                        if (existingItem != null){
                            val updatedQuantity = existingItem.quantity + ingredient.amount
                            existingItem.quantity = updatedQuantity
                            shoppingViewModel.updateShoppingListItem(existingItem)
                        }
                        else {
                            val notes = "Added from ${recipeItem.name} recipe"
                            val shoppingListItem = ShoppingListItem(
                                itemName = ingredient.name,
                                quantity = ingredient.amount,
                                checkbox = false,
                                notes = notes
                            )
                            shoppingViewModel.addShoppingListItem(shoppingListItem)
                        }
                    }
                    // Show a message indicating that the recipe can be used
                    withContext(Dispatchers.Main) {
                        Toast.makeText(requireContext(), "Added Recipe Ingredients to Shopping Cart successfully!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
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