package com.inventory.testapplication

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

abstract class PermissionManager(private val activity: AppCompatActivity) {

    private lateinit var cameraPermissionLauncher: ActivityResultLauncher<String>
    private lateinit var galleryPermissionLauncher: ActivityResultLauncher<Array<String>>
    private lateinit var locationPermissionLauncher: ActivityResultLauncher<String>

    init {
        setupPermissionLaunchers()
    }

    // Initialize permission launchers
    private fun setupPermissionLaunchers() {
        cameraPermissionLauncher = activity.registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted) {
                onPermissionGranted(PermissionType.CAMERA)
            } else {
                onPermissionDenied(PermissionType.CAMERA,"Manifest.permission.CAMERA Denied")
            }
        }

        galleryPermissionLauncher = activity.registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            val readMediaImagesGranted = permissions[Manifest.permission.READ_MEDIA_IMAGES] ?: false
            val readMediaVisualUserSelectedGranted = permissions[Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED] ?: false
            val readExternalStorageGranted = permissions[Manifest.permission.READ_EXTERNAL_STORAGE] ?: false
            val writeExternalStorageGranted = permissions[Manifest.permission.WRITE_EXTERNAL_STORAGE] ?: false

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                // Android 14+ prefers READ_MEDIA_IMAGES and READ_MEDIA_VISUAL_USER_SELECTED

                if (readMediaImagesGranted && readMediaVisualUserSelectedGranted) {
                    onPermissionGranted(PermissionType.GALLERY)
                } else if (readMediaImagesGranted) {
                    // Handle partial access when only READ_MEDIA_IMAGES is granted
                    onPermissionGranted(PermissionType.GALLERY)
                    onPartialPermissionGranted(
                        PermissionType.GALLERY,
                        "Partial Access: Only READ_MEDIA_IMAGES granted"
                    )
                } else if (readMediaVisualUserSelectedGranted) {
                    // Handle partial access when only READ_MEDIA_VISUAL_USER_SELECTED is granted
                 //   onPermissionGranted(PermissionType.GALLERY)
                    onPartialPermissionGranted(
                        PermissionType.GALLERY,
                        "Partial Access: Only READ_MEDIA_VISUAL_USER_SELECTED granted"
                    )
                } else {
                    onPermissionDenied(PermissionType.GALLERY, "Media permissions denied")
                }
            } else {
                // Android 7 to 12 uses READ_EXTERNAL_STORAGE and WRITE_EXTERNAL_STORAGE
                if (readExternalStorageGranted && writeExternalStorageGranted) {
                    onPermissionGranted(PermissionType.GALLERY)
                } else {
                    onPermissionDenied(PermissionType.GALLERY, "Read/Write permission denied")
                }
            }
        }

        locationPermissionLauncher = activity.registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted) {
                onPermissionGranted(PermissionType.LOCATION)
            } else {
                onPermissionDenied(PermissionType.LOCATION,"Manifest.permission.ACCESS_FINE_LOCATION Denied")
            }
        }
    }

    // Request Camera permission
    fun requestCameraPermission() {
        when {
            ContextCompat.checkSelfPermission(
                activity,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                onPermissionGranted(PermissionType.CAMERA)
            }

            else -> {
                cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    fun requestGalleryPermission() {
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE -> {
                // Android 14+ - Check for both READ_MEDIA_IMAGES and READ_MEDIA_VISUAL_USER_SELECTED
                galleryPermissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.READ_MEDIA_IMAGES,
                        Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED // Handle partial access for Android 14+
                    )
                )
            }

            Build.VERSION.SDK_INT == Build.VERSION_CODES.TIRAMISU -> {
                // Android 13
                galleryPermissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.READ_MEDIA_IMAGES
                    )
                )
            }

            Build.VERSION.SDK_INT in Build.VERSION_CODES.N..Build.VERSION_CODES.S_V2 -> {
                // Android 7 to 12
                galleryPermissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
                )
            }

            else -> {
                // Handle devices below Android 7
                onPermissionDenied(
                    PermissionType.GALLERY,
                    "Manifest.permission.READ_EXTERNAL_STORAGE and Manifest.permission.WRITE_EXTERNAL_STORAGE Denied"
                )
            }
        }
    }

    // Request Location permission
    fun requestLocationPermission() {
        when {
            ContextCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                onPermissionGranted(PermissionType.LOCATION)
            }

            else -> {
                locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }

    // Abstract functions to override for handling granted and denied permissions
    abstract fun onPermissionGranted(permissionType: PermissionType)
    abstract fun onPermissionDenied(permissionType: PermissionType, message: String)
    abstract fun onPartialPermissionGranted(permissionType: PermissionType, message: String)


    // Enum to differentiate permission types
    enum class PermissionType {
        CAMERA, GALLERY, LOCATION
    }
}
