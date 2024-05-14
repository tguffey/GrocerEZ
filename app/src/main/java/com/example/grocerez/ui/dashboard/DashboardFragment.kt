package com.example.grocerez.ui.dashboard

import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.grocerez.R
import com.example.grocerez.data.PantryRepository
import com.example.grocerez.database.AppDatabase
import com.example.grocerez.databinding.FragmentDashboardBinding
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult
import com.journeyapps.barcodescanner.ScanOptions
import com.example.grocerez.SocketHandler
import io.socket.client.Socket
import org.json.JSONObject


class DashboardFragment : Fragment(), FoodItemClickListener {

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()){
            isGranted: Boolean ->
            if (isGranted){
                showCamera()
            }
            else{
                // explain why you need permission
            }
        }

    private val scanLauncher =
        registerForActivityResult(ScanContract())
        {
            result: ScanIntentResult ->
            run {
                if (result.contents == null) {
                    Toast.makeText(requireContext(), "Cancelled", Toast.LENGTH_SHORT).show()
                } else {
                    setResult(result.contents)
                }
            }
        }

    private var _binding: FragmentDashboardBinding? = null

    private val binding get() = _binding!!
    private lateinit var dashboardViewModel: DashboardViewModel
    private var isExpanded = false

    // Animation variables
    private lateinit var fabOpen: Animation
    private lateinit var fabClose: Animation
    private lateinit var fromBottomBg: Animation
    private lateinit var toBottomBg: Animation

    // For backend socket connections
    private lateinit var mSocket: Socket


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Set and get sockets for backend connection
        SocketHandler.establishConnection()
        mSocket = SocketHandler.getSocket()

        val appDatabase = AppDatabase.getInstance(requireContext())
        dashboardViewModel = ViewModelProvider(this.requireActivity(),
            DashboardViewModel.PantryModelFactory(
                PantryRepository(
                    categoryDao = appDatabase.categoryDao(),
                    itemDao = appDatabase.itemDao(),
                    pantryItemDao = appDatabase.pantryItemDao(),
                    unitDao = appDatabase.unitDao()
                )
            )).get(DashboardViewModel::class.java)

        dashboardViewModel.loadPantryList()
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Return the root view
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setRecyclerView()

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

        // Set OnClickListener for the newItemButton
        binding.addItemFab.setOnClickListener {
            // Show the NewTaskSheet dialog
            if (findNavController().currentDestination?.id == R.id.navigation_dashboard) {
                findNavController().navigate(R.id.action_dashboard_to_newPantryItem)
            }
            shrinkFab()
        }

        binding.scanBarcode.setOnClickListener {
            checkPermissionCamera(requireContext())
        }
        // Setup the socket listeners for receiving barcode scan results
        setupSocketListeners()
    }

    private fun checkPermissionCamera(context: Context) {
        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
            showCamera()
        }
        else if (shouldShowRequestPermissionRationale(android.Manifest.permission.CAMERA)){
            Toast.makeText(context, "CAMERA permission required", Toast.LENGTH_SHORT).show()
        }
        else{
            requestPermissionLauncher.launch(android.Manifest.permission.CAMERA)
        }
    }

    // BARCODE SCANNER FUNCTIONS
    // Set the result of the barcode scan
    private fun setResult(scannedBarcodeNumber: String) {
        // Emit the scanned number to the backend
        mSocket.emit("get-barcode-info", scannedBarcodeNumber)
        // For displaying the barcode number for testing
        // binding.textResult.text = scannedBarcodeNumber
    }

    // Socket listeners for receiving data from backend
    private fun setupSocketListeners() {
        // Function that activates when receiving a barcode scan number
        mSocket.on("productInfo") { args ->
            val productName = args[0]?.toString() ?: "Unknown product"
            try {
                // Log the product name to Logcat
                Log.d("ProductInfoResult", productName)

                // Update the TextView to display the product name
                activity?.runOnUiThread {
                    binding.textResult.text = productName
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Log.d("ProductInfoError", "Error displaying product info")
            }
        }
    }

    private fun showCamera() {
        val options = ScanOptions()
        options.setDesiredBarcodeFormats(ScanOptions.UPC_A)
        options.setPrompt("Scan Barcode")
        options.setCameraId(0)
        options.setBeepEnabled(false)
        options.setBarcodeImageEnabled(true)
        options.setOrientationLocked(false)

        scanLauncher.launch(options)

    }
    override fun onDestroyView() {
        super.onDestroyView()
        // Set the binding variable to null to avoid memory leaks
        // Disconnect the socket to avoid memory leaks
        SocketHandler.closeConnection()
        _binding = null
    }

    // Close the edit option buttons
    private fun shrinkFab() {
        binding.transparentBg.startAnimation(toBottomBg)
        binding.addCategoryFab.startAnimation(fabClose)
        binding.addItemFab.startAnimation(fabClose)
        binding.scanBarcode.startAnimation(fabClose)

        // Toggle isExpanded
        isExpanded = !isExpanded
    }

    // Open the edit option buttons
    private fun expandFab() {

        binding.transparentBg.startAnimation(fromBottomBg)
        binding.addCategoryFab.startAnimation(fabOpen)
        binding.addItemFab.startAnimation(fabOpen)
        binding.scanBarcode.startAnimation(fabOpen)

        // Toggle isExpanded
        isExpanded = !isExpanded
    }

    private fun setRecyclerView() {
        val thisClickListener = this

        dashboardViewModel.categoryPantryItems.observe(viewLifecycleOwner){
            // Set the layout manager and adapter for the RecyclerView
            binding.foodListRecyclerView.apply {
                layoutManager = LinearLayoutManager(requireContext())
                if (it != null){
                    adapter = CategoryPantryItemAdapter(it, thisClickListener)
                }
            }
        }

    }



    // Method called when a food item is edited
    override fun editFoodItem(pantryItemName: String) {
        // Show the NewPantryItem dialog for editing the pantry item
        val bundle = Bundle().apply {
            putString("pantryItemName", pantryItemName)
        }
        findNavController().navigate(R.id.action_dashboard_to_newPantryItem, bundle)
    }




}
