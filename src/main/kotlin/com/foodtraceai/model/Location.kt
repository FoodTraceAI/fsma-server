// ----------------------------------------------------------------------------
// Copyright 2024 FoodTraceAI LLC or its affiliates. All Rights Reserved.
// ----------------------------------------------------------------------------
package com.foodtraceai.model

import jakarta.persistence.*
import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction
import java.time.OffsetDateTime

@Entity
data class Location(
    @Id @GeneratedValue
    override val id: Long = 0,

    @ManyToOne @JoinColumn
    @OnDelete(action = OnDeleteAction.CASCADE)
    override val foodBus: FoodBus,

    @ManyToOne @JoinColumn
    @OnDelete(action = OnDeleteAction.CASCADE)
    val locationContact: Contact,

    // Something Like: Company Distribution Center, Local Wholesale of Georgia
    // Includes: field name
    val description: String? = null,

    @ManyToOne @JoinColumn
    @OnDelete(action = OnDeleteAction.CASCADE)
    val address: Address,

    // Is this food business one of our clients that we need to bill?
    val isClient: Boolean = true,

    @Column(updatable = false)
    override var dateCreated: OffsetDateTime = OffsetDateTime.now(),
    override var dateModified: OffsetDateTime = OffsetDateTime.now(),
    override var isDeleted: Boolean = false,
    override var dateDeleted: OffsetDateTime? = null
) : BaseFoodBusModel<Location>()

data class LocationDto(
    val id: Long = 0,
    val foodBusId: Long,
    val locationContactId: Long,
    val description: String?,
    val addressId: Long,
    val isClient: Boolean,
    val dateCreated: OffsetDateTime = OffsetDateTime.now(),
    val dateModified: OffsetDateTime = OffsetDateTime.now(),
    val isDeleted: Boolean = false,
    val dateDeleted: OffsetDateTime? = null,
)

fun Location.toLocationDto() = LocationDto(
    id = id,
    foodBusId = foodBus.id,
    locationContactId = locationContact.id,
    description = description,
    addressId = address.id,
    isClient = isClient,
    dateCreated = dateCreated,
    dateModified = dateModified,
    isDeleted = isDeleted,
    dateDeleted = dateDeleted,
)

fun LocationDto.toLocation(
    foodBus: FoodBus,
    locationContact: Contact,
    address: Address,
) = Location(
    id = id,
    foodBus = foodBus,
    locationContact = locationContact,
    description = description,
    address = address,
    isClient = isClient,
)
