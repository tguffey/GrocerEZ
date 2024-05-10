package com.example.grocerez.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.grocerez.R
import com.example.grocerez.databinding.FragmentHomeBinding
import com.example.grocerez.ui.settings.SettingsActivity

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var recyclerViewList: RecyclerView
    private lateinit var adapter: NewsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val pantryPick = ArrayList<ListDomain>()
        for (i in 1..6) {
            pantryPick.add(ListDomain("Item $i", "carrots")) // Replace "sausage" with the appropriate URL
        }

        val recyclerView: RecyclerView = root.findViewById(R.id.recyclerView)
        val layoutManager = GridLayoutManager(requireContext(), 3)
        recyclerView.layoutManager = layoutManager

        recyclerView.adapter = CustomAdapter(pantryPick)


        // Find the settings button using binding
        val settingsButton: Button = binding.btnSettings

        // Set a click listener for the settings button
        settingsButton.setOnClickListener {
            // Handle button click here
            val intent = Intent(requireActivity(), SettingsActivity::class.java)
            startActivity(intent)
        }

        // Initialize RecyclerView
        recyclerViewList = binding.view
        val linearLayoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        recyclerViewList.layoutManager = linearLayoutManager

        // Prepare data for RecyclerView
        val reminders = ArrayList<ListDomain>()
        reminders.add(
            ListDomain(
                "Steak Strips",
                "steakstrips"
            )
        )
        reminders.add(
            ListDomain(
                "Carrots",
                "carrots"
            )
        )
        reminders.add(
            ListDomain(
                "Bananas",
                "bananas"
            )
        )
        reminders.add(
            ListDomain(
                "Coffee",
                "coffee"
            )
        )

        // Initialize adapter and set it to RecyclerView
        adapter = NewsAdapter(reminders)
        recyclerViewList.adapter = adapter

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

