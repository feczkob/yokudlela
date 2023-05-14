package com.yokudlela.kitchen.controller.converter

import com.yokudlela.kitchen.business.model.OrderItem
import com.yokudlela.kitchen.controller.model.request.OrderItemRequest
import org.modelmapper.ModelMapper
import org.springframework.beans.factory.annotation.Lookup
import org.springframework.core.convert.converter.Converter
import org.springframework.stereotype.Component

@Component
class OrderItemToBusinessConverter(
    private val modelMapper: ModelMapper,
) : Converter<OrderItemRequest, OrderItem> {

    override fun convert(source: OrderItemRequest): OrderItem {
        val orderItem = getOrderItem()
        modelMapper.map(source, orderItem)
        return orderItem
    }

    @Lookup
    fun getOrderItem(): OrderItem {
        throw IllegalCallerException("Do not call me!")
    }
}