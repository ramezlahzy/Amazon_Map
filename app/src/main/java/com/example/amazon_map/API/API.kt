package com.example.amazon_map.API

import android.content.SharedPreferences
import android.util.Log
import com.example.amazon_map.Classes.Location
import com.example.amazon_map.Classes.MyPreferences
import com.example.amazon_map.Classes.Searches
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await


class API(private val sharedPreferences: MyPreferences) {
    private val TAG = "APIFragment"
    private val db = Firebase.firestore
    suspend fun getAllSearchesInRange(location: Location)
            : List<Searches> {
        val collectionReference = db.collection("searches")
        return try {
            val querySnapshot = collectionReference
                .whereGreaterThanOrEqualTo("latitude", location.latitude - 30)
                .whereLessThanOrEqualTo("latitude", location.latitude + 30)
                .get()
                .await()
           val filtered= querySnapshot.documents.filter { documentSnapshot ->
                val longitude = documentSnapshot.get("longitude").toString().toDouble()
                longitude >= location.longitude - 30 && longitude <= location.longitude + 30
            }
            val searchList = filtered.map { document ->
                Searches(
                    latitude = document.getDouble("latitude") ?: 0.0,
                    longitude = document.getDouble("longitude") ?: 0.0,
                    searchWord = document.getString("searchWord") ?: "",
                    userId = document.getString("userId") ?: "",
                )
            }
            searchList
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun saveSearchWord(searchWord: String) {
        if (!checkIfUserExist(sharedPreferences.getUserId())) {
            saveUser()
        }
        val search = hashMapOf(
            "latitude" to sharedPreferences.getLocation().latitude,
            "longitude" to sharedPreferences.getLocation().longitude,
            "searchWord" to searchWord,
            "userId" to sharedPreferences.getUserId(),
        )
        db.collection("searches").add(search)
    }

    private suspend fun checkIfUserExist(userId: String): Boolean {
        var exist = false
        val querySnapshot = db.collection("users").whereEqualTo("userId", userId).get().await()
        if (querySnapshot.documents.size > 0)
            exist = true
        return exist
    }

    private fun saveUser() {
        val user = hashMapOf(
            "userId" to sharedPreferences.getUserId(),
            "birthDate" to sharedPreferences.getBirthDate(),
        )
        db.collection("users").add(user)
    }
}