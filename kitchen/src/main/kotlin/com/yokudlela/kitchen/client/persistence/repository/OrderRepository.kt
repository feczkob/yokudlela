package com.yokudlela.kitchen.client.persistence.repository

import com.yokudlela.kitchen.client.persistence.entity.Order
import io.swagger.v3.oas.annotations.Hidden
import org.springframework.data.domain.PageRequest
import org.springframework.data.jpa.repository.JpaRepository

@Hidden
interface OrderRepository : JpaRepository<Order, Int> {

    fun findAllByStatus(status: String, pageRequest: PageRequest): List<Order>
}