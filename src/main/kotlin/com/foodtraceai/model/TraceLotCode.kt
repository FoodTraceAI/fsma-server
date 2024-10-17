// ----------------------------------------------------------------------------
// Copyright 2024 FoodTraceAI LLC or its affiliates. All Rights Reserved.
// ----------------------------------------------------------------------------
package com.foodtraceai.model

import com.foodtraceai.util.BatchLot
import com.foodtraceai.util.GTIN
import com.foodtraceai.util.LogSerialNum
import com.foodtraceai.util.Sscc
import jakarta.persistence.*
import java.time.LocalDate
import java.time.OffsetDateTime

@Entity
data class TraceLotCode(
    @Id @GeneratedValue
    override val id: Long = 0,
    val tlcVal: String,
    @Embedded val gtin: GTIN? = null,   // AI(01) Case GTIN - not required
    val batchLot: BatchLot? = null,  // AI(10) Case Batch/Lot - not required

    // Optional GS1 parameters
    val sscc: Sscc? = null,   // AI(00) - Pallet Serial Shipping Container Code
    val packDate: LocalDate? = null,    // AI(13)
    val harvestDate: LocalDate? = null, // AI(13)
    val bestByDate: LocalDate? = null,  // AI(15)
    val serial: LogSerialNum? = null, // AI(21) - Logistics Serial Number

    @Column(updatable = false)
    override var dateCreated: OffsetDateTime = OffsetDateTime.now(),
    override var dateModified: OffsetDateTime = OffsetDateTime.now(),
    override var isDeleted: Boolean = false,
    override var dateDeleted: OffsetDateTime? = null
) : BaseModel<TraceLotCode>()

data class TraceLotCodeDto(
    val id: Long = 0,
    val tlcVal: String,
    val gtin: GTIN?,
    val batchLot: BatchLot?,

    // Optional
    val sscc: Sscc? = null,   // AI(00) - Pallet Serial Shipping Container Code
    val packDate: LocalDate? = null,    // AI(13)
    val harvestDate: LocalDate? = null, // AI(13)
    val bestByDate: LocalDate? = null,  // AI(15)
    val serial: LogSerialNum? = null, // AI(21) - Logistics Serial Number

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
    serial = serial,
    dateCreated = dateCreated,
    dateModified = dateModified,
    isDeleted = isDeleted,
    dateDeleted = dateDeleted,
)

fun TraceLotCodeDto.toTraceLotCode() = TraceLotCode(
    id = id,
    tlcVal = tlcVal,
    gtin = gtin,
    batchLot = batchLot,
    sscc = sscc,
    packDate = packDate,
    harvestDate = harvestDate,
    bestByDate = bestByDate,
    serial = serial,
    dateCreated = dateCreated,
    dateModified = dateModified,
    isDeleted = isDeleted,
    dateDeleted = dateDeleted,
)
