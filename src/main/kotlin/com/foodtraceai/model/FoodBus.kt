// ----------------------------------------------------------------------------
// Copyright 2024 FoodTraceAI LLC or its affiliates. All Rights Reserved.
// ----------------------------------------------------------------------------
package com.foodtraceai.model

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
    override val reseller: Reseller?,   // null reseller means that this entity is a client

    @ManyToOne @JoinColumn
    @OnDelete(action = OnDeleteAction.CASCADE)
    val foodBusContact: Contact,

    @ManyToOne @JoinColumn
    @OnDelete(action = OnDeleteAction.CASCADE)
    val mainAddress: Address,
    val foodBusName: String,
    val foodBusDesc: String,

    // If franchisor is nonnull this is a franchisee
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
    val resellerId: Long?,
    val foodBusContactId: Long,
    val mainAddressId: Long,
    val foodBusName: String,
    val foodBusDesc: String,
    val franchisorId: Long?,    // nonnull & nonzero means this is a franchisee
    val isEnabled: Boolean = true,
    val dateCreated: OffsetDateTime = OffsetDateTime.now(),
    val dateModified: OffsetDateTime = OffsetDateTime.now(),
    val isDeleted: Boolean = false,
    val dateDeleted: OffsetDateTime? = null,
)

fun FoodBus.toFoodBusDto() = FoodBusDto(
    id = id,
    resellerId = reseller?.id,
    foodBusContactId = foodBusContact.id,
    mainAddressId = mainAddress.id,
    foodBusName = foodBusName,
    foodBusDesc = foodBusDesc,
    franchisorId = franchisor?.id,
    isEnabled = isEnabled,
    dateCreated = dateCreated,
    dateModified = dateModified,
    isDeleted = isDeleted,
    dateDeleted = dateDeleted,
)

fun FoodBusDto.toFoodBus(
    reseller: Reseller?,
    mainAddress: Address,
    foodBusContact: Contact,
    franchisor: Franchisor?
) = FoodBus(
    id = id,
    reseller = reseller,
    foodBusContact = foodBusContact,
    mainAddress = mainAddress,
    foodBusName = foodBusName,
    foodBusDesc = foodBusDesc,
    franchisor = franchisor,
    isEnabled = isEnabled,
)
