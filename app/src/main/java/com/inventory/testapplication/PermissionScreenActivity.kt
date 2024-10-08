package com.inventory.testapplication

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.inventory.testapplication.databinding.ActivityMainBinding

class PermissionScreenActivity : AppCompatActivity() {

    private var isCameraPermissionEnabled = false
    private var isLocationPermissionEnabled = false
    private var isGalleryPermissionEnabled = false

    private lateinit var binding: ActivityMainBinding

    private lateinit var permissionManager: PermissionManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        innit()

    }


    private fun innit() {
        clickListener()
        permissionManager = object : PermissionManager(this) {
            override fun onPermissionGranted(permissionType: PermissionType) {
                when (permissionType) {
                    PermissionType.CAMERA -> {
                        isCameraPermissionEnabled = true
                        binding.icCameraSwitch.setImageResource(R.drawable.ic_permession_enable_switch)
                        binding.icCameraLayout.isEnabled = false
                        ApplicationPreference.setCameraPermissionGranted(isCameraPermissionEnabled, this@PermissionScreenActivity)
                    }

                    PermissionType.GALLERY -> {
                        isGalleryPermissionEnabled = true
                        binding.icGallerySwitch.setImageResource(R.drawable.ic_permession_enable_switch)
                        binding.icGalleryLayout.isEnabled = false
                        ApplicationPreference.setGalleryPermissionGranted(isGalleryPermissionEnabled, this@PermissionScreenActivity)

                        Log.d("galleryPermission", "onPermissionGranted: ${isGalleryPermissionEnabled}  ")
                        Log.d("galleryPermission", "onPermissionGranted:  ${isCameraPermissionEnabled}  ")
                        Log.d("galleryPermission", "onPermissionGranted:  ${isLocationPermissionEnabled}  ")


                        if (isGalleryPermissionEnabled && isCameraPermissionEnabled && isLocationPermissionEnabled) {
                            binding.continueBtn.visibility = View.VISIBLE
                        } else {
                            Toast.makeText(this@PermissionScreenActivity, "Please enable all permissiona", Toast.LENGTH_SHORT).show()
                        }
                    }

                    PermissionType.LOCATION -> {
                        isLocationPermissionEnabled = true
                        binding.icLocationSwitch.setImageResource(R.drawable.ic_permession_enable_switch)
                        binding.icLocationLayout.isEnabled = false
                        ApplicationPreference.setLocationPermissionGranted(isLocationPermissionEnabled, this@PermissionScreenActivity)

                    }
                }
            }

            override fun onPermissionDenied(permissionType: PermissionType,errorMessage : String) {
                when (permissionType) {
                    PermissionType.CAMERA -> {
                        isCameraPermissionEnabled = false
                        binding.icCameraSwitch.setImageResource(R.drawable.ic_disable_permission_switch)
                        binding.icCameraLayout.isEnabled = false
                    }

                    PermissionType.GALLERY -> {
                        isGalleryPermissionEnabled = false
                        binding.icGallerySwitch.setImageResource(R.drawable.ic_disable_permission_switch)
                        binding.icGalleryLayout.isEnabled = false

                        Log.d("galleryPermission", "onPermissionDenied: $errorMessage  ")

                    }

                    PermissionType.LOCATION -> {
                        Log.d("galleryPermission", "onPermissionDenied: $errorMessage  ")

                        isLocationPermissionEnabled = false
                        binding.icGallerySwitch.setImageResource(R.drawable.ic_disable_permission_switch)
                        binding.icGalleryLayout.isEnabled = false
                    }


                }
            }

            override fun onPartialPermissionGranted(
                permissionType: PermissionType,
                message: String
            ) {

                isGalleryPermissionEnabled = true
                binding.icGallerySwitch.setImageResource(R.drawable.ic_permession_enable_switch)
                binding.icGalleryLayout.isEnabled = false
                ApplicationPreference.setGalleryPermissionGranted(isGalleryPermissionEnabled, this@PermissionScreenActivity)

                if (isGalleryPermissionEnabled && isCameraPermissionEnabled && isLocationPermissionEnabled) {
                    binding.continueBtn.visibility = View.VISIBLE
                } else {
                    Toast.makeText(this@PermissionScreenActivity, "Please enable all permissiona", Toast.LENGTH_SHORT).show()
                }

            }
        }


    }


    private fun clickListener() {
        binding.continueBtn.setOnClickListener {
            navigateToActivity<ManualScreenActivity>(clearStack = true)
        }
        binding.icCameraSwitch.setOnClickListener {
            Log.e("innerLayout", "clickListener:)")
            permissionManager.requestCameraPermission()

        }

        binding.icLocationSwitch.setOnClickListener {
            permissionManager.requestLocationPermission()

        }

        binding.icGallerySwitch.setOnClickListener {
            permissionManager.requestGalleryPermission()

        }

    }


    override fun onResume() {
        super.onResume()
        isCameraPermissionEnabled = ApplicationPreference.getCameraPermission(this)
        isLocationPermissionEnabled = ApplicationPreference.getLocationPermission(this)
        isGalleryPermissionEnabled = ApplicationPreference.getGalleryPermission(this)

        if (isGalleryPermissionEnabled)
        {
            binding.icGalleryLayout.isEnabled = false
        }
        if (isCameraPermissionEnabled)
        {
            binding.icCameraLayout.isEnabled = false
        }
        if (isLocationPermissionEnabled)
        {
            binding.icLocationLayout.isEnabled = false
        }

    }


}