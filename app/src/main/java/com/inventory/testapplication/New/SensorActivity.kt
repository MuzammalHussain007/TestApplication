package com.inventory.testapplication.New

import android.content.Context
import android.content.IntentFilter
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.inventory.testapplication.R
import com.inventory.testapplication.databinding.ActivitySensorBinding

class SensorActivity : AppCompatActivity() , SensorEventListener {
    private lateinit var sensorManager: SensorManager
    private var gyroscope: Sensor? = null

    private var rotationX = 0.0f
    private var rotationY = 0.0f
    private var rotationZ = 0.0f

    private var rotationAngle = 0f


    private val connectivityReceiver = ConnectivityReceiver()



    private var timestamp: Long = 0
    private lateinit var binding: ActivitySensorBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySensorBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        innit()
    }

    private fun innit() {
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
    }

    override fun onResume() {
        super.onResume()

        val filter = IntentFilter()
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE")
        registerReceiver(connectivityReceiver, filter)
        // Register the gyroscope listener
        gyroscope?.also { gyro ->
            sensorManager.registerListener(this, gyro, SensorManager.SENSOR_DELAY_FASTEST)
        }
    }


    override fun onPause() {
        super.onPause()
        // Unregister the sensor listener when the activity is paused
        sensorManager.unregisterListener(this)
        unregisterReceiver(connectivityReceiver)

    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_GYROSCOPE) {
            val rotationZ = event.values[2] // Rotation around the Z-axis

            // Update the rotation angle
            rotationAngle += rotationZ * 2f // Adjust for smoother rotation

            // Apply rotation to the ImageView
            binding.imageView.rotation = rotationAngle

            // Change color dynamically based on rotation
            when {
                isAngleCloseTo(rotationAngle, 90f) -> binding.imageView.clearColorFilter()
                isAngleCloseTo(rotationAngle, 180f) -> binding.imageView.setColorFilter(Color.GREEN)
                isAngleCloseTo(rotationAngle, 270f) -> binding.imageView.clearColorFilter()
                else -> binding.imageView.clearColorFilter() // Clear filter if not at specified angles
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Handle accuracy changes if needed
    }

    private fun isAngleCloseTo(targetAngle: Float, angle: Float, threshold: Float = 5f): Boolean {
        return targetAngle % 360 >= angle - threshold && targetAngle % 360 <= angle + threshold
    }


}