package com.yokudlela.kitchen.business.service

import com.yokudlela.kitchen.application.AspectLogger
import com.yokudlela.kitchen.business.exception.BusinessException
import com.yokudlela.kitchen.business.exception.ErrorType
import com.yokudlela.kitchen.business.handler.OrderHandler
import com.yokudlela.kitchen.business.model.Order
import com.yokudlela.kitchen.business.model.Status
import com.yokudlela.kitchen.business.service.converter.OrderConverter
import com.yokudlela.kitchen.client.persistence.repository.OrderRepository
import org.modelmapper.ModelMapper
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.data.domain.PageRequest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@AspectLogger
class OrderService(
    private val orderRepository: OrderRepository,
    private val modelMapper: ModelMapper,
    private val orderConverter: OrderConverter,
) : OrderHandler {

    /**
     * Save
     * @param order Order to be saved
     * @return Order saved
     */
    @Transactional
    override fun saveOrder(order: Order): Order {
        val orderEntity = modelMapper.map(order, com.yokudlela.kitchen.client.persistence.entity.Order::class.java)
        val orderSaved = orderRepository.save(orderEntity)
        return orderConverter.convert(orderSaved)
    }

    /**
     * Load
     * @param orderId Int id
     * @return Order loaded
     */
    @Transactional
    override fun loadOrder(orderId: Int): Order {
        val order = orderRepository.findByIdOrNull(orderId) ?: throw BusinessException(ErrorType.MISSING_ENTITY)
        return orderConverter.convert(order)
    }

    /**
     * Delete
     * @param orderId Int id
     */
    @Transactional(rollbackFor = [BusinessException::class])
    override fun deleteOrder(orderId: Int) {
        try {
            orderRepository.deleteById(orderId)
        } catch (e: EmptyResultDataAccessException) {
            throw BusinessException(ErrorType.MISSING_ENTITY)
        } catch (e: Exception) {
            throw BusinessException(ErrorType.UNKNOWN_ERROR)
        }
    }

    /**
     * Load all
     * @param pageNumber Int number of the page
     * @param pageSize Int size of the page
     * @return List<Order> found
     */
    @Transactional
    override fun loadOrders(pageNumber: Int, pageSize: Int): List<Order> {
        val pageable = PageRequest.of(pageNumber, pageSize)
        val page = orderRepository.findAll(pageable).content
        return page.map { orderConverter.convert(it) }
    }

    /**
     * Load all by status
     * @param status Status to be filtered for
     * @param pageNumber Int number of the page
     * @param pageSize Int size of the page
     * @return List<Order> found
     */
    @Transactional
    override fun loadOrdersByStatus(status: Status, pageNumber: Int, pageSize: Int): List<Order> {
        val pageable = PageRequest.of(pageNumber, pageSize)
        val page = orderRepository.findAllByStatus(status.toString(), pageable)
        return page.map { orderConverter.convert(it) }
    }
}