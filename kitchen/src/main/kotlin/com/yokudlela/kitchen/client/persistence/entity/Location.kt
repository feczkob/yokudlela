package com.yokudlela.kitchen.client.persistence.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import lombok.EqualsAndHashCode
import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.OneToMany

@Entity
@EqualsAndHashCode
class Location
//@JvmOverloads
constructor(
    // TODO order of params matters (Zoli)
    @Id
    @Column(nullable = false, name = "id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    var id: Int? = null,
    @Column(nullable = false, name = "name")
    var name: String = "",
    @Column(name = "details")
    var details: String? = null,
    @OneToMany(cascade = [CascadeType.ALL], fetch = FetchType.LAZY, mappedBy = "locationId", orphanRemoval = true)
    @JsonIgnore
    var orders: MutableList<Order>? = mutableListOf(),
)