package com.example.grocerez.ui.dashboard

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.grocerez.databinding.FragmentDashboardBinding

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val dashboardViewModel =
            ViewModelProvider(this)[DashboardViewModel::class.java]

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textDashboard
        dashboardViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

        // binds the start button from the xml file
        val startButton: Button = binding.startProgress
        // listens for the user to click the button
        startButton.setOnClickListener {
            onStart(it)
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // variable to define the progress of the bar
    private val currentProgress = 100

    private fun onStart(view: View) {
        // Set the maximum value for the ProgressBar
        binding.progressBar.max = 100
        //binding.startProgress.setOnClickListener()

        // Animate the progress using ObjectAnimator
        val objectAnimator = ObjectAnimator.ofInt(
            binding.progressBar,
            "progress",
            binding.progressBar.progress,
            currentProgress
        )

        // Set the animation duration
        objectAnimator.duration = 2000 // 1000 milliseconds (1 second)

        // Start the animation
        objectAnimator.start()
    }

}