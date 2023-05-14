package com.yokudlela.kitchen.business.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.yokudlela.kitchen.business.Constants
import com.yokudlela.kitchen.business.exception.BusinessException
import com.yokudlela.kitchen.business.exception.ErrorType
import com.yokudlela.kitchen.business.handler.OrderHandler
import com.yokudlela.kitchen.business.handler.OrderItemHandler
import com.yokudlela.kitchen.business.handler.RecipeHandler
import lombok.NoArgsConstructor
import org.apache.commons.lang3.builder.ToStringBuilder
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.CachePut
import org.springframework.cache.annotation.Cacheable
import org.springframework.cache.annotation.Caching
import java.io.Serializable
import java.time.LocalDateTime

@NoArgsConstructor
class OrderItem @JvmOverloads constructor(
    // ! Transient is needed so that the handlers won't be serialized
    @Transient
    private val orderItemHandler: OrderItemHandler,
    @Transient
    private val recipeHandler: RecipeHandler,
    @Transient
    private val orderHandler: OrderHandler,
    var id: Int? = null,
    var orderId: Int? = null,
    @JsonIgnore
    var order: Order? = null,
    var menuItemId: Int = 0,
    var quantity: Int = 0,
    var timeOfRecord: LocalDateTime? = null,
    var timeOfModification: LocalDateTime? = null,
    var timeOfFulfillment: LocalDateTime? = null,
    var status: Status? = null,
    var details: String? = null,
) : Serializable {

    override fun toString(): String = ToStringBuilder.reflectionToString(this)

    /**
     * Add orderItem
     * @return OrderItem added
     * ! invalidating Order cache is needed
     */
    @Caching(
        put = [CachePut(cacheNames = [Constants.CACHE_ORDER_ITEM], key = "#result.id")],
        evict = [CacheEvict(cacheNames = [Constants.CACHE_ORDER_ITEM + Constants.CACHE_ALL], key = "#result.orderId", allEntries = true),
            CacheEvict(cacheNames = [Constants.CACHE_ORDER, Constants.CACHE_ORDER + Constants.CACHE_ALL], allEntries = true)]
    )
    fun addOrderItem(): OrderItem {
        status = Status.OPEN
        timeOfRecord = LocalDateTime.now()
        val order = orderHandler.loadOrder(orderId!!)
//        order = orderId?.let { orderHandler.loadOrder(it) }
        this.order = order
        return save()
    }

    /**
     * Load orderItem
     * @param orderItemId Int id
     * @return OrderItem loaded
     */
    @Cacheable(cacheNames = [Constants.CACHE_ORDER_ITEM], key = "#orderItemId")
    fun loadOrderItem(orderItemId: Int): OrderItem {
        return orderItemHandler.loadOrderItem(orderItemId)
    }

    /**
     * Change orderItem
     * @param orderItemId Int id
     * @return OrderItem modified OrderItem
     * ! invalidating Order cache is needed
     */
    @Caching(
        put = [CachePut(cacheNames = [Constants.CACHE_ORDER_ITEM], key = "#result.id")],
        evict = [CacheEvict(cacheNames = [Constants.CACHE_ORDER_ITEM + Constants.CACHE_ALL], key = "#result.orderId", allEntries = true),
            CacheEvict(cacheNames = [Constants.CACHE_ORDER, Constants.CACHE_ORDER + Constants.CACHE_ALL], allEntries = true)]
    )
    fun changeOrAddOrderItem(orderItemId: Int): OrderItem {
        val orderItemFound : OrderItem = try {
            loadOrderItem(orderItemId)
        } catch (e: BusinessException) {
            return addOrderItem()
        }
        order = orderHandler.loadOrder(orderId!!)
        mergeOrderItem(orderItemFound)
        return save()
    }

    fun modify(existing: OrderItem) {
        mergeOrderItem(existing)
        save()
    }

    /**
     * Delete orderItem
     * @param orderItemId Int id
     * ! invalidating Order cache is needed
     */
    @Caching(
        evict = [CacheEvict(cacheNames = [Constants.CACHE_ORDER_ITEM], key = "#orderItemId"),
            CacheEvict(cacheNames = [Constants.CACHE_ORDER_ITEM + Constants.CACHE_ALL], allEntries = true),
            CacheEvict(cacheNames = [Constants.CACHE_ORDER, Constants.CACHE_ORDER + Constants.CACHE_ALL], allEntries = true)]
    )
    fun deleteOrderItem(orderItemId: Int) {
        // ? update order's timeOfModification?
//        val order = orderHandler.loadOrder(orderId)
        orderItemHandler.deleteOrderItem(orderItemId)
    }

    /**
     * Modify orderItem status
     * @param orderItemId Int id
     * @param status Status new Status
     * @return OrderItem modified OrderItem
     * ! invalidating Order cache is needed, since
     * ! OrderItemResponse returns status
     */
    @Caching(
        put = [CachePut(cacheNames = [Constants.CACHE_ORDER_ITEM], key = "#orderItemId")],
        evict = [CacheEvict(cacheNames = [Constants.CACHE_ORDER_ITEM + Constants.CACHE_ALL], key = "#result.orderId", allEntries = true),
            CacheEvict(cacheNames = [Constants.CACHE_ORDER, Constants.CACHE_ORDER + Constants.CACHE_ALL], allEntries = true)]
    )
    fun modifyOrderItemStatus(orderItemId: Int, status: Status): OrderItem {
        // TODO
        val orderItem = loadOrderItem(orderItemId)
        return orderItem.modifyStatus(status)
    }

    /**
     * Load orderItems
     * @param pageNumber Int number of the page
     * @param pageSize Int size of the page
     * @return List<OrderItem> found
     */
    @Cacheable(cacheNames = [Constants.CACHE_ORDER_ITEM + Constants.CACHE_ALL])
    fun loadOrderItems(pageNumber: Int, pageSize: Int): List<OrderItem> {
        return orderItemHandler.loadOrderItems(pageNumber, pageSize)
    }

    private fun mergeOrderItem(old: OrderItem) {
        // ? update order's timeOfModification?
        id = old.id
        status = old.status
        timeOfRecord = old.timeOfRecord
        timeOfModification = LocalDateTime.now()
    }

    private fun modifyStatus(status: Status): OrderItem {
        // * OPEN -> IN_PROGRESS
        if (this.status == Status.OPEN && status == Status.IN_PROGRESS) {
            this.status = Status.IN_PROGRESS
            modifyStatusToInProgress()
            return save()
        }

        // * IN_PROGRESS -> FINISHED
        if (this.status == Status.IN_PROGRESS && status == Status.FINISHED) {
            this.status = Status.FINISHED
            this.timeOfFulfillment = LocalDateTime.now()
            return save()
        }
        throw BusinessException(ErrorType.INVALID_STATUS_CHANGE)
    }

    private fun save(): OrderItem {
        return orderItemHandler.saveOrderItem(this)
    }

    private fun modifyStatusToInProgress(): OrderItem {
        val recipe = recipeHandler.loadRecipeByMenuItemId(menuItemId)
        for (measure in recipe.ingredientMeasures) {
            measure.ingredient.checkQuantityWh(measure.quantity, this)
        }

        for (measure in recipe.ingredientMeasures) {
            measure.ingredient.obtainFromWh()
        }
        return save()
    }
}