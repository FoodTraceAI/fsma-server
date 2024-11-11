// ----------------------------------------------------------------------------
// Copyright 2024 FoodTraceAI LLC or its affiliates. All Rights Reserved.
// ----------------------------------------------------------------------------
package com.foodtraceai.model

import com.foodtraceai.util.FoodBusType
import com.foodtraceai.util.PointOfContact
import jakarta.persistence.*
import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction
import java.time.OffsetDateTime

@Entity
data class FoodBus(
    @Id @GeneratedValue
    override val id: Long = 0,

    @ManyToOne @JoinColumn
    @OnDelete(action = OnDeleteAction.CASCADE)
    override val reseller: Reseller,

    @Embedded val pointOfContact: PointOfContact,

    @ManyToOne @JoinColumn
    @OnDelete(action = OnDeleteAction.CASCADE)
    val mainAddress: Address,
    val foodBusName: String,

    @Enumerated(EnumType.STRING)
    val foodBusType: FoodBusType,

    // Is this a franchisee?  If so, it has a franchisor
    @ManyToOne @JoinColumn
    @OnDelete(action = OnDeleteAction.CASCADE)
    val franchisor: Franchisor? = null,

    // Is this account enabled in the system?
    val isEnabled: Boolean = true,

    @Column(updatable = false)
    override var dateCreated: OffsetDateTime = OffsetDateTime.now(),
    override var dateModified: OffsetDateTime = OffsetDateTime.now(),
    override var isDeleted: Boolean = false,
    override var dateDeleted: OffsetDateTime? = null,
) : BaseResellerModel<FoodBus>() {
    val isFranchisee: Boolean
        get() = franchisor != null
}

data class FoodBusDto(
    val id: Long = 0,
    val resellerId: Long,
    val pointOfContact: PointOfContact,
    val mainAddressId: Long,
    val foodBusName: String,
    val foodBusType: FoodBusType,
    val franchisorId: Long? = null,
    val isEnabled: Boolean = true,
    val isClient: Boolean = true,
    val dateCreated: OffsetDateTime = OffsetDateTime.now(),
    val dateModified: OffsetDateTime = OffsetDateTime.now(),
    val isDeleted: Boolean = false,
    val dateDeleted: OffsetDateTime? = null,
)

fun FoodBus.toFoodBusDto() = FoodBusDto(
    id = id,
    resellerId = reseller.id,
    pointOfContact = pointOfContact,
    mainAddressId = mainAddress.id,
    foodBusName = foodBusName,
    foodBusType = foodBusType,
    franchisorId = franchisor?.id,
    isEnabled = isEnabled,
    dateCreated = dateCreated,
    dateModified = dateModified,
    isDeleted = isDeleted,
    dateDeleted = dateDeleted,
)

fun FoodBusDto.toFoodBus(
    reseller: Reseller,
    mainAddress: Address,
    franchisor: Franchisor? = null,
) = FoodBus(
    id = id,
    reseller = reseller,
   pointOfContact=pointOfContact,
    mainAddress = mainAddress,
    foodBusName = foodBusName,
    foodBusType = foodBusType,
    franchisor = franchisor,
    isEnabled = isEnabled,
)
