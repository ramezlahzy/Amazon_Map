package com.example.amazon_map.Classes
import android.content.Context
import android.content.SharedPreferences
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 *This class is used to save and get data from shared preferences
 * @param context the context of the activity
 * @property sharedPreferences the shared preferences object
 */
class MyPreferences(context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)


    //save and get string values in shared preferences
    fun saveString(key: String, value: String) {
        val editor = sharedPreferences.edit()
        editor.putString(key, value)
        editor.apply()
    }

    fun getString(key: String, defaultValue: String): String {
        return sharedPreferences.getString(key, defaultValue) ?: defaultValue
    }

    //save the location of the user in the shared preferences
    fun saveLocation(location: Location){
        val editor = sharedPreferences.edit()
        editor.putString("latitude", location.latitude.toString())
        editor.putString("longitude", location.longitude.toString())
        editor.apply()
    }
    //get the location of the user from the shared preferences
    fun getLocation():Location{
        val location=Location()
        location.latitude=sharedPreferences.getString("latitude","0.0")!!.toDouble()
        location.longitude=sharedPreferences.getString("longitude","0.0")!!.toDouble()
        return location
    }
    //save the date of birth of the user in the shared preferences
    fun saveBirthDate(date:Date){
        val editor = sharedPreferences.edit()
        editor.putString("birthDate", date.toString())
        editor.apply()
    }
    //get the date of birth of the user from the shared preferences
    fun getBirthDate(): Date? {
        if (sharedPreferences.getString("birthDate", "0") == "0")
            return null
        val dateString = sharedPreferences.getString("birthDate", "0")!!
        val inputFormat = SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH)
        return inputFormat.parse(dateString);
    }
    //save the user id in the shared preferences
    fun saveUserId(userId:String){
        val editor = sharedPreferences.edit()
        editor.putString("userId", userId)
        editor.apply()
    }
    //get the user id from the shared preferences
    fun getUserId():String{
        return sharedPreferences.getString("userId","0")!!
    }

}
