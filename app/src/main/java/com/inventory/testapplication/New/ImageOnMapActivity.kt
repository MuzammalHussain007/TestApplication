package com.inventory.testapplication.New

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.inventory.testapplication.R
import com.inventory.testapplication.databinding.ActivityImageOnMapBinding

class ImageOnMapActivity : AppCompatActivity() {
    private lateinit var binding: ActivityImageOnMapBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityImageOnMapBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.icMoreMapImageMap.setOnClickListener {
            val bottomSheetFragment = MapTypeBottomSheet()
            bottomSheetFragment.show(supportFragmentManager, bottomSheetFragment.tag)
        }
    }
}