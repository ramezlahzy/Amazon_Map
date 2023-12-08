package com.example.amazon_map.API

import com.example.amazon_map.Classes.Searches
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class API {
    val db = Firebase.firestore
    //location
    fun getUser():String{
        return "user"
    }
    fun getAllSearchesInRange(location1:Searches, location2:Searches):ArrayList<Searches>{
        var locations=ArrayList<Searches>()
        db.collection("locations").whereGreaterThanOrEqualTo("latitude",location1.getLatitude()).whereLessThanOrEqualTo("latitude",location2.getLatitude()).get().addOnSuccessListener {
            for (document in it){
                var location=Searches(document.get("latitude").toString().toDouble(),document.get("longitude").toString().toDouble())
                locations.add(location)
            }
        }
        return locations
    }



}