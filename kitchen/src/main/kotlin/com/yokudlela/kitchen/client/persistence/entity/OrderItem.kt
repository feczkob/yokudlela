package com.yokudlela.kitchen.client.persistence.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import lombok.EqualsAndHashCode
import java.time.LocalDateTime
import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne

@Entity
@EqualsAndHashCode
class OrderItem
//@JvmOverloads
constructor(
    @Id
    @Column(nullable = false, name = "id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    var id: Int? = null,
    // ! owning side
    @Column(nullable = false, name = "order_id", updatable = false, insertable = false)
    var orderId: Int? = null,
    @ManyToOne(cascade = [CascadeType.REFRESH])
    @JoinColumn(name = "order_id")
    @JsonIgnore
    // TODO JsonManagedReference
    var order: Order? = null,
    @Column(nullable = false, name = "menu_item_id")
    var menuItemId: Int = 0,
    @Column(nullable = false, name = "quantity")
    var quantity: Int = 0,
    @Column(nullable = false, name = "time_of_record", updatable = false)
    var timeOfRecord: LocalDateTime = LocalDateTime.now(),
    @Column(name = "time_of_modification")
    var timeOfModification: LocalDateTime? = null,
    @Column(name = "time_of_fulfillment")
    var timeOfFulfillment: LocalDateTime? = null,
    @Column(nullable = false, name = "status")
    var status: String = "",
    @Column(name = "details")
    var details: String? = null,
)