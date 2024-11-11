// ----------------------------------------------------------------------------
// Copyright 2024 FoodTraceAI LLC or its affiliates. All Rights Reserved.
// ----------------------------------------------------------------------------
package com.foodtraceai.model

import com.foodtraceai.util.PointOfContact
import jakarta.persistence.*
import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction
import java.time.OffsetDateTime

@Entity
data class Location(
    @Id @GeneratedValue override val id: Long = 0,

    @ManyToOne @JoinColumn
    @OnDelete(action = OnDeleteAction.CASCADE)
    val foodBus: FoodBus,

    @Embedded val pointOfContact: PointOfContact,

    // Something Like: Company Distribution Center, Local Wholesale of Georgia
    // Includes: field name
    val description: String? = null,

    @ManyToOne @JoinColumn
    @OnDelete(action = OnDeleteAction.CASCADE)
    val address: Address,

    // Is this food business one of our clients that we need to bill?
    val isBillable: Boolean = true,

    @Column(updatable = false)
    override var dateCreated: OffsetDateTime = OffsetDateTime.now(),
    override var dateModified: OffsetDateTime = OffsetDateTime.now(),
    override var isDeleted: Boolean = false,
    override var dateDeleted: OffsetDateTime? = null
) : BaseModel<Location>()

data class LocationDto(
    val id: Long = 0,
    val foodBusId: Long,
    val pointOfContact: PointOfContact,
    val description: String?,
    val addressId: Long,
    val isBillable: Boolean,
    val dateCreated: OffsetDateTime = OffsetDateTime.now(),
    val dateModified: OffsetDateTime = OffsetDateTime.now(),
    val isDeleted: Boolean = false,
    val dateDeleted: OffsetDateTime? = null,
)

fun Location.toLocationDto() = LocationDto(
    id = id,
    foodBusId = foodBus.id,
    pointOfContact = pointOfContact,
    description = description,
    addressId = address.id,
    isBillable = isBillable,
    dateCreated = dateCreated,
    dateModified = dateModified,
    isDeleted = isDeleted,
    dateDeleted = dateDeleted,
)

fun LocationDto.toLocation(
    foodBus: FoodBus,
    address: Address,
) = Location(
    id = id,
    foodBus = foodBus,
    pointOfContact = pointOfContact,
    description = description,
    address = address,
    isBillable = isBillable,
)
