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


class NewRecipeSheet (var recipeItem: RecipeItem?) : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentNewRecipeSheetBinding
    private lateinit var recipeViewModel: RecipesViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?){
        super.onViewCreated(view, savedInstanceState)

        if(recipeItem != null)
        {
            binding.recipeTitle.text = "Edit Recipe"
            val editable = Editable.Factory.getInstance()
            binding.name.text = editable.newEditable(recipeItem!!.name)
            binding.description.text = editable.newEditable(recipeItem!!.description)
            binding.ingredients.text = editable.newEditable(recipeItem!!.ingredients)
            binding.notes.text = editable.newEditable(recipeItem!!.note)
        }
        else{
            binding.recipeTitle.text = "New Recipe"
        }

        val activity = requireActivity()
        recipeViewModel = ViewModelProvider(activity)[RecipesViewModel::class.java]

        binding.saveButton.setOnClickListener{
            saveAction()
        }

        binding.cancelButton.setOnClickListener{
            clearFields()
        }

    }

    private fun clearFields() {
        binding.name.setText("")
        binding.description.setText("")
        binding.ingredients.setText("")
        binding.notes.setText("")
        dismiss()
    }

    private fun saveAction() {
        val name = binding.name.text.toString()
        val description = binding.description.text.toString()
        val ingredients = binding.ingredients.text.toString()
        val notes = binding.notes.text.toString()

        if (recipeItem == null){
            val newRecipe = RecipeItem(name, description, ingredients, notes)
            recipeViewModel.addRecipeItem(newRecipe)
        }
        else{
            recipeViewModel.updateRecipeItem(recipeItem!!.id, name, description, ingredients, notes)
        }

        binding.name.setText("")
        binding.description.setText("")
        binding.ingredients.setText("")
        binding.notes.setText("")
        dismiss()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) : View {
        binding = FragmentNewRecipeSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

}