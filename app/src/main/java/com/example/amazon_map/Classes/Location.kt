package com.example.amazon_map.Classes

class Location(
    var latitude: Double = 0.0,
    var longitude: Double = 0.0,
    var label: String? = ""
) {
    override fun toString(): String {
        return "Location(latitude=$latitude, longitude=$longitude, label=$label)"
    }
}