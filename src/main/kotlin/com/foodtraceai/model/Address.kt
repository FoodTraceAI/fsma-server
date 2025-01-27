// ----------------------------------------------------------------------------
// Copyright 2025 FoodTraceAI LLC or its affiliates. All Rights Reserved.
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
    val street: String,
    val street2: String? = null,
    val city: String,
    @Enumerated(EnumType.STRING)
    val state: UsaCanadaState,
    val postalCode: String,
    @Enumerated(EnumType.STRING)
    val country: Country = Country.USA,
    val lat: Double? = null,
    val lon: Double? = null,
    val gln: String? = null, // AI(254) Global Location Number
    val ffrn: String? = null,
    @Column(updatable = false)
    override var dateCreated: OffsetDateTime = OffsetDateTime.now(),
    override var dateModified: OffsetDateTime = OffsetDateTime.now(),
    override var isDeleted: Boolean = false,
    override var dateDeleted: OffsetDateTime? = null,
    override var authUsername: String? = null,
) : BaseModel<Address>()

data class AddressRequestDto(
    val street: String,
    val street2: String? = null,
    val city: String,
    val state: UsaCanadaState,
    val postalCode: String,
    val country: Country,
    val lat: Double? = null,
    val lon: Double? = null,
    val gln: String? = null,
    val ffrn: String? = null,
)

data class AddressResponseDto(
    override val id: Long,
    val street: String,
    val street2: String? = null,
    val city: String,
    val state: UsaCanadaState,
    val postalCode: String,
    val country: Country,
    val lat: Double? = null,
    val lon: Double? = null,
    val gln: String? = null,
    val ffrn: String? = null,
    override var dateCreated: OffsetDateTime = OffsetDateTime.now(),
    override var dateModified: OffsetDateTime = OffsetDateTime.now(),
    override var isDeleted: Boolean = false,
    override var dateDeleted: OffsetDateTime? = null,
    override var authUsername: String? = null,
) : BaseResponse<AddressResponseDto>()

fun Address.toAddressResponseDto() = AddressResponseDto(
    id = id,
    street = street,
    street2 = street2,
    city = city,
    state = state,
    postalCode = postalCode,
    country = country,
    lat = lat,
    lon = lon,
    gln = gln,
    ffrn = ffrn,
    dateCreated = dateCreated,
    dateModified = dateModified,
    isDeleted = isDeleted,
    dateDeleted = dateDeleted,
    authUsername = authUsername,
)

fun AddressRequestDto.toAddress(id: Long) = Address(
    id = id,
    street = street,
    street2 = street2,
    city = city,
    state = state,
    postalCode = postalCode,
    country = country,
    lat = lat,
    lon = lon,
    gln = gln,
    ffrn = ffrn,
)

fun AddressResponseDto.toAddress() = Address(
    id = id,
    street = street,
    street2 = street2,
    city = city,
    state = state,
    postalCode = postalCode,
    country = country,
    lat = lat,
    lon = lon,
    gln = gln,
    ffrn = ffrn,
    dateCreated = dateCreated,
    dateModified = dateModified,
    isDeleted = isDeleted,
    dateDeleted = dateDeleted,
    authUsername = authUsername,
)

fun Address.format(
    showStreet: Boolean = true,
    showLatLon: Boolean = false,
): String {
    var address = ""
    if (showStreet) {
        address += street
        if (street2 != null)
            address += ", $street2"
        address += ", $city, $state $postalCode"
        if (country != Country.USA)
            address += ", ${country.name}"
    }

    if (showLatLon) {
        if (lat != null && lon != null)
            address += ", lat: $lat & lng: $lon"
    }

    return address
}
