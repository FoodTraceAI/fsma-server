// ----------------------------------------------------------------------------
// Copyright 2025 FoodTraceAI LLC or its affiliates. All Rights Reserved.
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
    override var dateDeleted: OffsetDateTime? = null,
    override var authUsername: String? = null,
) : BaseFoodBusModel<Location>()

data class LocationRequestDto(
    val foodBusId: Long,
    val locationContactId: Long,
    val description: String?,
    val addressId: Long,
    val isClient: Boolean,
)

data class LocationResponseDto(
    override var id: Long,
    val foodBusId: Long,
    val locationContactId: Long,
    val description: String?,
    val addressId: Long,
    val isClient: Boolean,
    override var dateCreated: OffsetDateTime = OffsetDateTime.now(),
    override var dateModified: OffsetDateTime = OffsetDateTime.now(),
    override var isDeleted: Boolean = false,
    override var dateDeleted: OffsetDateTime? = null,
    override var authUsername: String? = null,
) : BaseResponse<LocationResponseDto>()

fun Location.toLocationResponseDto() = LocationResponseDto(
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
    authUsername = authUsername,
)

fun LocationRequestDto.toLocation(
    id: Long,
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
