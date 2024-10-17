package com.inventory.testapplication.New

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.inventory.testapplication.R
import com.inventory.testapplication.databinding.CustomMapTypeBinding

class MapTypeBottomSheet : BottomSheetDialogFragment() {


    private var _binding: CustomMapTypeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = CustomMapTypeBinding.inflate(inflater, container, false)
        return binding.root
    }





    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}