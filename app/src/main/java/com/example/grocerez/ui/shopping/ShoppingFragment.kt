package com.example.grocerez.ui.shopping

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.grocerez.R
import com.example.grocerez.data.ShoppingRepository
import com.example.grocerez.data.model.ShoppingListItem
import com.example.grocerez.database.AppDatabase
import com.example.grocerez.databinding.FragmentShoppingBinding
import com.example.grocerez.ui.dashboard.FoodItem
import com.example.grocerez.ui.dashboard.FoodItemAdapter

class ShoppingFragment : Fragment(), ShoppingItemClickListener {

    private var _binding : FragmentShoppingBinding? = null
    private lateinit var shoppingViewModel: ShoppingViewModel
    // Tracks whether or not the edit options are expanded or not
    private var isExpanded = false

    // Animation variables
    private lateinit var fabOpen: Animation
    private lateinit var fabClose: Animation
    private lateinit var fromBottomBg: Animation
    private lateinit var toBottomBg: Animation

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    // Create the UI view
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val appDatabase = AppDatabase.getInstance(requireContext())
        shoppingViewModel = ViewModelProvider(this.requireActivity(),
            ShoppingViewModel.ShoppingModelFactory(
                ShoppingRepository(
                    categoryDao = appDatabase.categoryDao(),
                    itemDao = appDatabase.itemDao(),
                    shoppingListItemDao = appDatabase.shoppingListItemDao(),
                    unitDao = appDatabase.unitDao()
                )
            )).get(ShoppingViewModel::class.java)

        _binding = FragmentShoppingBinding.inflate(inflater, container, false)
        val root: View = binding.root

        /*// Label the shopping list as empty if there are no categories
        if (shoppingViewModel.categoryItems.value?.size == 0) {
            val textView: TextView = binding.textShopping
            shoppingViewModel.text.observe(viewLifecycleOwner) {
                textView.text = it
            }
        }*/

        // Animate the expandable Edit Item button
        val context = requireContext()
        fabOpen = AnimationUtils.loadAnimation(context, R.anim.from_bottom_fab)
        fabClose = AnimationUtils.loadAnimation(context, R.anim.to_bottom_fab)
        fromBottomBg = AnimationUtils.loadAnimation(context, R.anim.from_bottom_anim)
        toBottomBg = AnimationUtils.loadAnimation(context, R.anim.to_bottom_anim)

        // Determine whether to open or close edit options when the edit button is clicked
        binding.editItemFab.setOnClickListener {
            if (isExpanded) {
                // Close the edit options if the options are expanded
                shrinkFab()
            } else {
                // Open the edit options if the options are not expanded
                expandFab()
            }
        }
        // Make a new sheet when the Add Item button is pressed
        binding.addItemFab.setOnClickListener {
            // Show New Grocery Item bottom dialog
            NewGrocerySheet(null, null).show(parentFragmentManager, "newItemTag")
            binding.textShopping.visibility = View.INVISIBLE
            shrinkFab()
        }
        // Ask if the user wants to clear the selected items
        binding.clearListFab.setOnClickListener{
            val numItems = shoppingViewModel.categoryItems.value!!.size
            // Show Clear List dialog if there are checked off items
            if ((numItems > 0) /*&& (shoppingViewModel.anyChecked())*/){
                buildClearItemDialog()
            }
            else {
                notifyNone()
            }
            shrinkFab()
        }
        setRecyclerView()

        // TODO: this is to test viewing the db. fix this later
        binding.addCategoryFab.setOnClickListener {
            // fill this in
        }

        return root
    }

    // Destroy the view once the user navigates to a different page
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // Close the edit option buttons
    private fun shrinkFab() {
        binding.transparentBg.startAnimation(toBottomBg)
        binding.clearListFab.startAnimation(fabClose)
        binding.addItemFab.startAnimation(fabClose)
        binding.addCategoryFab.startAnimation(fabClose)

        // Toggle isExpanded
        isExpanded = !isExpanded
    }

    // Open the edit option buttons
    private fun expandFab() {

        binding.transparentBg.startAnimation(fromBottomBg)
        binding.clearListFab.startAnimation(fabOpen)
        binding.addItemFab.startAnimation(fabOpen)
        binding.addCategoryFab.startAnimation(fabOpen)

        // Toggle isExpanded
        isExpanded = !isExpanded
    }

    // Sets up the RecyclerView to display the list of grocery items
    private fun setRecyclerView() {
//        val categoryItemAdapter = CategoryItemAdapter(mutableListOf(), this)
        /*// Set the layout manager and adapter for the RecyclerView
        binding.categoryListRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = categoryItemAdapter
        }

        // Observe changes in the list of food items in the ViewModel
        shoppingViewModel.categoryItems.observe(viewLifecycleOwner) { newCategoryItems ->
            // Convert the MutableList to List before passing it to the adapter
            val foodItemsList: List<CategoryItem> = newCategoryItems.orEmpty()
            // Update the adapter with the new list of food items
            categoryItemAdapter.updateCategoryItems(foodItemsList)

            // Notify the RecyclerView about the change in the dataset
            categoryItemAdapter.notifyDataSetChanged()
        }*/
        val thisClickListener = this
/*        val categoryItemAdapter = CategoryItemAdapter(this)
        binding.categoryListRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = categoryItemAdapter
        }
        shoppingViewModel.categoryItems.observe(viewLifecycleOwner) {
            categoryItemAdapter.setCategories(it)
        }*/

        // Observe changes in the list of category items in the ViewModel
        shoppingViewModel.categoryItems.observe(viewLifecycleOwner) {
            // Apply any changes to the category RecyclerView
            binding.categoryListRecyclerView.apply {
                // Set the layout manager
                layoutManager = LinearLayoutManager(requireContext())
                // Set the adapter for the category RecyclerView
                // If the list of categories is not null, create an adapter for the list
                // and set it to the category RecyclerView
                if (it != null) {
                    adapter = CategoryItemAdapter(it, thisClickListener)
                }
            }
        }
        binding.categoryListRecyclerView.adapter?.notifyDataSetChanged()


        /*// Observe changes in the list of grocery items in the ViewModel
        shoppingViewModel.groceryItems.observe(viewLifecycleOwner){
            // Apply any changes to the item RecyclerView
            val groceryRecyclerView = view?.findViewById<RecyclerView>(R.id.groceryListRecyclerView)
            groceryRecyclerView?.apply {
                // Set the layout manager
                groceryRecyclerView.layoutManager = LinearLayoutManager(requireContext())
                // Set the adapter for the grocery RecyclerView
                // If the list of grocery is not null, create an adapter for the list
                // and set it to the grocery RecyclerView
                if (it != null) {
                    groceryRecyclerView.adapter = GroceryItemAdapter(it)
                }
            }
        }*/
    }

    // Build the dialog that allows the user what they want to delete
    private fun buildClearItemDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Clear Items")
        builder.setMessage("Remove items that are currently checked?")

        // remove the checked off items from the list if the OK button it pressed
        builder.setPositiveButton("OK", DialogInterface.OnClickListener { dialog, which ->
            Toast.makeText(
                context, "Checked off items have been removed.",
                Toast.LENGTH_SHORT
            ).show()
            shoppingViewModel.removeCheckedItems()
        })

        // do nothing if the cancel button is pressed
        builder.setNegativeButton("Cancel") { dialog, which ->
        }
        builder.show()
    }

    // Tells the user that there are no grocery items in the list
    private fun notifyNone() {
        Toast.makeText(
            context, "There are no items to remove",
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun checkItem(shoppingListItem: ShoppingListItem) {
        shoppingViewModel.toggleCheck(shoppingListItem)
    }
}