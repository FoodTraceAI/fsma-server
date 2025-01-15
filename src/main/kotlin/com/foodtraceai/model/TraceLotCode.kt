// ----------------------------------------------------------------------------
// Copyright 2025 FoodTraceAI LLC or its affiliates. All Rights Reserved.
// ----------------------------------------------------------------------------
package com.foodtraceai.model

import jakarta.persistence.*
import java.time.LocalDate
import java.time.OffsetDateTime

@Entity
data class TraceLotCode(
    @Id @GeneratedValue
    override val id: Long = 0,
    val tlcVal: String,

    // PTI Recommended
    val gtin: String? = null,   // AI(01) Case GTIN - not required
    val batchLot: String? = null,  // AI(10) Case Batch/Lot - not required

    // Optional GS1 parameters
    val sscc: String? = null,   // AI(00) - Pallet Serial Shipping Container Code
    val packDate: LocalDate? = null,    // AI(13)
    val harvestDate: LocalDate? = null, // AI(13)
    val bestByDate: LocalDate? = null,  // AI(15)
    val logSerialNo: String? = null, // AI(21) - Logistics Serial Number

    // Extra parameters that seem to belong to the TLC
    // The location description for the traceability lot code source,
    // or the traceability lot code source reference; and
    @ManyToOne  //(cascade = [CascadeType.ALL])
    @JoinColumn
    val tlcSource: Location,
    val tlcSourceReference: String? = null,

    @Column(updatable = false)
    override var dateCreated: OffsetDateTime = OffsetDateTime.now(),
    override var dateModified: OffsetDateTime = OffsetDateTime.now(),
    override var isDeleted: Boolean = false,
    override var dateDeleted: OffsetDateTime? = null
) : BaseModel<TraceLotCode>()

data class TraceLotCodeDto(
    val id: Long = 0,
    val tlcVal: String,

    // PTI Recommended
    val gtin: String?,
    val batchLot: String?,

    // Optional
    val sscc: String? = null,   // AI(00) - Pallet Serial Shipping Container Code
    val packDate: LocalDate? = null,    // AI(13)
    val harvestDate: LocalDate? = null, // AI(13)
    val bestByDate: LocalDate? = null,  // AI(15)
    val logSerialNo: String? = null, // AI(21) - Logistics Serial Number

    // Extra parameters that seem to belong
    val tlcSourceId: Long,
    val tlcSourceReference: String? = null,

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
    batchLot = batchLot,
    sscc = sscc,
    packDate = packDate,
    harvestDate = harvestDate,
    bestByDate = bestByDate,
    logSerialNo = logSerialNo,
    tlcSourceId = tlcSource.id,
    tlcSourceReference = tlcSourceReference,
    dateCreated = dateCreated,
    dateModified = dateModified,
    isDeleted = isDeleted,
    dateDeleted = dateDeleted,
)

fun TraceLotCodeDto.toTraceLotCode(
    tlcSource: Location,
) = TraceLotCode(
    id = id,
    tlcVal = tlcVal,
    gtin = gtin,
    batchLot = batchLot,
    sscc = sscc,
    packDate = packDate,
    harvestDate = harvestDate,
    bestByDate = bestByDate,
    logSerialNo = logSerialNo,
    tlcSource = tlcSource,
    tlcSourceReference = tlcSourceReference,
    dateCreated = dateCreated,
    dateModified = dateModified,
    isDeleted = isDeleted,
    dateDeleted = dateDeleted,
)
