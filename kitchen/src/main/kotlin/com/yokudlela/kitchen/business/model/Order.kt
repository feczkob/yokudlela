package com.yokudlela.kitchen.business.model

import com.yokudlela.kitchen.business.Constants
import com.yokudlela.kitchen.business.exception.BusinessException
import com.yokudlela.kitchen.business.exception.ErrorType
import com.yokudlela.kitchen.business.handler.LocationHandler
import com.yokudlela.kitchen.business.handler.OrderHandler
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
class Order @JvmOverloads constructor(
    @Transient
    private val orderHandler: OrderHandler,
    @Transient
    private val recipeHandler: RecipeHandler,
    @Transient
    private val locationHandler: LocationHandler,
    var id: Int = 0,
    // ! MutableList is needed so that modelMapper can map from entity to business
    var orderItems: MutableList<OrderItem> = mutableListOf(),
    var timeOfRecord: LocalDateTime? = null,
    var timeOfModification: LocalDateTime? = null,
    var timeOfFulfillment: LocalDateTime? = null,
    var status: Status? = null,
    var locationId: Int = 0,
    var location: Location? = null,
    var details: String? = null,
) : Serializable {

    override fun toString(): String = ToStringBuilder.reflectionToString(this)

    /**
     * Add order
     * @return Order added
     * ! invalidating Location cache is needed
     * ! TODO if the data can vary between consecutive requests, cache is needed at a lower level
     */
    @Caching(
        put = [CachePut(cacheNames = [Constants.CACHE_ORDER], key = "#result.id")],
        evict = [CacheEvict(
            cacheNames = [Constants.CACHE_ORDER + Constants.CACHE_ALL, Constants.CACHE_LOCATION,
                Constants.CACHE_LOCATION + Constants.CACHE_ALL], allEntries = true
        )]
    )
    fun addOrder(): Order {
        setAttributesCreateOrModify()
        timeOfRecord = LocalDateTime.now()
        location = locationHandler.loadLocation(locationId)
        return save()
    }

    /**
     * Load order
     * @param orderId Int id
     * @return Order loaded
     */
    @Cacheable(cacheNames = [Constants.CACHE_ORDER], key = "#orderId")
    fun loadOrder(orderId: Int): Order {
        return orderHandler.loadOrder(orderId)
    }

    /**
     * Change or add order
     * If the order is not present, it'll be added
     * @param orderId Int id
     * @return Order modified / newly created Order
     * ! invalidating Location cache is needed
     */
    @Caching(
        put = [CachePut(cacheNames = [Constants.CACHE_ORDER], key = "#result.id")],
        evict = [CacheEvict(
            cacheNames = [Constants.CACHE_ORDER + Constants.CACHE_ALL, Constants.CACHE_LOCATION,
                Constants.CACHE_LOCATION + Constants.CACHE_ALL], allEntries = true
        )]
    )
    fun changeOrAddOrder(orderId: Int): Order {
        val orderFound: Order
        try {
            orderFound = loadOrder(orderId)
        } catch (e: BusinessException) {
            return addOrder()
        }
        mergeOrder(orderFound)
        return save()
    }

    fun modify(existing: Order) {
        mergeOrder(existing)
        save()
    }

    /**
     * Delete order
     * @param orderId Int id
     * ! invalidating Location cache is needed
     */
    @Caching(
        evict = [CacheEvict(cacheNames = [Constants.CACHE_ORDER], key = "#orderId"),
            CacheEvict(
                cacheNames = [Constants.CACHE_ORDER + Constants.CACHE_ALL, Constants.CACHE_LOCATION,
                    Constants.CACHE_LOCATION + Constants.CACHE_ALL], allEntries = true
            )]
    )
    fun deleteOrder(orderId: Int) {
        orderHandler.deleteOrder(orderId)
    }

    /**
     * Modify order status
     * @param orderId Int id
     * @param status Status new Status
     * @return Order modified Order
     * ! invalidating Location cache is not needed, since
     * ! LocationResponse returns orderIds only
     */
    @Caching(
        put = [CachePut(cacheNames = [Constants.CACHE_ORDER], key = "#orderId")],
        evict = [CacheEvict(cacheNames = [Constants.CACHE_ORDER + Constants.CACHE_ALL], allEntries = true)]
    )
    fun modifyOrderStatus(orderId: Int, status: Status): Order {
        // TODO
        val order = loadOrder(orderId)
        return order.modifyStatus(status)
    }

    /**
     * Load orders
     * @param status Status? status (if present) to be filtered for
     * @param pageNumber Int number of the page
     * @param pageSize Int size of the page
     * @return List<Order> found
     */
    @Cacheable(cacheNames = [Constants.CACHE_ORDER + Constants.CACHE_ALL])
    fun loadOrders(status: Status?, pageNumber: Int, pageSize: Int): List<Order> {
        return if (status == null) {
            orderHandler.loadOrders(pageNumber, pageSize)
        } else {
            orderHandler.loadOrdersByStatus(status, pageNumber, pageSize)
        }
    }

    /**
     * Load status values
     * @return List<Status> values
     */
    @Cacheable(cacheNames = [Constants.CACHE_STATUS])
    fun loadStatusValues(): List<Status> {
        return Status.values().asList()
    }

    /**
     * Merge order
     * @param old Order the new
     * @return Order saved
     */
    private fun mergeOrder(old: Order) {
        // * open for modification: orderItems, details, location
        // TODO
        id = old.id
        timeOfRecord = old.timeOfRecord
        status = old.status
        timeOfModification = LocalDateTime.now()
        setAttributesCreateOrModify()
    }

    /**
     * Modify status
     * @param status Status newly assigned
     * @return Order saved
     */
    private fun modifyStatus(status: Status): Order {
        // * OPEN -> IN_PROGRESS
        if (this.status == Status.OPEN && status == Status.IN_PROGRESS) {
            this.status = Status.IN_PROGRESS
            modifyStatusToInProgress()
            return save()
        }

        // * IN_PROGRESS -> FINISHED
        if (this.status == Status.IN_PROGRESS && status == Status.FINISHED) {
            if (!checkIfAll(Status.FINISHED)) {
                throw BusinessException(ErrorType.NOT_FINISHED_ALL_ITEMS)
            }
            this.status = Status.FINISHED
            this.timeOfFulfillment = LocalDateTime.now()
            for (orderItem in orderItems) {
                if (orderItem.status != Status.FAILED) {
                    orderItem.status = Status.FINISHED
                    orderItem.timeOfFulfillment = LocalDateTime.now()
                } else {
                    this.status = Status.PARTIALLY_FINISHED
                }
            }
            return save()
        }
        throw BusinessException(ErrorType.INVALID_STATUS_CHANGE)
    }

    private fun setAttributesCreateOrModify() {
        status = Status.OPEN
        for (orderItem in orderItems) {
            orderItem.status = Status.OPEN
            orderItem.timeOfRecord = LocalDateTime.now()
            orderItem.order = this
            orderItem.orderId = null
        }
    }

    private fun modifyStatusToInProgress(): Order {
        for (orderItem in orderItems) {
            val recipe = recipeHandler.loadRecipeByMenuItemId(orderItem.menuItemId)
            orderItem.status = Status.IN_PROGRESS
            for (measure in recipe.ingredientMeasures) {
//                if (// TODO: location) {
                measure.ingredient.checkQuantityWh(measure.quantity, orderItem)
//                } else {
            }

            for (measure in recipe.ingredientMeasures) {
//                if (// TODO: location) {
                measure.ingredient.obtainFromWh()
//                } else {
            }
        }
        if (checkIfAll(Status.FAILED)) {
            this.status = Status.FAILED
        }
        return save()
    }

    private fun checkIfAll(statusToCompareWith: Status): Boolean {
        return (orderItems.stream().allMatch { o -> o.status == statusToCompareWith })
    }

    private fun save(): Order {
        return orderHandler.saveOrder(this)
    }
}