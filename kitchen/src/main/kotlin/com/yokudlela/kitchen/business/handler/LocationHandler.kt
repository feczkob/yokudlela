package com.yokudlela.kitchen.business.handler

import com.yokudlela.kitchen.business.model.Location

interface LocationHandler {

    fun saveLocation(location: Location): Location
    fun loadLocation(locationId: Int): Location
    fun deleteLocation(locationId: Int)
    fun loadLocations(pageNumber: Int, pageSize: Int): List<Location>
}