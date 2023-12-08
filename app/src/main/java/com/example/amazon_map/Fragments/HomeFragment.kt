package com.example.amazon_map.Fragments

import android.os.Bundle
import android.provider.Settings.Secure
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.amplifyframework.core.Amplify
import com.example.amazon_map.API.API
import com.example.amazon_map.Classes.Location
import com.example.amazon_map.Classes.MapHandler
import com.example.amazon_map.Classes.MyPreferences
import com.example.amazon_map.Classes.RequestsHandler
import com.example.amazon_map.Dialog.DateDialogFragment
import com.example.amazon_map.R
import kotlinx.coroutines.runBlocking
import java.util.Calendar
import java.util.Date


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class HomeFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var mapHandler: MapHandler
    private lateinit var requestsHandler: RequestsHandler
    private lateinit var preferences: MyPreferences
    private var TAG = "HomeFragmentHomeFragment"
    private lateinit var api: API
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preferences = MyPreferences(requireActivity())
        api = API(preferences)
        requestsHandler = RequestsHandler(requireActivity(), preferences)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        val androidId = Secure.getString(requireContext().contentResolver, Secure.ANDROID_ID)
        preferences.saveUserId(androidId)
        val dialog = DateDialogFragment(this)

        if (preferences.getBirthDate() == null)
            dialog.showDialog()
//        test()
    }

    private fun test() {
        val location = Location(30.0, 5.0)
       runBlocking{
            try {
                val data = api.getAllSearchesInRange(location)
            } catch (e: Exception) {
                Log.d(TAG, "test: $e")
            }
        }
    }

    fun onDateSet(year: Int, month: Int, day: Int) {
        Log.d(TAG, "onDateSet: $year $month $day")
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month)
        calendar.set(Calendar.DAY_OF_MONTH, day)
        preferences.saveBirthDate(calendar.time)
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mapHandler = MapHandler(
            view.findViewById(R.id.mapView),
            view.findViewById(R.id.search_edit_text),
            view.findViewById(R.id.description_text_view),
            view.findViewById(R.id.draggableImageView),
            api,
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