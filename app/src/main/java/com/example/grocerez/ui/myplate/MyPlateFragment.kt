package com.example.grocerez.ui.myplate

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.grocerez.databinding.FragmentMyplateBinding
import com.anychart.AnyChart
import com.anychart.AnyChartView
import com.anychart.chart.common.dataentry.DataEntry
import com.anychart.chart.common.dataentry.ValueDataEntry
import com.example.grocerez.R

class MyPlateFragment : Fragment() {

    private var _binding: FragmentMyplateBinding? = null
    private lateinit var anyChartView: AnyChartView

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val notificationsViewModel =
            ViewModelProvider(this).get(MyPlateViewModel::class.java)

        _binding = FragmentMyplateBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textNotifications
        notificationsViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

        anyChartView = root.findViewById(R.id.anyChartView)
        setupChartView()

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupChartView() {
        val pie = AnyChart.pie()
        val months = arrayOf("January", "February", "March", "April")
        val salary = intArrayOf(16000, 20000, 30000, 50000)

        val dataEntries: MutableList<DataEntry> = ArrayList()

        for (i in months.indices) {
            dataEntries.add(ValueDataEntry(months[i], salary[i]))
        }
        pie.data(dataEntries)
        pie.title("Salary")
        anyChartView.setChart(pie)
    }
}
