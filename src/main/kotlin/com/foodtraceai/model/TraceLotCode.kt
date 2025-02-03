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
    val tlcSourceLoc: Location,
    val tlcSourceReference: String? = null,

    @Column(updatable = false)
    override var dateCreated: OffsetDateTime = OffsetDateTime.now(),
    override var dateModified: OffsetDateTime = OffsetDateTime.now(),
    override var isDeleted: Boolean = false,
    override var dateDeleted: OffsetDateTime? = null,
    override var authUsername: String? = null,
) : BaseModel<TraceLotCode>()

data class TraceLotCodeRequestDto(
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
    val tlcSourceLocId: Long,
    val tlcSourceReference: String? = null,
)

data class TraceLotCodeResponseDto(
    override val id: Long,
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
    val tlcSourceLocId: Long,
    val tlcSourceReference: String? = null,

    override var dateCreated: OffsetDateTime = OffsetDateTime.now(),
    override var dateModified: OffsetDateTime = OffsetDateTime.now(),
    override var isDeleted: Boolean = false,
    override var dateDeleted: OffsetDateTime? = null,
    override var authUsername: String? = null,
) : BaseResponse<TraceLotCodeResponseDto>()

fun TraceLotCode.toTraceLotCodeResponseDto() = TraceLotCodeResponseDto(
    id = id,
    tlcVal = tlcVal,
    gtin = gtin,
    batchLot = batchLot,
    sscc = sscc,
    packDate = packDate,
    harvestDate = harvestDate,
    bestByDate = bestByDate,
    logSerialNo = logSerialNo,
    tlcSourceLocId = tlcSourceLoc.id,
    tlcSourceReference = tlcSourceReference,
    dateCreated = dateCreated,
    dateModified = dateModified,
    isDeleted = isDeleted,
    dateDeleted = dateDeleted,
    authUsername = authUsername,
)

fun TraceLotCodeRequestDto.toTraceLotCode(
    id: Long,
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
    tlcSourceLoc = tlcSource,
    tlcSourceReference = tlcSourceReference,
)
