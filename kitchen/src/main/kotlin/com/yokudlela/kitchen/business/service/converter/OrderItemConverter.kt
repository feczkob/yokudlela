package com.yokudlela.kitchen.business.service.converter

import com.yokudlela.kitchen.client.persistence.entity.OrderItem
import org.modelmapper.ModelMapper
import org.springframework.beans.factory.annotation.Lookup
import org.springframework.core.convert.converter.Converter
import org.springframework.stereotype.Component

@Component
class OrderItemConverter(
    private val modelMapper: ModelMapper,
) : Converter<OrderItem, com.yokudlela.kitchen.business.model.OrderItem> {

    override fun convert(source: OrderItem): com.yokudlela.kitchen.business.model.OrderItem {
        val orderItem = getOrderItem()
        modelMapper.map(source, orderItem)
        return orderItem
    }

    @Lookup
    fun getOrderItem(): com.yokudlela.kitchen.business.model.OrderItem {
        throw IllegalCallerException("Do not call me!")
    }
}