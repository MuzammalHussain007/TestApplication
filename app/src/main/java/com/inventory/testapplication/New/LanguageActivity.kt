package com.inventory.testapplication.New

import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsetsController
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.inventory.testapplication.LocationAdapter
import com.inventory.testapplication.LocationItem
import com.inventory.testapplication.R
import com.inventory.testapplication.databinding.ActivityLanguageBinding

class LanguageActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLanguageBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLanguageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setLightStatusBar()

        val locationList = listOf(
            LocationItem("Park Location", "Slade Green Park, Cedar Rd, New Gt, Australia", "N 33 31”’ 13.4304 _E 73 5’ 12.2345”", "Nice park to visit."),
            LocationItem("Office Location", "Slade Green Park, Cedar Rd, New Gt, Australia", "N 33 31”’ 13.4304 _E 73 5’ 12.2345”", "Great collection of art."),
            // Add more items...
        )

        binding.LanguageListRecyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = LocationAdapter(locationList)
        binding.LanguageListRecyclerView.adapter = adapter
    }
}