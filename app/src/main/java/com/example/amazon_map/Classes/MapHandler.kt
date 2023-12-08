package com.example.amazon_map.Classes

import android.R
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.ImageView
import android.widget.TextView
import androidx.core.widget.doOnTextChanged
import com.amazonaws.mobile.auth.core.internal.util.ThreadUtils
import com.amplifyframework.core.Amplify
import com.amplifyframework.geo.location.models.AmazonLocationPlace
import com.amplifyframework.geo.maplibre.view.MapLibreView
import com.amplifyframework.geo.maplibre.view.support.fadeIn
import com.amplifyframework.geo.maplibre.view.support.fadeOut
import com.amplifyframework.geo.models.Coordinates
import com.amplifyframework.geo.options.GeoSearchByCoordinatesOptions
import com.example.amazon_map.API.API
import com.example.amazon_map.Fragments.HomeFragment
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.plugins.annotation.SymbolOptions
import kotlinx.coroutines.runBlocking


// this class is responsible for all the map actions
/**
 * @param map The map object whose center point to reverse geocode.
 * @param searchEditText The search edit text view.
 * @param descriptionView The description text view.
 * @param locationPoint The location point image view.
 * @param api The API object.
 * @param context The context of the fragment.
 * @constructor Creates a map handler object.
 *  */

class MapHandler(
    private var mapView: MapLibreView,
    private var searchEditText: AutoCompleteTextView,
    private var descriptionView: TextView,
    private var locationPoint: ImageView,
    private var api: API,
    private var context: HomeFragment? = null
) {
    private val mapLableLocation: MutableMap<String, Location> = mutableMapOf()


    // Move the camera to the selected place and update searches in that location by calling the API
    private fun moveCameraToSelectedPlace(selectedPlace: Location) {
        // Move the camera to the selected place
        val newCameraPosition = CameraPosition.Builder()
            .target(
                LatLng(
                    selectedPlace.latitude,
                    selectedPlace.longitude
                )
            )
            .zoom(DEFAULT_ZOOM_LEVEL.toDouble())
            .build()

        // Update the map
        mapView.getMapAsync { map ->
            map.cameraPosition = newCameraPosition
        }
        // Update searches in that location
        updateSearches(selectedPlace)
    }

    // Update searches in that location by calling the API
    fun updateSearches(location: Location) {

        runBlocking {
            val searches = api.getAllSearchesInRange(location)
            searches.forEach { search ->
                mapView.getStyle { map, style ->
                    val spaceNeedle = LatLng(search.latitude!!, search.longitude!!)
                    mapView.symbolManager.create(
                        SymbolOptions()
                            .withIconImage("place")
                            .withLatLng(spaceNeedle)
                    )
                }
            }
        }
    }

    /**
     * Reverse geocodes the map's center point and displays the address in a text view.
     *  This function is called when the user stops moving the map.
     *  It calls the API to get the address of the map's center point and displays it in a text view.
     *  It also updates the searches in that location.
     */
    private fun reverseGeocode(
        map: MapboxMap
    ) {
        val options = GeoSearchByCoordinatesOptions.builder()
            .maxResults(1)
            .build()
        updateSearches(Location(
                map.cameraPosition.target.latitude,
                map.cameraPosition.target.longitude
            ))
        val centerCoordinates = Coordinates().apply {
            longitude = map.cameraPosition.target.longitude
            latitude = map.cameraPosition.target.latitude
        }
        Amplify.Geo.searchByCoordinates(centerCoordinates, options,
            { result ->
                result.places.firstOrNull()?.let { place ->
                    val amazonPlace = (place as AmazonLocationPlace)
                    ThreadUtils.runOnUiThread { toggleDescriptionText(amazonPlace.label) }
                }
            },
            { exp ->
                Log.e("AndroidQuickStart", "Failed to reverse geocode : $exp")
            }
        )
    }

    /**
     * Toggles the visibility of the description text view.
     * @param label The label to display in the description text view.
     */
    private fun toggleDescriptionText(label: String? = "") {
        if (label.isNullOrBlank()) {
            descriptionView.fadeOut()
        } else {
            descriptionView.text = label
            descriptionView.fadeIn()
        }
    }
    // Map lifecycle methods
    fun onResume() {
        mapView.onResume()
    }

    fun onPause() {
        mapView?.onPause()
    }

    fun onStart() {
        mapView?.onStart()
    }


    fun onStop() {
        mapView?.onStop()
    }

    fun onSaveInstanceState(outState: Bundle) {
        mapView?.onSaveInstanceState(outState)
    }

    fun onLowMemory() {
        mapView?.onLowMemory()
    }

    fun onDestroy() {
        mapView?.onDestroy()
    }


    // the default zoom level
    private val DEFAULT_ZOOM_LEVEL = 15.0f

    /**
     * Initializes the map handler.
     */
    init {

        // Initialize the map view
        /**
         * add a listener to the search edit text view.
         * When the user selects a place from the drop-down list, the map moves to that place.
         */
        searchEditText.setOnItemClickListener { parent, _, position, _ ->
            val selectedPlace = parent.getItemAtPosition(position) as String
            moveCameraToSelectedPlace(mapLableLocation[selectedPlace]!!)
            runBlocking {
                api.saveSearchWord(selectedPlace)
            }
        }
        /**
         * When the user types a place name, the map displays a drop-down list of places that match the search text.
         * When the user stops moving the map, the map reverse geocodes the map's center point and displays the address in a text view.
         * When the user moves the location point image view, the map displays the address of the location point in a text view.
         *
         */
        searchEditText.doOnTextChanged { text, _, _, _ ->
            if (text.isNullOrBlank()) {
                return@doOnTextChanged
            }
            Amplify.Geo.searchByText(text.toString(),
                { result ->
                    val places = result.places
                    val locations = places.map { place ->
                        val amazonPlace = (place as AmazonLocationPlace)
                        val location = Location()
                        location.label = amazonPlace.label
                        location.latitude = amazonPlace.coordinates.latitude
                        location.longitude = amazonPlace.coordinates.longitude
                        mapLableLocation[location.label!!] = location
                        location.label
                    }

                    ThreadUtils.runOnUiThread {
                        val adapter = ArrayAdapter(
                            context!!.requireContext(),
                            R.layout.simple_dropdown_item_1line,
                            locations
                        )
                        searchEditText.setAdapter(adapter)
                        searchEditText.showDropDown()
                    }
                },
                { error ->
                    Log.e("AndroidQuickStart", "Failed to search by text : $error")
                }
            )
        }

        // Initialize the location point image view

        locationPoint.setOnTouchListener(object : View.OnTouchListener {
            private var lastX: Float = 0f
            private var lastY: Float = 0f

            override fun onTouch(view: View, event: MotionEvent): Boolean {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        lastX = event.rawX
                        lastY = event.rawY
                    }

                    MotionEvent.ACTION_MOVE -> {
                        val deltaX: Float = event.rawX - lastX
                        val deltaY: Float = event.rawY - lastY

                        val newX: Float = view.x + deltaX
                        val newY: Float = view.y + deltaY

                        view.x = newX
                        view.y = newY

                        lastX = event.rawX
                        lastY = event.rawY
                    }
                }
                return true
            }
        })

        mapView.getMapAsync { map ->
            val initialPosition = LatLng(47.6160281982247, -122.32642111977668)
            map.cameraPosition = CameraPosition.Builder()
                .target(initialPosition)
                .zoom(13.0)
                .build()

            // Add a listener to the map
            map.addOnCameraMoveStartedListener { toggleDescriptionText() }
            map.addOnCameraIdleListener { reverseGeocode(map) }
        }
    }

}