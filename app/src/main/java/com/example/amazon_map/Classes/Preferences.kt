package com.example.amazon_map.Classes
import android.content.Context
import android.content.SharedPreferences

class MyPreferences(context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

    // Function to save a string value
    fun saveString(key: String, value: String) {
        val editor = sharedPreferences.edit()
        editor.putString(key, value)
        editor.apply()
    }

    // Function to retrieve a string value
    fun getString(key: String, defaultValue: String): String {
        return sharedPreferences.getString(key, defaultValue) ?: defaultValue
    }

    // Add similar functions for other data types (int, boolean, etc.) if needed
}
