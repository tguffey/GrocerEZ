package com.example.grocerez.ui.myplate

import android.content.res.Configuration
import android.graphics.Color
import android.os.Build
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
        // Make the status bar transparent-jocelyn
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = activity?.window
            window?.decorView?.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            window?.statusBarColor = Color.TRANSPARENT
        }

        val notificationsViewModel =
            ViewModelProvider(this).get(MyPlateViewModel::class.java)

        _binding = FragmentMyplateBinding.inflate(inflater, container, false)
        val root: View = binding.root

        //val textView: TextView = binding.textNotifications
//        notificationsViewModel.text.observe(viewLifecycleOwner) {
//            textView.text = it
//        }

        anyChartView = root.findViewById(R.id.anyChartView)
        setupChartView()

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    //-jocelyn set up the chart with logic
    private fun setupChartView() {
        // I added a condition to check if the user is in dark or light mode
        val isDarkMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
        val pie = AnyChart.pie()
         //Use the appropriate background color
        //val backgroundColor = if (isDarkMode) "#333333" /* Dark grey color */ else "#FFFFFF" /* White color */
        if (isDarkMode){
            pie.background().fill("#333333")
        }else{
            pie.background().fill("#FFFFFF")
        }
        var background = pie.background();
        val category = arrayOf("Grains", "Protein", "Vegetables", "Fruits")
        val amount = floatArrayOf(0.28F, 0.22F, 0.28F, 0.22F)

        val dataEntries: MutableList<DataEntry> = ArrayList()

        for (i in category.indices) {
            dataEntries.add(ValueDataEntry(category[i], amount[i]))
        }

        pie.data(dataEntries)
        //pie.background().fill("#72A0C1")
        pie.stroke("6px #F1F1F1")
        pie.title("My Plate")
        pie.padding(0, 0, 0, 0)
        anyChartView.setChart(pie)
    }
}
