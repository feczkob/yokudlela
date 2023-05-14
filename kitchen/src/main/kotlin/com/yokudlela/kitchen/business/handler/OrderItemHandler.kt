package com.yokudlela.kitchen.business.handler

import com.yokudlela.kitchen.business.model.OrderItem

interface OrderItemHandler {

    fun saveOrderItem(orderItem: OrderItem): OrderItem
    fun loadOrderItem(orderItemId: Int): OrderItem
    fun deleteOrderItem(orderItemId: Int)
    fun loadOrderItems(pageNumber: Int, pageSize: Int): List<OrderItem>
}