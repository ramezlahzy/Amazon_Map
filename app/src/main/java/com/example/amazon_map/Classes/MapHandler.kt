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
import com.example.amazon_map.Fragments.HomeFragment
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapboxMap

class MapHandler {
    private  var mapView: MapLibreView
    private  var searchEditText: AutoCompleteTextView
    private  var descriptionView: TextView
    private  var locationPoint: ImageView
    private val mapLableLocation: MutableMap<String, HomeFragment.Location> = mutableMapOf()
    private var context: HomeFragment? = null

    constructor(mapView: MapLibreView, searchEditText: AutoCompleteTextView, descriptionView: TextView, locationPoint: ImageView, context: HomeFragment? = null){
        this.mapView=mapView
        this.searchEditText=searchEditText
        this.descriptionView=descriptionView
        this.locationPoint=locationPoint
        this.context=context
        searchEditText.setOnItemClickListener { parent, _, position, _ ->
            val selectedPlace = parent.getItemAtPosition(position) as String
            moveCameraToSelectedPlace(mapLableLocation[selectedPlace]!!)
        }
        searchEditText.doOnTextChanged { text, _, _, _ ->
            if (text.isNullOrBlank()) {
                return@doOnTextChanged
            }
            Amplify.Geo.searchByText(text.toString(),
                { result ->
                    val places = result.places
                    val locations = places.map { place ->
                        val amazonPlace = (place as AmazonLocationPlace)
                        val location = HomeFragment.Location()
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
            map.addOnCameraMoveStartedListener { toggleDescriptionText() }
            map.addOnCameraIdleListener { reverseGeocode(map) }
        }
    }
    private fun moveCameraToSelectedPlace(selectedPlace: HomeFragment.Location) {
        val newCameraPosition = CameraPosition.Builder()
            .target(
                LatLng(
                    selectedPlace.latitude,
                    selectedPlace.longitude
                )
            )
            .zoom(DEFAULT_ZOOM_LEVEL.toDouble())
            .build()
        mapView.getMapAsync { map ->
            map.cameraPosition = newCameraPosition
        }
    }
    private fun reverseGeocode(
        map: MapboxMap
    ) {
        val options = GeoSearchByCoordinatesOptions.builder()
            .maxResults(1)
            .build()

//        Log.d(TAG, "reverseGeocode: " + locationPoint.x.toDouble() + " " +locationPoint.y.toDouble())
//        Log.d(TAG, "reverseGeocode: " + map.cameraPosition.target.longitude + " " + map.cameraPosition.target.latitude)

        val centerCoordinates = Coordinates().apply {
            longitude = map.cameraPosition.target.longitude
            latitude = map.cameraPosition.target.latitude
        }
        //put pins on the map
//        val place = Place.builder()
//            .coordinates(centerCoordinates)
//            .build()
//        map.getStyle { style ->
//            style.addImage("marker-icon-id", resources.getDrawable(R.drawable.mapbox_marker_icon_default))
//            style.addSource(com.amplifyframework.geo.maplibre.model.Source.builder()
//                .id("marker-source-id")
//                .place(place)
//                .build())
//            style.addLayer(com.amplifyframework.geo.maplibre.model.Layer.builder()
//                .id("marker-layer-id")
//                .sourceId("marker-source-id")
//                .type(com.amplifyframework.geo.maplibre.model.Layer.Type.SYMBOL)
//                .iconImage("marker-icon-id")
//                .iconAllowOverlap(true)
//                .iconIgnorePlacement(true)
//                .build())
//        }
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

    private fun toggleDescriptionText(label: String? = "") {
        if (label.isNullOrBlank()) {
            descriptionView.fadeOut()
        } else {
            descriptionView.text = label
            descriptionView.fadeIn()
        }
    }
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








    private val DEFAULT_ZOOM_LEVEL = 15.0f

}