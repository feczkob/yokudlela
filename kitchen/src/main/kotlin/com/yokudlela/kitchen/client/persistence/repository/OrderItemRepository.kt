package com.yokudlela.kitchen.client.persistence.repository

import com.yokudlela.kitchen.client.persistence.entity.OrderItem
import io.swagger.v3.oas.annotations.Hidden
import org.springframework.data.jpa.repository.JpaRepository

@Hidden
interface OrderItemRepository : JpaRepository<OrderItem, Int>