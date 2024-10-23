package com.inventory.testapplication.New

import android.app.Dialog
import android.app.ProgressDialog.show
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.inventory.testapplication.R
import android.provider.Settings


class ConnectivityReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val isConnected = isInternetAvailable(context!!)
        Log.d("ConnectivityReceiver", "Internet connected: $isConnected")

        // You can show a dialog here or send a local broadcast
        if (!isConnected) {
            showNoInternetDialog(context)  // Use the dialog function from the previous example
        }
    }

    private fun showNoInternetDialog(context: Context) {

        val dialog = Dialog(context,R.style.RoundedCornersDialog)
        val view = LayoutInflater.from(context).inflate(R.layout.custom_alert_dialog, null)

        val messageTextView: TextView = view.findViewById(R.id.messageTextView)
        val goToSettingsButton: Button = view.findViewById(R.id.goToSettingsButton)

        dialog.setContentView(view)
        dialog.setCancelable(false) // Make it non-dismissible outside

        // Set button click listener to open Settings
        goToSettingsButton.setOnClickListener {
            context.startActivity(Intent(Settings.ACTION_WIRELESS_SETTINGS))
            dialog.dismiss()
        }

        dialog.show()

    }

    private fun isInternetAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork ?: return false
            val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
            return when {
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            val networkInfo = connectivityManager.activeNetworkInfo
            return networkInfo != null && networkInfo.isConnected
        }
    }
}