package com.yokudlela.kitchen.controller.converter

import com.yokudlela.kitchen.business.model.Order
import com.yokudlela.kitchen.controller.model.request.OrderRequest
import org.modelmapper.ModelMapper
import org.springframework.beans.factory.annotation.Lookup
import org.springframework.core.convert.converter.Converter
import org.springframework.stereotype.Component

@Component
class OrderToBusinessConverter(
    private val modelMapper: ModelMapper,
) : Converter<OrderRequest, Order> {

    override fun convert(source: OrderRequest): Order {
        val order = getOrder()
        modelMapper.map(source, order)
        return order
    }

    @Lookup
    fun getOrder(): Order {
        throw IllegalCallerException("Do not call me!")
    }
}