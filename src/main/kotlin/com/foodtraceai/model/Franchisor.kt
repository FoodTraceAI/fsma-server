// ----------------------------------------------------------------------------
// Copyright 2024 FoodTraceAI LLC or its affiliates. All Rights Reserved.
// ----------------------------------------------------------------------------
package com.foodtraceai.model

import jakarta.persistence.*
import java.time.OffsetDateTime

@Entity
@Table(
    indexes = [
        Index(columnList = "subdomain")
    ]
)
data class Franchisor(
    @Id @GeneratedValue
    override val id: Long = 0,

    @ManyToOne(cascade = [CascadeType.ALL])
    @JoinColumn
    val address: Address,
    val franchisorName: String,

    val mainContactName: String, // "Operational Contact Name"
    val mainContactPhone: String, // "Phone Number"
    val mainContactEmail: String, // "Email Address"

    val billingContactName: String? = null,
    val billingContactPhone: String? = null,
    val billingContactEmail: String? = null,

    @ManyToOne(cascade = [CascadeType.ALL])
    @JoinColumn
    val billingAddress: Address? = null,

    val subdomain: String? = null,
    val accessKey: Long? = null,
    val isEnabled: Boolean = true,

    @Column(updatable = false)
    override var dateCreated: OffsetDateTime = OffsetDateTime.now(),
    override var dateModified: OffsetDateTime = OffsetDateTime.now(),
    override var isDeleted: Boolean = false,
    override var dateDeleted: OffsetDateTime? = null
) : BaseModel<Franchisor>()

data class FranchisorDto(
    val id: Long = 0,
    val addressId: Long,
    val franchisorName: String,
    val mainContactName: String,
    val mainContactPhone: String,
    val mainContactEmail: String,
    val billingContactName: String?,
    val billingContactPhone: String?,
    val billingContactEmail: String?,
    val billingAddressId: Long?,
    val subdomain: String?,
    val accessKey: Long? = null,
    val isEnabled: Boolean,
    val dateCreated: OffsetDateTime = OffsetDateTime.now(),
    val dateModified: OffsetDateTime = OffsetDateTime.now(),
    val isDeleted: Boolean = false,
    val dateDeleted: OffsetDateTime? = null,
)

fun Franchisor.toFranchisorDto() = FranchisorDto(
    id = id,
    addressId = address.id,
    franchisorName = franchisorName,
    mainContactName = mainContactName,
    mainContactPhone = mainContactPhone,
    mainContactEmail = mainContactEmail,
    billingContactName = billingContactName,
    billingContactPhone = billingContactPhone,
    billingContactEmail = billingContactEmail,
    billingAddressId = billingAddress?.id,
    subdomain = subdomain,
    accessKey = accessKey,
    isEnabled = isEnabled,
    dateCreated = dateCreated,
    dateModified = dateModified,
    isDeleted = isDeleted,
    dateDeleted = dateDeleted,
)

fun FranchisorDto.toFranchisor(
    address: Address,
    billingAddress: Address?,
) = Franchisor(
    id = id,
    address = address,
    franchisorName = franchisorName,
    mainContactName = mainContactName,
    mainContactPhone = mainContactPhone,
    mainContactEmail = mainContactEmail,
    billingContactName = billingContactName,
    billingContactPhone = billingContactPhone,
    billingContactEmail = billingContactEmail,
    billingAddress = billingAddress,
    subdomain = subdomain,
    accessKey = accessKey,
    isEnabled = isEnabled,
)
