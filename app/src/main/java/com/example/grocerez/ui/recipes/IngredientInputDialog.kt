import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import com.example.grocerez.databinding.DialogAddIngredientsBinding
import com.example.grocerez.ui.recipes.IngredientItem
import com.example.grocerez.ui.recipes.IngredientItemViewHolder
import com.example.grocerez.ui.recipes.RecipeIngredientAdapter

class IngredientInputDialog(var ingredientItem: IngredientItem?) : DialogFragment() {

    private var _binding: DialogAddIngredientsBinding? = null
    private val binding get() = _binding!!
    private lateinit var ingredientItemViewHolder: IngredientItemViewHolder
    lateinit var ingredientItemAdapter: RecipeIngredientAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DialogAddIngredientsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (ingredientItem != null){
            binding.ingredientTitle.text = "Edit Item"
            val editable = Editable.Factory.getInstance()
            binding.name.text = editable.newEditable(ingredientItem!!.name)
            binding.quantity.text = editable.newEditable(ingredientItem!!.quantity.toString())
        }

        binding.saveButton.setOnClickListener {
            if (binding.name.text.toString() == "" || binding.quantity.text.toString() == "") {
                clearFields()
            }
            else{
                val name = binding.name.text.toString()
                val quantity = binding.quantity.text.toString()
                val ingredient = IngredientItem(name, quantity.toDouble())
                ingredientItemAdapter.addIngredients(ingredient)
                clearFields()
            }
        }

        binding.cancelButton.setOnClickListener {
            clearFields()
        }
    }

    private fun clearFields() {
        binding.name.setText("")
        binding.quantity.setText("")
        dismiss()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent) // Set transparent background
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ) // Set dialog dimensions
        dialog.window?.requestFeature(Window.FEATURE_NO_TITLE) // Hide the title bar
        dialog.setCanceledOnTouchOutside(true) // Optional: Close dialog when touched outside
        return dialog
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
