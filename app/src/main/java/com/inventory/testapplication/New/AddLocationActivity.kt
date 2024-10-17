package com.inventory.testapplication.New

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.inventory.testapplication.R
import com.inventory.testapplication.databinding.ActivityAddLocationBinding
import com.inventory.testapplication.databinding.CustomAddLocationBottomSheetBinding

class AddLocationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddLocationBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAddLocationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.icFullImageMap.setOnClickListener {
            val bottomSheet = AddLocationBottomSheet()
            bottomSheet.show(supportFragmentManager, "CustomBottomSheet")
        }


    }
}