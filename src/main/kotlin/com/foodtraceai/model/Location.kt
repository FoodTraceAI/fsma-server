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
    @Id @GeneratedValue override val id: Long = 0,

    @ManyToOne @JoinColumn
    @OnDelete(action = OnDeleteAction.CASCADE)
    val foodBus: FoodBus,

    val contactName: String? = null,
    val contactPhone: String? = null,
    val contactEmail: String? = null,

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
    val contactName: String?,
    val contactPhone: String?,
    val contactEmail: String?,
    val addressId: Long,
    val isBillable: Boolean,
    val dateCreated: OffsetDateTime= OffsetDateTime.now(),
    val dateModified: OffsetDateTime= OffsetDateTime.now(),
    val isDeleted: Boolean = false,
    val dateDeleted: OffsetDateTime?=null,
)

fun Location.toLocationDto() = LocationDto(
    id = id,
    foodBusId = foodBus.id,
    contactName = contactName,
    contactPhone = contactPhone,
    contactEmail = contactEmail,
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
    contactName = contactName,
    contactPhone = contactPhone,
    contactEmail = contactEmail,
    address = address,
    isBillable = isBillable,
)