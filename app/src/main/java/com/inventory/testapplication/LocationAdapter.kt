package com.inventory.testapplication

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.inventory.testapplication.databinding.CustomAddMapLocationBinding
import com.inventory.testapplication.databinding.CustomLanguageBinding

data class LocationItem(
    val title: String,
    val address: String,
    val latitude: String,
    val information: String
)

class LocationAdapter(private val locationList: List<LocationItem>) :
    RecyclerView.Adapter<LocationAdapter.LocationViewHolder>() {

    // ViewHolder class that uses ViewBinding
    class LocationViewHolder(val binding: CustomLanguageBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationViewHolder {
        val binding = CustomLanguageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LocationViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LocationViewHolder, position: Int) {
        val location = locationList[position]
        // Bind data to views using ViewBinding
        holder.binding.apply {
            /*headTV.text = location.title
            AddressTV.text = location.address
            PointsTV.text = location.latitude.toString()*/
        }
    }

    override fun getItemCount(): Int = locationList.size
}
