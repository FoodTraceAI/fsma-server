// ----------------------------------------------------------------------------
// Copyright 2024 FoodTraceAI LLC or its affiliates. All Rights Reserved.
// ----------------------------------------------------------------------------
package com.foodtraceai.model

import com.foodtraceai.util.TlcDateType
import jakarta.persistence.*
import java.time.LocalDate
import java.time.OffsetDateTime

@Entity
data class TraceLotCode(
    @Id @GeneratedValue
    override val id: Long = 0,
    val tlcVal: String,
    val gtin: String? = null,   // not required
    val batch: String? = null,  // not required
    val tlcDate: LocalDate? = null,    // not required
    @Enumerated(EnumType.STRING)
    val tlcDateType: TlcDateType? = null,   // not required
    // Serial Shipping Container Code
    val sscc: String? = null,   // not required

    @Column(updatable = false)
    override var dateCreated: OffsetDateTime = OffsetDateTime.now(),
    override var dateModified: OffsetDateTime = OffsetDateTime.now(),
    override var isDeleted: Boolean = false,
    override var dateDeleted: OffsetDateTime? = null
) : BaseModel<TraceLotCode>()

data class TraceLotCodeDto(
    val id: Long = 0,
    val tlcVal: String,
    val gtin: String?,
    val batch: String?,
    val tlcDate: LocalDate?,
    val tlcDateType: TlcDateType?,
    val sscc: String?,
    val dateCreated: OffsetDateTime = OffsetDateTime.now(),
    val dateModified: OffsetDateTime = OffsetDateTime.now(),
    val isDeleted: Boolean = false,
    val dateDeleted: OffsetDateTime? = null,
)

// TODO: TraceLotCode and TraceLotCodeDto are identical for now
// but I expect this to change in the future
fun TraceLotCode.toTraceLotCodeDto() = TraceLotCodeDto(
    id = id,
    tlcVal = tlcVal,
    gtin = gtin,
    batch = batch,
    tlcDate = tlcDate,
    tlcDateType = tlcDateType,
    sscc = sscc,
    dateCreated = dateCreated,
    dateModified = dateModified,
    isDeleted = isDeleted,
    dateDeleted = dateDeleted,
)

fun TraceLotCodeDto.toTraceLotCode() = TraceLotCode(
    id = id,
    tlcVal = tlcVal,
    batch = batch,
    gtin = gtin,
    tlcDate = tlcDate,
    tlcDateType = tlcDateType,
    sscc = sscc,
    dateCreated = dateCreated,
    dateModified = dateModified,
    isDeleted = isDeleted,
    dateDeleted = dateDeleted,
)
