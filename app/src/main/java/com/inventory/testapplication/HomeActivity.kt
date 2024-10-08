package com.inventory.testapplication

import CustomSpinnerAdapter
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.AdapterView
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.inventory.testapplication.databinding.ActivityHomeBinding
import java.io.IOException
import java.util.Locale

class HomeActivity : AppCompatActivity()  {
    private lateinit var binding: ActivityHomeBinding

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var permissionManager: PermissionManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        innit()



        checkLocationPermission()
    }

    private fun innit() {
        val items = listOf(
           "Auto Location",
            "Manual Location")

        // Create an instance of the custom adapter
        val  adapter = CustomSpinnerAdapter(this, items)

        // Set the adapter to the Spinner
        binding.mySpinner.adapter = adapter

        /*binding.mySpinner.setOnTouchListener { _, _ ->
            val isOpen = !adapter.isSpinnerOpens
            adapter.setSpinnerOpen(isOpen) // Toggle spinner state
            true
        }*/

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        /*
                binding.mySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                        adapter.setSpinnerOpen(false)
                    }

                    override fun onNothingSelected(parent: AdapterView<*>) {

                    }
                }

                binding.mySpinner.viewTreeObserver.addOnGlobalLayoutListener {
                    val isOpen = binding.mySpinner.dropDownVerticalOffset > 0
                    adapter.setSpinnerOpen(isOpen)
                }*/


        // Detect when the spinner is touched (about to open)
        binding.mySpinner.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                adapter.setSpinnerOpen(true)
            }
            false
        }



        // Detect when the spinner closes (after selecting an item)
        binding.mySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                // Spinner has been closed after selection

                adapter?.setSpinnerOpen(false)
                adapter?.isSpinnerOpens = false
            }

            override fun onNothingSelected(parent: AdapterView<*>) {

                // This might be called in some cases, ensure we handle it
                adapter?.setSpinnerOpen(false)
                adapter?.isSpinnerOpens = false
            }
        }
    }


    private fun checkLocationPermission() {
        permissionManager = object : PermissionManager(this) {
            override fun onPermissionGranted(permissionType: PermissionManager.PermissionType) {
                if (permissionType == PermissionManager.PermissionType.LOCATION) {
                    getLastKnownLocation()
                }
            }

            override fun onPermissionDenied(permissionType: PermissionManager.PermissionType, message: String) {
                Log.d("Permission", message)
                Toast.makeText(this@HomeActivity, "Permission denied: $message", Toast.LENGTH_SHORT).show()
            }

            override fun onPartialPermissionGranted(permissionType: PermissionManager.PermissionType, message: String) {
                Log.d("Permission", message)
                Toast.makeText(this@HomeActivity, "Partial permission granted: $message", Toast.LENGTH_SHORT).show()
            }
        }

        // Check for location permissions on start
        permissionManager.requestLocationPermission()
    }

    private fun getLastKnownLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                if (location != null) {
                    val latitude = location.latitude
                    val longitude = location.longitude

                    getAddressFromLocation(latitude, longitude)

                    Toast.makeText(this, "Latitude: $latitude, Longitude: $longitude", Toast.LENGTH_SHORT).show()
                    Log.d("Location", "Latitude: $latitude, Longitude: $longitude")
                    // Use the latitude and longitude here
                } else {
                    Log.d("Location", "Location not found")
                }
            }
            .addOnFailureListener { exception ->
                Log.e("Location", "Failed to get location", exception)
            }
    }


    private fun getAddressFromLocation(latitude: Double, longitude: Double) {
        val geocoder = Geocoder(this, Locale.getDefault())

        try {
            // Get the address list based on latitude and longitude
            val addresses: MutableList<Address>? = geocoder.getFromLocation(latitude, longitude, 1)

            if (addresses != null) {
                if (addresses.isNotEmpty()) {
                    val address: Address? = addresses?.get(0)

                    // Full address (long address)
                    val fullAddress = address?.getAddressLine(0)
                    Log.d("Location", "Full Address: $fullAddress")

                    // Short address (locality/city name)
                    val city = address?.locality
                    Log.d("Location", "City/Locality: $city")

                    // Other available details
                    val postalCode = address?.postalCode
                    val country = address?.countryName

                    Log.d("Location", "Postal Code: $postalCode")
                    Log.d("Location", "Country: $country")

                    Toast.makeText(this, "Latitude: $fullAddress, Longitude: $city", Toast.LENGTH_SHORT).show()
                } else {
                    Log.d("Location", "No address found for the location")
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
            Log.e("Location", "Unable to get address from the latitude and longitude")
        }
    }



}


