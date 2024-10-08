package com.inventory.testapplication

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.WindowManager
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.inventory.testapplication.databinding.CustomDialogForDeleteBinding

class ManualScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_manual_screen)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        innit()
    }

    private fun innit() {
        showCustomBottomDialog(this)
    }

    fun showCustomBottomDialog(context: Context) {
        val dialog = Dialog(context)
        val binding: CustomDialogForDeleteBinding = CustomDialogForDeleteBinding.inflate(LayoutInflater.from(context))

        // Set the custom layout for the dialog
        dialog.setContentView(binding.root)



        // Configure the dialog to appear at the bottom
        dialog.window?.let { window ->
            val params: WindowManager.LayoutParams = window.attributes
            params.gravity = Gravity.BOTTOM
            params.width = WindowManager.LayoutParams.MATCH_PARENT
         //   params.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND
            window.attributes = params
            window.setBackgroundDrawableResource(android.R.color.transparent)
        }

        dialog.setCancelable(true)

        // Show the dialog
        dialog.show()
    }

    private fun showCustomDialog() {
        // Use ViewBinding to inflate the custom layout
        val dialogBinding = CustomDialogForDeleteBinding.inflate(layoutInflater)

        // Create the AlertDialog and set the custom view with binding.root
        val dialog = AlertDialog.Builder(this)
            .setView(dialogBinding.root)
            .setCancelable(true) // Allow the user to cancel the dialog
            .create()
        dialog.show()

        val window = dialog.window
        if (window != null) {
            val params: WindowManager.LayoutParams = window.attributes
            params.gravity = Gravity.BOTTOM // Set the gravity to the bottom
            params.width = WindowManager.LayoutParams.MATCH_PARENT // Set width to match parent for full screen width
            params.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND // Dim the background when dialog appears
            window.attributes = params
            window.setBackgroundDrawableResource(android.R.color.transparent) // Make the background transparent if needed
        }
    }
}