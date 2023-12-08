package com.example.amazon_map.Fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.amplifyframework.core.Amplify
import com.example.amazon_map.R
import com.example.amazon_map.Classes.MapHandler
import com.example.amazon_map.Classes.MyPreferences
import com.example.amazon_map.Classes.RequestsHandler

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class HomeFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var mapHandler: MapHandler
    private lateinit var requestsHandler: RequestsHandler
    private lateinit var preferences: MyPreferences

    private var TAG = "HomeFragmentHomeFragment"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preferences = MyPreferences(requireActivity())
        requestsHandler = RequestsHandler(requireActivity(),preferences)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }






    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        requestsHandler.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }



    override fun onResume() {
        super.onResume()
        mapHandler.onResume()
        requestsHandler.onResume()
    }
    override fun onPause() {
        super.onPause()
        mapHandler.onPause()
        requestsHandler.onPause()
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }
    class Location {
        var longitude: Double = 0.0
        var latitude: Double = 0.0
        var label: String? = ""
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mapHandler = MapHandler(
            view.findViewById(R.id.mapView),
            view.findViewById(R.id.search_edit_text),
            view.findViewById(R.id.description_text_view),
            view.findViewById(R.id.draggableImageView),
            this
        )
    }
    override fun onStart() {
        Log.d("AmazonActivity", "AmazonActivityonStart: " + Amplify.Auth.currentUser)
        super.onStart()
        mapHandler.onStart()
    }
    override fun onStop() {
        super.onStop()
        mapHandler.onStop()
    }
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapHandler.onSaveInstanceState(outState)
    }
    override fun onLowMemory() {
        super.onLowMemory()
        mapHandler.onLowMemory()
    }
    override fun onDestroy() {
        super.onDestroy()
        mapHandler.onDestroy()
    }


}