package com.inventory.testapplication.New

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.TextView
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
import com.inventory.testapplication.databinding.PopupMenuBinding

class SpinnerLikeThing : AppCompatActivity()  {
    private lateinit var binding: ActivitySpinnerLikeThingBinding
    private var isPopupMenuOpen = false



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


            val drawable = ContextCompat.getDrawable(this, R.drawable.ic_collapse)
            binding.manualLocation.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null)


         //   showPopupMenu(it)

            showCustomPopup(it)
        }

    }


    private fun showCustomPopup(anchorView: View) {
        val bindingPopup = PopupMenuBinding.inflate(LayoutInflater.from(this))

        val popupWindow = PopupWindow(
            bindingPopup.root,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        )


        bindingPopup.popupText.setOnClickListener {

            binding.manualLocation.setText("Auto Location")

            val drawable = ContextCompat.getDrawable(this, R.drawable.ic_not_collapse)
            binding.manualLocation.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null)
            popupWindow.dismiss()
        }

        bindingPopup.popupText2.setOnClickListener {
            binding.manualLocation.setText("Manual Location")

            val drawable = ContextCompat.getDrawable(this, R.drawable.ic_not_collapse)
            binding.manualLocation.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null)



            popupWindow.dismiss()
        }

        popupWindow.setOnDismissListener {
            val drawable = ContextCompat.getDrawable(this, R.drawable.ic_not_collapse)
            binding.manualLocation.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null)
        }



        popupWindow.showAsDropDown(anchorView, 0, 10)
    }
}