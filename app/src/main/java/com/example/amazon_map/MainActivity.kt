package com.example.amazon_map

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.amplifyframework.AmplifyException
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin
import com.amplifyframework.core.Amplify
import com.amplifyframework.geo.location.AWSLocationGeoPlugin
import androidx.fragment.app.Fragment
import com.example.amazon_map.Fragments.AboutFragment
import com.example.amazon_map.Fragments.GeofencesFragment
import com.example.amazon_map.Fragments.HomeFragment
import com.example.amazon_map.Fragments.SettingsFragment
import com.example.amazon_map.Fragments.TrackersFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private val homeFragment = HomeFragment()
    private val trackersFragment = TrackersFragment()
    private val geofencesFragment = GeofencesFragment()
    private val settingsFragment = SettingsFragment()
    private val aboutFragment = AboutFragment()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initAmplify()
        setContentView(R.layout.activity_main)
        val navView: BottomNavigationView = findViewById(R.id.bottom_navigation_view)
        navView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)
        switchFragment(homeFragment)
    }

    private fun initAmplify() {
        try {
            Amplify.addPlugin(AWSCognitoAuthPlugin())
            Amplify.addPlugin(AWSLocationGeoPlugin())
            Amplify.configure(applicationContext) //applicationContext
            Log.i("AndroidQuickStart", "Initialized Amplify")
        } catch (error: AmplifyException) {
            Log.e("AndroidQuickStart", "Could not initialize Amplify", error)
        }
    }

    private fun switchFragment(fragment: Fragment) {
        val fragmentTransaction =
            supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragment_container, fragment)
        fragmentTransaction.commit()

    }

    private val onNavigationItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    switchFragment(homeFragment)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_trackers -> {
                    switchFragment(trackersFragment)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_geofences -> {
                    switchFragment(geofencesFragment)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_settings -> {
                    switchFragment(settingsFragment)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_about -> {
                    switchFragment(aboutFragment)
                    return@OnNavigationItemSelectedListener true
                }

            }
            false
        }

}
