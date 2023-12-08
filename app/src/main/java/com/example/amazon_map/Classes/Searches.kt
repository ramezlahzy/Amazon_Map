package com.example.amazon_map.Classes

class Searches(

    var latitude: Double? = 0.0,
    var longitude: Double? = 0.0,
    var userId: String? = "",
    var searchWord: String? = ""
) {
    override fun toString(): String {
        return "Searches(latitude=$latitude, longitude=$longitude,userId='$userId', searchWord='$searchWord')"
    }
}