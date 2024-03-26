
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.grocerez.databinding.FragmentNewRecipeSheetBinding
import com.example.grocerez.ui.recipes.IngredentItemClickListener
import com.example.grocerez.ui.recipes.IngredientItem
import com.example.grocerez.ui.recipes.RecipeIngredientAdapter
import com.example.grocerez.ui.recipes.RecipeItem
import com.example.grocerez.ui.recipes.RecipesViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class NewRecipeSheet(var recipeItem: RecipeItem?) : BottomSheetDialogFragment(),  IngredentItemClickListener{

    private var _binding: FragmentNewRecipeSheetBinding? = null
    private val binding get() = _binding!!

    private lateinit var recipeViewModel: RecipesViewModel
    private lateinit var ingredientItemAdapter: RecipeIngredientAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewRecipeSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Adjust the bottom sheet dialog to pull all the way to the top of the screen
        dialog?.setOnShowListener {
            val bottomSheetDialog = it as BottomSheetDialog
            val bottomSheetInternal =
                bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheetInternal?.let { sheet ->
                val behavior = BottomSheetBehavior.from(sheet)
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
                behavior.skipCollapsed = true
                behavior.isHideable = false
            }
        }

        recipeViewModel = ViewModelProvider(requireActivity()).get(RecipesViewModel::class.java)

        ingredientItemAdapter = RecipeIngredientAdapter(mutableListOf(), this)

        if (recipeItem != null) {
            binding.recipeTitle.text = "Edit Recipe"
            val editable = Editable.Factory.getInstance()
            binding.name.text = editable.newEditable(recipeItem!!.name)
            binding.description.text = editable.newEditable(recipeItem!!.description)
            recipeItem!!.ingredients.forEach { ingredient ->
                ingredientItemAdapter.addIngredients(ingredient)
            }
            binding.notes.text = editable.newEditable(recipeItem!!.note)
        } else {
            binding.recipeTitle.text = "New Recipe"
        }

        binding.saveButton.setOnClickListener {
            saveAction()
        }

        binding.cancelButton.setOnClickListener {
            clearFields()
        }

        binding.addIngredientButton.setOnClickListener {
            showIngredientInputDialog()
        }

        binding.ingredientRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = ingredientItemAdapter
        }
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

        val ingredients = ingredientItemAdapter.getIngredients().toMutableList()

        if (recipeItem == null) {
            val newRecipe = RecipeItem(name, description, ingredients, notes)
            recipeViewModel.addRecipeItem(newRecipe)
        } else {
            recipeItem!!.name = name
            recipeItem!!.description = description
            recipeItem!!.note = notes
            recipeItem!!.ingredients.clear()
            recipeItem!!.ingredients.addAll(ingredients)
            recipeViewModel.updateRecipeItem(recipeItem!!)
        }

        clearFields()
    }

    private fun clearFields() {
        binding.name.setText("")
        binding.description.setText("")
        binding.notes.setText("")
        dismiss()
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