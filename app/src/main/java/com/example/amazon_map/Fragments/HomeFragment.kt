package com.example.amazon_map.Fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.ImageView
import android.widget.TextView
import androidx.core.widget.doOnTextChanged
import com.amazonaws.mobile.auth.core.internal.util.ThreadUtils.runOnUiThread
import com.amplifyframework.AmplifyException
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin
import com.amplifyframework.core.Amplify
import com.amplifyframework.geo.location.AWSLocationGeoPlugin
import com.amplifyframework.geo.location.models.AmazonLocationPlace
import com.amplifyframework.geo.maplibre.view.MapLibreView
import com.amplifyframework.geo.maplibre.view.support.fadeIn
import com.amplifyframework.geo.maplibre.view.support.fadeOut
import com.amplifyframework.geo.models.Coordinates
import com.amplifyframework.geo.models.Place
import com.amplifyframework.geo.options.GeoSearchByCoordinatesOptions
import com.example.amazon_map.R
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapboxMap

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
     lateinit var mapView: MapLibreView
    private lateinit var searchEditText: AutoCompleteTextView
    private lateinit var descriptionView: TextView
    private lateinit var locationPoint: ImageView
    private var TAG = "HomeFragmentHomeFragment"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    //create class called location
    class Location {
        var longitude: Double = 0.0
        var latitude: Double = 0.0
        var label: String? = ""
    }
    //create a map
    private val mapLableLocation: MutableMap<String, Location> = mutableMapOf()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mapView = view.findViewById(R.id.mapView)
        locationPoint = view.findViewById(R.id.draggableImageView)
        descriptionView = view.findViewById(R.id.description_text_view)
        searchEditText = view.findViewById(R.id.search_edit_text)
        searchEditText.setOnItemClickListener { parent, _, position, _ ->
            val selectedPlace = parent.getItemAtPosition(position) as String
            moveCameraToSelectedPlace(mapLableLocation[selectedPlace ]!!)
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
                        val location = Location()
                        location.label = amazonPlace.label
                        location.latitude = amazonPlace.coordinates.latitude
                        location.longitude = amazonPlace.coordinates.longitude
                        mapLableLocation[location.label!!] = location
                        location.label
                    }

                    runOnUiThread {
                        val adapter = ArrayAdapter(
                            requireContext(),
                            android.R.layout.simple_dropdown_item_1line,
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
//        locationPoint.setOnClickListener { point ->
//            locationPoint.x = point.x
//            locationPoint.y = point.y
////            reverseGeocode()
//            Log.d(TAG, "onViewCreated: x=" + point.x + " y=" + point.y)
//        }
    }
    private fun moveCameraToSelectedPlace(selectedPlace: Location) {
        val newCameraPosition = CameraPosition.Builder()
            .target(LatLng(selectedPlace.latitude, selectedPlace.longitude)) // Replace with the new coordinates
            .zoom(
                DEFAULT_ZOOM_LEVEL.toDouble()
            ) // Replace with the new zoom level
            .build()
//        mapView.camera
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

        Log.d(TAG, "reverseGeocode: " + locationPoint.x.toDouble() + " " +locationPoint.y.toDouble())
        Log.d(TAG, "reverseGeocode: " + map.cameraPosition.target.longitude + " " + map.cameraPosition.target.latitude)

        val centerCoordinates = Coordinates().apply {
//            longitude = //locationPoint.x.toDouble()
                longitude =   map.cameraPosition.target.longitude

//            latitude =// locationPoint.y.toDouble()
            latitude =     map.cameraPosition.target.latitude
        }
        Amplify.Geo.searchByCoordinates(centerCoordinates, options,
            { result ->
                result.places.firstOrNull()?.let { place ->
                    val amazonPlace = (place as AmazonLocationPlace)
                    runOnUiThread { toggleDescriptionText(amazonPlace.label) }
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

    override fun onStart() {
        Log.d("AmazonActivity", "AmazonActivityonStart: " + Amplify.Auth.currentUser)
        super.onStart()
        mapView?.onStart()
    }

    override fun onResume() {
        super.onResume()
        mapView?.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView?.onPause()
    }

    override fun onStop() {
        super.onStop()
        mapView?.onStop()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView?.onSaveInstanceState(outState)
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView?.onLowMemory()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView?.onDestroy()
    }

    private val DEFAULT_ZOOM_LEVEL = 15.0f

}