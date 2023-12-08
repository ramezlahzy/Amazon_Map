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

    /**
     * Function to retrieve all searches within a specified range(the range of the camera in the map)
     * @param location The center location for the search range.
     * @return List of Searches within the specified range.
     */
    suspend fun getAllSearchesInRange(location: Location)
            : List<Searches> {
        val collectionReference = db.collection("searches")
        return try {
            // Perform a Firestore query to get searches within a latitude range
            val querySnapshot = collectionReference
                .whereGreaterThanOrEqualTo("latitude", location.latitude - 30)
                .whereLessThanOrEqualTo("latitude", location.latitude + 30)
                .get()
                .await()
            // Filter the documents based on longitude within the specified range
            val filtered= querySnapshot.documents.filter { documentSnapshot ->
                val longitude = documentSnapshot.get("longitude").toString().toDouble()
                longitude >= location.longitude - 30 && longitude <= location.longitude + 30
            }
            // Map filtered documents to Searches objects
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
            // Handle exceptions by returning an empty list
            emptyList()
        }
    }
    /**
     * Function to save a search word, creating a new user if necessary.
     * @param searchWord The search word to be saved.
     */
    suspend fun saveSearchWord(searchWord: String) {
        // Check if user exists, if not create a new user
        if (!checkIfUserExist(sharedPreferences.getUserId())) {
            saveUser()
        }
        // Save the search word

        val search = hashMapOf(
            "latitude" to sharedPreferences.getLocation().latitude,
            "longitude" to sharedPreferences.getLocation().longitude,
            "searchWord" to searchWord,
            "userId" to sharedPreferences.getUserId(),
        )
        // Add a new document with a generated ID
        db.collection("searches").add(search)
    }

    /**
     * Function to check if a user with the specified ID exists.
     * @param userId The ID of the user to check.
     * @return True if the user exists, false otherwise.
     */
    private suspend fun checkIfUserExist(userId: String): Boolean {
        var exist = false
        // Query Firestore to check if a user with the specified ID exists
        val querySnapshot = db.collection("users").whereEqualTo("userId", userId).get().await()
        // Set 'exist' to true if there are documents in the query result
        if (querySnapshot.documents.size > 0)
            exist = true
        return exist
    }

    /**
     * Function to save a new user with user ID and birth date.
     */
    private fun saveUser() {
        // Create a user object with user ID and birth date
        val user = hashMapOf(
            "userId" to sharedPreferences.getUserId(),
            "birthDate" to sharedPreferences.getBirthDate(),
        )
        // Add the user object to the "users" collection in Firestore

        db.collection("users").add(user)
    }
}