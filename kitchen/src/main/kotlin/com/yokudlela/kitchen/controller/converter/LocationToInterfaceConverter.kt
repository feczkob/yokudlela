package com.yokudlela.kitchen.controller.converter

import com.yokudlela.kitchen.business.model.Location
import com.yokudlela.kitchen.controller.model.response.LocationResponse
import org.modelmapper.ModelMapper
import org.springframework.core.convert.converter.Converter
import org.springframework.stereotype.Component

@Component
class LocationToInterfaceConverter(
    private val modelMapper: ModelMapper,
) : Converter<Location, LocationResponse> {

    override fun convert(source: Location): LocationResponse {
        val response = modelMapper.map(source, LocationResponse::class.java)
        source.orders?.forEach { response.orderIds += it.id }
        return response
    }
}