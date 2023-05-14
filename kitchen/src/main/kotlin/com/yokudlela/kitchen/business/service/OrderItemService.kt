package com.yokudlela.kitchen.business.service

import com.yokudlela.kitchen.application.AspectLogger
import com.yokudlela.kitchen.business.exception.BusinessException
import com.yokudlela.kitchen.business.exception.ErrorType
import com.yokudlela.kitchen.business.handler.OrderItemHandler
import com.yokudlela.kitchen.business.model.OrderItem
import com.yokudlela.kitchen.business.service.converter.OrderItemConverter
import com.yokudlela.kitchen.client.persistence.repository.OrderItemRepository
import org.modelmapper.ModelMapper
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.data.domain.PageRequest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@AspectLogger
class OrderItemService(
    private val orderItemRepository: OrderItemRepository,
    private val modelMapper: ModelMapper,
    private val orderItemConverter: OrderItemConverter,
) : OrderItemHandler {

    /**
     * Save
     * @param orderItem OrderItem to be saved
     * @return OrderItem saved
     */
    @Transactional
    override fun saveOrderItem(orderItem: OrderItem): OrderItem {
        val ord = modelMapper.map(orderItem, com.yokudlela.kitchen.client.persistence.entity.OrderItem::class.java)
        return orderItemConverter.convert(orderItemRepository.save(ord))
    }

    /**
     * Load
     * @param orderItemId Int id
     * @return OrderItem loaded
     */
    @Transactional
    override fun loadOrderItem(orderItemId: Int): OrderItem {
        val orderItem =
            orderItemRepository.findByIdOrNull(orderItemId) ?: throw BusinessException(ErrorType.MISSING_ENTITY)
        return orderItemConverter.convert(orderItem)
    }

    /**
     * Delete
     * @param orderItemId Int id
     */
    @Transactional(rollbackFor = [BusinessException::class])
    override fun deleteOrderItem(orderItemId: Int) {
        try {
            orderItemRepository.deleteById(orderItemId)
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
     * @return List<OrderItem> found
     */
    @Transactional
    override fun loadOrderItems(pageNumber: Int, pageSize: Int): List<OrderItem> {
        val pageable = PageRequest.of(pageNumber, pageSize)
        val page = orderItemRepository.findAll(pageable).content
        return page.map { orderItemConverter.convert(it) }
    }
}