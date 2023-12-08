package com.example.amazon_map.Classes


/**
 * Data class to represent a location.
 * @param latitude The latitude of the location.
 * @param longitude The longitude of the location.
 * @param label The label of the location.
 */
class Location(
    var latitude: Double = 0.0,
    var longitude: Double = 0.0,
    var label: String? = ""
) {
    override fun toString(): String {
        return "Location(latitude=$latitude, longitude=$longitude, label=$label)"
    }
}