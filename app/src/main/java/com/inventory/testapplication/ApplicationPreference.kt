
package com.inventory.testapplication

import android.content.Context

object ApplicationPreference {


    fun setCameraPermissionGranted(isCameraPermissionGranted: Boolean, context: Context) {
        val sharedPreference = context.getSharedPreferences("application", Context.MODE_PRIVATE)
        val editor = sharedPreference.edit()
        editor.putBoolean("camPermission", isCameraPermissionGranted).apply()
    }

    fun setLocationPermissionGranted(isLocationPermissionGranted: Boolean, context: Context) {
        val sharedPreference = context.getSharedPreferences("application", Context.MODE_PRIVATE)
        val editor = sharedPreference.edit()
        editor.putBoolean("locationPermission", isLocationPermissionGranted).apply()
    }

    fun setGalleryPermissionGranted(isGalleryPermissionGranted: Boolean, context: Context) {
        val sharedPreference = context.getSharedPreferences("application", Context.MODE_PRIVATE)
        val editor = sharedPreference.edit()
        editor.putBoolean("galleryPermission", isGalleryPermissionGranted).apply()
    }

    fun getCameraPermission(context: Context) : Boolean {
        val sharedPreference = context.getSharedPreferences("application", Context.MODE_PRIVATE)
        return sharedPreference.getBoolean("camPermission",false)
    }

    fun getLocationPermission(context: Context) : Boolean {
        val sharedPreference = context.getSharedPreferences("application", Context.MODE_PRIVATE)
        return sharedPreference.getBoolean("locationPermission",false)
    }

    fun getGalleryPermission(context: Context) : Boolean {
        val sharedPreference = context.getSharedPreferences("application", Context.MODE_PRIVATE)
        return sharedPreference.getBoolean("galleryPermission",false)
    }


}
