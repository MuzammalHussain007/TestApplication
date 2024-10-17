package com.inventory.testapplication.New

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.inventory.testapplication.R

class MarkerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_marker)

    }

    /*fun getMarkerBitmapFromView(context: Context, imageResId: Int): Bitmap {
      // Inflate the custom marker layout
 //     val markerView = LayoutInflater.from(context).inflate(R.layout.custom_marker_layout, null)

      // Find the ImageView in the layout
  //    val imageView = markerView.findViewById<ShapeableImageView>(R.id.custom_marker_image)

      // Set the dynamic image to the ImageView
   //   imageView.setImageResource(imageResId)

      // Measure and layout the view
  *//*    markerView.measure(
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED))
        markerView.layout(0, 0, markerView.measuredWidth, markerView.measuredHeight)

        // Create the bitmap and draw the view into it
        val bitmap = Bitmap.createBitmap(markerView.measuredWidth, markerView.measuredHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        markerView.draw(canvas)*//*

        return bitmap
    }*/







// This code will in MapView

/*
    // Get the custom marker bitmap from XML layout and set dynamic image
    val customMarkerBitmap = getMarkerBitmapFromView(this, R.drawable.your_dynamic_image)

    // Add the custom marker to the map
    mMap.addMarker(
    MarkerOptions()
    .position(location)
    .title("Custom Marker")
    .icon(BitmapDescriptorFactory.fromBitmap(customMarkerBitmap))*/
}