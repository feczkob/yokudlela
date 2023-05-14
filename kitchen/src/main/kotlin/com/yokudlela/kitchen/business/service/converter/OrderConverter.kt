package com.yokudlela.kitchen.business.service.converter

import com.yokudlela.kitchen.client.persistence.entity.Order
import org.modelmapper.ModelMapper
import org.springframework.beans.factory.annotation.Lookup
import org.springframework.core.convert.converter.Converter
import org.springframework.stereotype.Component

@Component
class OrderConverter(
    private val modelMapper: ModelMapper,
) : Converter<Order, com.yokudlela.kitchen.business.model.Order> {

    override fun convert(source: Order): com.yokudlela.kitchen.business.model.Order {
        val order = getOrder()
        modelMapper.map(source, order)
        return order
    }

    @Lookup
    fun getOrder(): com.yokudlela.kitchen.business.model.Order {
        throw IllegalCallerException("Do not call me!")
    }
}