package com.inventory.testapplication.New

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ContextThemeWrapper
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.inventory.testapplication.R
import com.inventory.testapplication.databinding.ActivitySpinnerLikeThingBinding

class SpinnerLikeThing : AppCompatActivity()  {
    private lateinit var binding: ActivitySpinnerLikeThingBinding

    private fun showPopupMenu(view: View) {

        val contextWrapper = ContextThemeWrapper(this, R.style.RoundedPopupMenu)

        var  popupMenu = PopupMenu(contextWrapper, view)

        // Inflate the menu resource
        popupMenu.menuInflater.inflate(R.menu.menu, popupMenu.menu)


        // Set a click listener for menu items
        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.home -> {
                    // Handle Action One
                    Toast.makeText(this, "Action One clicked", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.title -> {
                    // Handle Action Two
                    Toast.makeText(this, "Action Two clicked", Toast.LENGTH_SHORT).show()
                    true
                }

                else -> false
            }
        }

        popupMenu.setOnDismissListener {

            binding.colapseImage.setImageResource(R.drawable.ic_collapse)
            Toast.makeText(this, "Popup closed", Toast.LENGTH_SHORT).show()
        }

        // Show the popup menu
        popupMenu.show()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySpinnerLikeThingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.manualLocation.setOnClickListener {
            binding.colapseImage.setImageResource(R.drawable.ic_not_collapse)
            showPopupMenu(it)
        }





    }
}