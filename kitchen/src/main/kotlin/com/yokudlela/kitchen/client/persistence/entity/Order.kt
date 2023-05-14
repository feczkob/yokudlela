package com.yokudlela.kitchen.client.persistence.entity

import lombok.EqualsAndHashCode
import java.time.LocalDateTime
import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.OneToMany
import javax.persistence.Table

@Entity
@EqualsAndHashCode
@Table(name = "order_")
class Order
//@JvmOverloads
constructor(
    @Id
    @Column(nullable = false, name = "id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    var id: Int? = null,
    @OneToMany(cascade = [CascadeType.ALL], fetch = FetchType.LAZY, mappedBy = "orderId", orphanRemoval = true)
    var orderItems: MutableList<OrderItem> = mutableListOf(),
    @Column(nullable = false, name = "time_of_record", updatable = false)
    var timeOfRecord: LocalDateTime = LocalDateTime.now(),
    @Column(name = "time_of_modification")
    var timeOfModification: LocalDateTime? = null,
    @Column(name = "time_of_fulfillment")
    var timeOfFulfillment: LocalDateTime? = null,
    @Column(nullable = false, name = "status")
    var status: String = "",
    @Column(nullable = false, name = "location_id")
    var locationId: Int = 0,
    @ManyToOne
    @JoinColumn(name = "location_id", updatable = false, insertable = false)
    var location: Location? = null,
    @Column(name = "details")
    var details: String? = null,
)
