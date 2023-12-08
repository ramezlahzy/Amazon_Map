package com.example.amazon_map.Classes
import android.content.Context
import android.content.SharedPreferences
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MyPreferences(context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

    fun saveString(key: String, value: String) {
        val editor = sharedPreferences.edit()
        editor.putString(key, value)
        editor.apply()
    }

    fun getString(key: String, defaultValue: String): String {
        return sharedPreferences.getString(key, defaultValue) ?: defaultValue
    }
    fun saveLocation(location: Location){
        val editor = sharedPreferences.edit()
        editor.putString("latitude", location.latitude.toString())
        editor.putString("longitude", location.longitude.toString())
        editor.apply()
    }
    fun getLocation():Location{
        val location=Location()
        location.latitude=sharedPreferences.getString("latitude","0.0")!!.toDouble()
        location.longitude=sharedPreferences.getString("longitude","0.0")!!.toDouble()
        return location
    }
    fun saveBirthDate(date:Date){
        val editor = sharedPreferences.edit()
        editor.putString("birthDate", date.toString())
        editor.apply()
    }
    fun getBirthDate(): Date? {
        if (sharedPreferences.getString("birthDate", "0") == "0")
            return null
        val dateString = sharedPreferences.getString("birthDate", "0")!!
        val inputFormat = SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH)
        return inputFormat.parse(dateString);
    }
    fun saveUserId(userId:String){
        val editor = sharedPreferences.edit()
        editor.putString("userId", userId)
        editor.apply()
    }
    fun getUserId():String{
        return sharedPreferences.getString("userId","0")!!
    }

}
