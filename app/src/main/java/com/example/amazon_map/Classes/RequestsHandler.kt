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

/**
 * This class is used to handle the requests of the user
 * @param activity the activity that the user is in
 * @property sharedPreferences the shared preferences object
 * @property fusedLocationProvider the fused location provider object
 * @property lastLocation the last location of the user
 * @property TAG the tag of the class
 * @property locationRequest the location request object
 * @property locationCallback the location callback object

 */
class RequestsHandler(
    private var activity: Activity,
    private val sharedPreferences: MyPreferences
) {
    private var fusedLocationProvider: FusedLocationProviderClient? = null
    private var lastLocation: Location? = null
    private var TAG = "LocationFragment"

    /**
     * This function is used to check if the user has the location permission
     * if the user has the permission then it will request the location updates
     * if the user doesn't have the permission then it will request the permission
     */
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
            //permission granted
            fusedLocationProvider?.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        }
    }

    /**
     * This function is used to request the location permission
     */
    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
            ),
            MY_PERMISSIONS_REQUEST_LOCATION
        )
    }

    /**
     * This function is used to get the location of the user
     * @return the location of the user
     */
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
                val location = Location()
                //here we save the location of the user in the shared preferences
                location.latitude = lastLocation!!.latitude
                location.longitude = lastLocation!!.longitude
                sharedPreferences.saveLocation(location)
            }
        }
    }


    //those are lifecycle functions
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

    /**
     * This function is used to handle the result of the permission request
     * @param requestCode the request code of the permission
     * @param permissions the permissions that the user requested
     * @param grantResults the results of the permission request
     */
    fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        //here we check if the user granted the permission or not
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_LOCATION -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(
                            activity,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        //here we request the location updates
                        fusedLocationProvider?.requestLocationUpdates(
                            locationRequest,
                            locationCallback,
                            Looper.getMainLooper()
                        )
                    }

                } else {
                    //here we show a toast to the user to tell him that the permission is denied
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

    //those are lifecycle functions
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