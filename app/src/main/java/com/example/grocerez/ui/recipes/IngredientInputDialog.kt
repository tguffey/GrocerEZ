import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.grocerez.R
import com.example.grocerez.databinding.DialogAddIngredientsBinding
import com.example.grocerez.ui.ItemAmount
import com.example.grocerez.ui.recipes.IngredientItem
import com.example.grocerez.ui.recipes.IngredientItemViewHolder
import com.example.grocerez.ui.recipes.RecipeIngredientAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class IngredientInputDialog(var ingredientItem: IngredientItem?) : DialogFragment() {

    private var _binding: DialogAddIngredientsBinding? = null
    private val binding get() = _binding!!
    private lateinit var ingredientItemViewHolder: IngredientItemViewHolder
    lateinit var ingredientItemAdapter: RecipeIngredientAdapter
    private lateinit var selectedUnit: String

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

        setSpinner()

        if (ingredientItem != null){
            binding.ingredientTitle.text = "Edit Item"
            val editable = Editable.Factory.getInstance()
            binding.name.text = editable.newEditable(ingredientItem!!.name)
            binding.quantity.text = editable.newEditable(ingredientItem!!.quantity.toString())
        }

        binding.saveButton.setOnClickListener {
            saveButton()
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

    private fun setSpinner() {
        // Create a list of the units, Units label is the first element
        var options: Array<String> = arrayOf("Units")
        options += ItemAmount.getAllUnits()

        val context = requireContext()
        val arrayAdapter = object : ArrayAdapter<String>(context,
            R.layout.shopping_quantity_spinner, options) {

            override fun isEnabled(position: Int): Boolean {
                return position != 0
            }

            // Show the Units label as grayed out and choices as black text
            override fun getDropDownView(
                position: Int,
                convertView: View?,
                parent: ViewGroup
            ): View {
                val view: TextView = super.getDropDownView(position, convertView, parent) as TextView
                if (position == 0) {
                    view.setTextColor(Color.GRAY)
                } else {
                    view.setTextColor(Color.BLACK)
                }
                return view
            }
        }
        // adapter for the actual list, creates an Item Amount object for the quantity
        arrayAdapter.setDropDownViewResource(R.layout.shopping_quantity_spinner)
        binding.quantitySpinner.adapter = arrayAdapter

        binding.quantitySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedUnit = parent?.getItemAtPosition(position) as String

                // Only the units choices can be selected and not the Units label
                if (position > 0) {
                    Toast.makeText(
                        context, "Selected : $selectedUnit",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
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

    fun saveButton(){

        //ingredientName
        val name = binding.name.text.toString().trim()

        // ingredientAmount
        val quantity = binding.quantity.text.toString().trim()

        // NULL CHECKS
        // Check if name or quantity is empty
        if (name.isEmpty() || quantity.isEmpty()) {
            Toast.makeText(requireContext(), "Name and quantity cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }

        if (selectedUnit == "Units") {
            Toast.makeText(requireContext(), "Select a given unit", Toast.LENGTH_SHORT).show()
            return
        }
        // if code made it past this point, that means name quantiy and units are filled.
        // now, we're gonna see if this item already exists in the table. if not add
        // if not exist yet, in order to add, fill the category with "uncategorized" for now.

        // TODO: find out how alexis used view model to adapt to adding to room database
        // this part performs database functions
        CoroutineScope(Dispatchers.IO).launch {

        }
        // TODO: figure out what this does.
        val ingredient = IngredientItem(name, quantity.toDouble(), selectedUnit)
        ingredientItemAdapter.addIngredients(ingredient)
        clearFields()
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
