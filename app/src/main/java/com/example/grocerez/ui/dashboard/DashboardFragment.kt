package com.example.grocerez.ui.dashboard

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.grocerez.databinding.FragmentDashboardBinding

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var itemViewModel: DashboardViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Call the superclass onCreateView method
        super.onCreateView(inflater, container, savedInstanceState)

        // Initialize the ViewModel
        itemViewModel = ViewModelProvider(this.requireActivity())[DashboardViewModel::class.java]

        // Inflate the layout using view binding
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Set OnClickListener for the newItemButton
        binding.newItemButton.setOnClickListener {
            // Show the NewTaskSheet dialog
            NewTaskSheet().show(parentFragmentManager, "newItemTag")
        }

        // Observe changes in name LiveData and update the UI accordingly
        itemViewModel.name.observe(viewLifecycleOwner){ newValue ->
            binding.itemName.text = String.format("Item Name: %s", newValue)
        }

        // Observe changes in value LiveData and animate the progress accordingly
        itemViewModel.value.observe(viewLifecycleOwner) { newValue ->
            val intValue: Int = newValue ?: 0 // Default value if newValue is null or not an Int

            // Animate the progress using ObjectAnimator
            val objectAnimator = ObjectAnimator.ofInt(
                binding.itemProgressBar,
                "progress",
                binding.itemProgressBar.progress,
                intValue
            )

            // Set the animation duration
            objectAnimator.duration = 1000 // 1000 milliseconds (1 second)

            // Start the animation
            objectAnimator.start()
        }

        // Return the root view
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Set the binding variable to null to avoid memory leaks
        _binding = null
    }
}
