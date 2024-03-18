package com.example.grocerez.ui.recipes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.grocerez.R
import com.example.grocerez.databinding.FragmentRecipesBinding
import com.example.grocerez.ui.recipes.NewRecipeSheet

class RecipesFragment : Fragment(), RecipeItemClickListener {

    private var _binding : FragmentRecipesBinding? = null
    private lateinit var recipesViewModel: RecipesViewModel
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
        super.onCreateView(inflater, container, savedInstanceState)

        recipesViewModel = ViewModelProvider(this.requireActivity())[RecipesViewModel::class.java]

        _binding = FragmentRecipesBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val context = requireContext()
        fabOpen = AnimationUtils.loadAnimation(context, R.anim.from_bottom_fab)
        fabClose = AnimationUtils.loadAnimation(context, R.anim.to_bottom_fab)
        fromBottomBg = AnimationUtils.loadAnimation(context, R.anim.from_bottom_anim)
        toBottomBg = AnimationUtils.loadAnimation(context, R.anim.to_bottom_anim)

        setRecyclerView()
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
            NewRecipeSheet(null).show(parentFragmentManager, "newRecipeTag")
            shrinkFab()
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

        // Toggle isExpanded
        isExpanded = !isExpanded
    }

    // Open the edit option buttons
    private fun expandFab() {

        binding.transparentBg.startAnimation(fromBottomBg)
        binding.clearListFab.startAnimation(fabOpen)
        binding.addItemFab.startAnimation(fabOpen)

        // Toggle isExpanded
        isExpanded = !isExpanded
    }

    private fun setRecyclerView(){
        val recipeItemAdapter = RecipeItemAdapter(mutableListOf(), this)

        binding.recipeListRecyclerView.apply{
            layoutManager = LinearLayoutManager(requireContext())
            adapter = recipeItemAdapter
        }

        recipesViewModel.recipeItems.observe(viewLifecycleOwner) {newRecipeItems ->
            val recipeItemList: List<RecipeItem> = newRecipeItems.orEmpty()
            recipeItemAdapter.updateRecipeItems(recipeItemList)

            recipeItemAdapter.notifyDataSetChanged()
        }
    }

    override fun editRecipeItem(recipeItem: RecipeItem) {
        NewRecipeSheet(recipeItem).show(parentFragmentManager, "newRecipeTag")
    }

}
