// ----------------------------------------------------------------------------
// Copyright 2024 FoodTraceAI LLC or its affiliates. All Rights Reserved.
// ----------------------------------------------------------------------------
package com.foodtraceai.model

import com.foodtraceai.util.Country
import com.foodtraceai.util.UsaCanadaState
import jakarta.persistence.*
import java.time.OffsetDateTime

@Entity
//@EntityListeners(BaseModelEntityListener::class)
data class Address(
    @Id @GeneratedValue
    override val id: Long = 0,
    val resellerId: Long = 0,
    val street: String,
    val street2: String? = null,
    val city: String,
    @Enumerated(EnumType.STRING)
    val state: UsaCanadaState,
    val postalCode: String,
    @Enumerated(EnumType.STRING)
    val country: Country, // Country = Country.USA,
    val lat: Double? = null,
    val lng: Double? = null,
    val gln: String? = null, // AI(254) Global Location Numbere

    @Column(updatable = false)
    override var dateCreated: OffsetDateTime = OffsetDateTime.now(),
    override var dateModified: OffsetDateTime = OffsetDateTime.now(),
    override var isDeleted: Boolean = false,
    override var dateDeleted: OffsetDateTime? = null

) : BaseModel<Address>()

data class AddressDto(
    val id: Long = 0,
    val resellerId: Long? = null,
    val street: String,
    val street2: String? = null,
    val city: String,
    val state: UsaCanadaState,
    val postalCode: String,
    val country: Country,
    val lat: Double? = null,
    val lng: Double? = null,
    val gln: String? = null,
    val dateCreated: OffsetDateTime = OffsetDateTime.now(),
    val dateModified: OffsetDateTime = OffsetDateTime.now(),
    val isDeleted: Boolean = false,
    val dateDeleted: OffsetDateTime? = null,
)

fun Address.toAddressDto() = AddressDto(
    id = id,
    resellerId = resellerId,
    street = street,
    street2 = street2,
    city = city,
    state = state,
    postalCode = postalCode,
    country = country,
    lat = lat,
    lng = lng,
    gln = gln,
    dateCreated = dateCreated,
    dateModified = dateModified,
    isDeleted = isDeleted,
    dateDeleted = dateDeleted,
)

fun AddressDto.toAddress(resellerId: Long) = Address(
    id = id,
    resellerId = resellerId,
    street = street,
    street2 = street2,
    city = city,
    state = state,
    postalCode = postalCode,
    country = country,
    lat = lat,
    lng = lng,
    gln = gln,
)
