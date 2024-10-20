package com.inventory.testapplication.New

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.media.Image
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.WindowInsetsController
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.nio.ByteBuffer

fun AppCompatActivity.setLightStatusBar() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        window.insetsController?.setSystemBarsAppearance(
            WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
            WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
        )
    } else {
        @Suppress("DEPRECATION")
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
    }
}


 fun Fragment.saveImageOldWay(image: Image) {
    val planes = image.planes
    val buffer: ByteBuffer = planes[0].buffer
    val bytes = ByteArray(buffer.remaining())
    buffer.get(bytes)


    val file = File(
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
        "captured_image_${System.currentTimeMillis()}.jpg")


    try {
        val outputStream = FileOutputStream(file)
        outputStream.use {
            it.write(bytes)
        }
        Log.d("CameraCapture", "Image saved to: ${file.absolutePath}")
    } catch (e: IOException) {
        e.printStackTrace()
        Log.e("CameraCapture", "Failed to save image: ${e.message}")
    } finally {
        image.close()
    }
}

 fun Fragment.saveImageNewWay(image: Image,context: Context) {
    val planes = image.planes
    val buffer: ByteBuffer = planes[0].buffer
    val bytes = ByteArray(buffer.remaining())
    buffer.get(bytes)


    val values = ContentValues().apply {
        put(MediaStore.Images.Media.DISPLAY_NAME, "captured_image_${System.currentTimeMillis()}.jpg")
        put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES) // Only for API 29 and above
    }


    val uri = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)

    uri?.let {
        try {
            context.contentResolver.openOutputStream(it).use { outputStream ->
                outputStream?.write(bytes)
                Log.d("CameraCapture", "Image saved to: $it")
            }
        } catch (e: IOException) {
            e.printStackTrace()
            Log.e("CameraCapture", "Failed to save image: ${e.message}")
        } finally {
            image.close()
        }
    }
}

