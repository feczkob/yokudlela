package com.yokudlela.kitchen.controller.converter

import com.yokudlela.kitchen.business.model.Location
import com.yokudlela.kitchen.controller.model.request.LocationRequest
import org.modelmapper.ModelMapper
import org.springframework.beans.factory.annotation.Lookup
import org.springframework.core.convert.converter.Converter
import org.springframework.stereotype.Component

@Component
class LocationToBusinessConverter(
    private val modelMapper: ModelMapper,
) : Converter<LocationRequest, Location> {

    override fun convert(source: LocationRequest): Location {
        val location = getLocation()
        modelMapper.map(source, location)
        return location
    }

    // TODO where to have the lookup? config or here
    @Lookup
    fun getLocation(): Location {
        throw IllegalCallerException("Do not call me!")
    }
}