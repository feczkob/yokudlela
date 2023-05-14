package com.yokudlela.kitchen.business.service.converter

import com.yokudlela.kitchen.client.persistence.entity.Location
import liquibase.pro.packaged.hu
import org.modelmapper.ModelMapper
import org.springframework.beans.factory.annotation.Lookup
import org.springframework.core.convert.converter.Converter
import org.springframework.stereotype.Component

@Component
class LocationConverter(
    private val modelMapper: ModelMapper,
    // TODO dont implement this interface, make methods 'toEntity', 'toBusiness'
) : Converter<Location, com.yokudlela.kitchen.business.model.Location>{

    override fun convert(source: Location): com.yokudlela.kitchen.business.model.Location {
        val location = getLocation()
        modelMapper.map(source, location)
        return location
    }

    @Lookup
    fun getLocation(): com.yokudlela.kitchen.business.model.Location {
        throw IllegalCallerException("Do not call me!")
    }
}