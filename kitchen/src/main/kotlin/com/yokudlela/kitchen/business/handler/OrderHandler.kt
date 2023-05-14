package com.yokudlela.kitchen.business.handler

import com.yokudlela.kitchen.business.model.Order
import com.yokudlela.kitchen.business.model.Status

interface OrderHandler {

    fun saveOrder(order: Order): Order
    fun loadOrder(orderId: Int): Order
    fun deleteOrder(orderId: Int)
    fun loadOrders(pageNumber: Int, pageSize: Int): List<Order>
    fun loadOrdersByStatus(status: Status, pageNumber: Int, pageSize: Int): List<Order>
}