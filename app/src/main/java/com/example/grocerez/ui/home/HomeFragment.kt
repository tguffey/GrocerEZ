package com.example.grocerez.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
        val homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

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
        val news = ArrayList<ListDomain>()
        news.add(
            ListDomain(
                "Browsing in Belgium",
                "pic1"
            )
        )
        news.add(
            ListDomain(
                "Browsing in France",
                "pic2"
            )
        )
        news.add(
            ListDomain(
                "Browsing in Spain",
                "pic3"
            )
        )
        news.add(
            ListDomain(
                "Browsing in China",
                "pic4"
            )
        )

        // Initialize adapter and set it to RecyclerView
        adapter = NewsAdapter(news)
        recyclerViewList.adapter = adapter

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
