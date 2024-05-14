package com.example.grocerez.ui.recipes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.grocerez.R
import com.example.grocerez.databinding.DialogNutritionFactsBinding
import com.example.grocerez.ui.myplate.MyPlateRecyclerAdapter
import com.example.grocerez.ui.myplate.RecommendedAmountModel
import java.util.Locale

class NutritionFactsDialog : DialogFragment() {

    companion object {
        fun newInstance(recipeItemName: String): NutritionFactsDialog {
            val args = Bundle().apply {
                putString("recipeItemName", recipeItemName)
            }
            val fragment = NutritionFactsDialog()
            fragment.arguments = args
            return fragment
        }
    }
    private lateinit var recyclerView: RecyclerView
    private var _binding: DialogNutritionFactsBinding? = null
    private val binding get() = _binding!!
    private var recipeItemName: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DialogNutritionFactsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        // Set dialog size
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val pastaDishesWithoutMeat = arrayOf(
            "Spaghetti Aglio e Olio",
            "Pasta Primavera",
            "Fettuccine Alfredo",
            "Pasta Pomodoro",
            "Pasta Puttanesca",
            "Linguine with Pesto",
            "Caprese Pasta",
            "Pasta alla Norma",
            "Pasta Carbonara (without bacon)",
            "Pasta with Garlic Butter Sauce",
            "Macaroni and Cheese",
            "Baked Ziti",
            "Lasagna (vegetarian version)",
            "Pasta Salad",
            "Broccoli Pasta",
            "Pasta with Marinara Sauce",
            "Alfredo Penne",
            "Four Cheese Pasta",
            "Pasta with Tomato Cream Sauce",
            "Creamy Mushroom Pasta",
            "Mac n Cheese"
        )

        val pastaDishesWithMeat = arrayOf(
            "Spaghetti Bolognese",
            "Linguine with Clam Sauce",
            "Penne alla Vodka with Pancetta",
            "Chicken Alfredo Pasta",
            "Beef Stroganoff",
            "Carbonara with Pancetta",
            "Sausage and Peppers Pasta",
            "Meatball Pasta",
            "Pasta with Sausage",
            "Pasta with Bacon",
            "Pasta with Meat Sauce",
            "Lasagna (traditional with ground beef)",
            "Chicken Parmesan Pasta",
            "Shrimp Scampi Pasta",
            "Bacon Carbonara",
            "Baked Ziti with Sausage",
            "Beef and Mushroom Stroganoff",
            "Cheeseburger Pasta",
            "Creamy Chicken Marsala Pasta",
            "Tuscan Chicken Pasta"
        )



        // Retrieve the recipe item name from arguments
        recipeItemName = arguments?.getString("recipeItemName")?.lowercase()

        binding.backButton.setOnClickListener{
            dismiss()
        }

        // Use the recipe item name as needed
        // For example, you can set it to a TextView
        binding.recipeNameTextView.text = recipeItemName

        //START OF RECYCLER VIEW LOGIC
        recyclerView = binding.root.findViewById(R.id.food_suggestions)
        val AmountsModelList = ArrayList<RecommendedAmountModel>()
        val categoryImages = intArrayOf(
            R.drawable.fruits_icon,
            R.drawable.vegetables_icon,
            R.drawable.grains_icon,
            R.drawable.protein_icon,
            R.drawable.dairy_icon
        )

        val suggestedAmountsArray: Array<Int>
        val actualAmountsArray: Array<Int>

        when (recipeItemName) {
            "mac n cheese" -> {
                suggestedAmountsArray = arrayOf(1, 1, 2, 2, 1)
                actualAmountsArray = arrayOf(0, 0, 2, 1, 1)
            }
            "meatballs" -> {
                suggestedAmountsArray = arrayOf(1, 1, 2, 2, 1)
                actualAmountsArray = arrayOf(0, 2, 0, 1, 0)
            }
            "tacos" -> {
                suggestedAmountsArray = arrayOf(1, 1, 2, 2, 1)
                actualAmountsArray = arrayOf(0, 2, 0, 1, 0)
            }
            // Add more cases for other recipe item names
            else -> {
                // Default case
                suggestedAmountsArray = arrayOf(1, 1, 1, 1, 1)
                actualAmountsArray = arrayOf(0, 0, 0, 0, 0)
            }
        }

        for (i in categoryImages.indices) {
            val foodAmountModel = RecommendedAmountModel(
                suggestedAmountsArray[i], // Assuming foodAmountsArray contains Double values
                actualAmountsArray[i],
                categoryImages[i]
            )
            AmountsModelList.add(foodAmountModel)
        }
        // Setting up the adapter
        val adapter = FoodSuggestionsAdapter(AmountsModelList)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        /*THE RECYCLER VIEW LOGIC WILL END HERE*/
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
