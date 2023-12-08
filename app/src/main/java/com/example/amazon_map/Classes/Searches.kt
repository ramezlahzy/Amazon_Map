package com.example.amazon_map.Classes

/**
 * This class is used to save the searches of the users
 * @property latitude the latitude of the search
 * @property longitude the longitude of the search
 * @property userId the id of the user
 * @property searchWord the word that the user searched for
 */
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