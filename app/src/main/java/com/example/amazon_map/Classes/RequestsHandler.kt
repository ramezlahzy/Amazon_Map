package com.example.amazon_map.Classes

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.*
import android.location.Location
import android.net.Uri
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import  android.app.Activity

class RequestsHandler(
    private var activity: Activity,
    private val sharedPreferences: MyPreferences
) {
    private var fusedLocationProvider: FusedLocationProviderClient? = null
    private var lastLocation: Location? = null
    private var TAG = "LocationFragment"

    private fun checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    activity,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                AlertDialog.Builder(activity)
                    .setTitle("Location Permission Needed")
                    .setMessage("This app needs the Location permission, please accept to use location functionality")
                    .setPositiveButton(
                        "OK"
                    ) { _, _ ->
                        //Prompt the user once explanation has been shown
                        requestLocationPermission()
                    }
                    .create()
                    .show()
            } else {
                // No explanation needed, we can request the permission.
                requestLocationPermission()

            }
        } else {
            fusedLocationProvider?.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        }
    }
    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
            ),
           MY_PERMISSIONS_REQUEST_LOCATION
        )
    }
    private val locationRequest: LocationRequest = LocationRequest.create().apply {
        interval = 30
        fastestInterval = 10
        priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
        maxWaitTime = 60
    }

    private var locationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val locationList = locationResult.locations
            if (locationList.isNotEmpty()) {
                lastLocation = locationList.last()
                val location=Location()
                location.latitude=lastLocation!!.latitude
                location.longitude=lastLocation!!.longitude
                sharedPreferences.saveLocation(location)
//                Log.d(TAG, "onLocationResult: " + lastLocation?.latitude + " " + lastLocation?.longitude)
            }
        }
    }
     fun onResume() {
        if (ContextCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            == PackageManager.PERMISSION_GRANTED
        ) {

            fusedLocationProvider?.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        }
    }
    fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_LOCATION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(
                            activity,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        fusedLocationProvider?.requestLocationUpdates(
                            locationRequest,
                            locationCallback,
                            Looper.getMainLooper()
                        )
                    }

                } else {
                    Toast.makeText(activity, "permission denied", Toast.LENGTH_LONG).show()
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(
                            activity,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        )
                    ) {
                        activity.startActivity(
                            Intent(
                                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                Uri.fromParts("package", activity.packageName, null),
                            ),
                        )
                    }
                }
                return
            }
        }
    }

    fun onPause() {
        if (ContextCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            == PackageManager.PERMISSION_GRANTED
        ) {

            fusedLocationProvider?.removeLocationUpdates(locationCallback)
        }
    }
    companion object {
        private const val MY_PERMISSIONS_REQUEST_LOCATION = 99
    }

    init {
        fusedLocationProvider = LocationServices.getFusedLocationProviderClient(activity)
        checkLocationPermission()
    }
}