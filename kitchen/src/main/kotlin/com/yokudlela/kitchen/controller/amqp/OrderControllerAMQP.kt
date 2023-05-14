package com.yokudlela.kitchen.controller.amqp

import com.yokudlela.kitchen.business.config.OrderConfig
import com.yokudlela.kitchen.controller.converter.OrderToBusinessConverter
import com.yokudlela.kitchen.controller.model.request.OrderRequest
import com.yokudlela.kitchen.controller.model.response.OrderResponse
import org.modelmapper.ModelMapper
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Controller

@Controller
class OrderControllerAMQP(
    private val orderConfig: OrderConfig,
    private val orderToBusinessConverter: OrderToBusinessConverter,
    private val modelMapper: ModelMapper,
) {

    @RabbitListener(queues = ["\${amqp.order.register}"])
//    @AspectLogger
    fun registerOrder(order: OrderRequest) {
        // * waiter

        val orderAdded = orderToBusinessConverter.convert(order).addOrder()
        // TODO: send message to queue
    }

    @RabbitListener(queues = ["\${amqp.order.modify}"])
//    @AspectLogger
    fun modifyOrder(orderId: Int, order: OrderRequest) {
        // * waiter

        val business = orderConfig.getOrder()
        val changedOrder = modelMapper.map(
            business.changeOrAddOrder(orderId),
            OrderResponse::class.java
        )
        // TODO: send message to queue
    }
}