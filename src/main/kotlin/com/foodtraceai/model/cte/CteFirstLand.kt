// ----------------------------------------------------------------------------
// Copyright 2024 FoodTraceAI LLC or its affiliates. All Rights Reserved.
// ----------------------------------------------------------------------------
package com.foodtraceai.model.cte

import com.foodtraceai.model.Location
import com.foodtraceai.model.TraceLotCode
import com.foodtraceai.util.CteType
import com.foodtraceai.util.FtlItem
import com.foodtraceai.util.ReferenceDocumentType
import com.foodtraceai.util.UnitOfMeasure
import jakarta.persistence.*
import java.time.LocalDate
import java.time.OffsetDateTime

/**
https://producetraceability.org/wp-content/uploads/2024/02/PTI-FSMA-204-Implementation-Guidance-FINAL-2.12.24.pdf
Does not cover this case

https://www.ecfr.gov/current/title-21/chapter-I/subchapter-A/part-1/subpart-S/subject-group-ECFRbfe98fb65ccc9f7/section-1.1335
§ 1.1335 What records must I keep when I am the first land-based receiver of a
food on the Food Traceability List that was obtained from a fishing vessel?
 **/

@Entity
@Table(name = "cte_first_land_receive")
data class CteFirstLand(
    @Id @GeneratedValue override val id: Long = 0,

    @Enumerated(EnumType.STRING)
    override val cteType: CteType = CteType.FirstLandReceive,

    // Location for this CTE
    @ManyToOne @JoinColumn
    override val location: Location,

    @Enumerated(EnumType.STRING)
    override val ftlItem: FtlItem,

    // (a) The traceability lot code you assigned;
    @ManyToOne @JoinColumn
    val tlc: TraceLotCode,

    // (b) The species and/or acceptable market name for unpackaged food,
    // or the product description for packaged food;
    override val variety: String,
    override val foodDesc: String,

    //(c) The quantity and unit of measure of the food (e.g., 300 kg);
    override val quantity: Short,
    @Enumerated(EnumType.STRING)
    override val unitOfMeasure: UnitOfMeasure,

    //(d) The harvest date range and locations (as identified under the
    // National Marine Fisheries Service Ocean Geographic Code,
    // the United Nations Food and Agriculture Organization
    // Major Fishing Area list, or any other widely recognized
    // geographical location standard) for the trip during which
    // the food was caught;
    val harvestDateBegin: LocalDate,
    val harvestDateEnd: LocalDate,
    val harvestLocation: String,

    //(e) The location description for the first land-based receiver
    // (i.e., the traceability lot code source), and (if applicable)
    // the traceability lot code source reference;
    @ManyToOne(cascade = [CascadeType.ALL])
    @JoinColumn
    val tlcSource: Location? = null,
    val tlcSourceReference: String? = null,

    //(f) The date the food was landed; and
    val landedDate: LocalDate,
    val landedTime: OffsetDateTime,    // not required

    //(g) The reference document type and reference document number.
    @Enumerated(EnumType.STRING)
    override val referenceDocumentType: ReferenceDocumentType,
    override val referenceDocumentNum: String,

    @Column(updatable = false)
    override var dateCreated: OffsetDateTime = OffsetDateTime.now(),
    override var dateModified: OffsetDateTime = OffsetDateTime.now(),
    override var isDeleted: Boolean = false,
    override var dateDeleted: OffsetDateTime? = null
) : CteBase<CteFirstLand>()

data class CteFirstLandDto(
    val id: Long,
    val cteType: CteType,
    val locationId: Long,
    val ftlItem: FtlItem,
    val tlc: TraceLotCode,
    val variety: String,
    val foodDesc: String,
    val quantity: Short,
    val unitOfMeasure: UnitOfMeasure,
    val harvestDateBegin: LocalDate,
    val harvestDateEnd: LocalDate,
    val harvestLocation: String,
    val tlcSourceId: Long?,
    val tlcSourceReference: String?,
    val landedDate: LocalDate,
    val landedTime: OffsetDateTime,    // not required
    val referenceDocumentType: ReferenceDocumentType,
    val referenceDocumentNum: String,
    val dateCreated: OffsetDateTime,
    val dateModified: OffsetDateTime,
    val isDeleted: Boolean,
    val dateDeleted: OffsetDateTime?,
)

fun CteFirstLand.toCteFirstLandDto() = CteFirstLandDto(
    id = id,
    cteType = cteType,
    locationId = location.id,
    ftlItem = ftlItem,
    tlc = tlc,
    variety = variety,
    foodDesc = foodDesc,
    quantity = quantity,
    unitOfMeasure = unitOfMeasure,
    harvestDateBegin = harvestDateBegin,
    harvestDateEnd = harvestDateEnd,
    harvestLocation = harvestLocation,
    tlcSourceId = tlcSource?.id,
    tlcSourceReference = tlcSourceReference,
    landedDate = landedDate,
    landedTime = landedTime,    // not required
    referenceDocumentType = referenceDocumentType,
    referenceDocumentNum = referenceDocumentNum,
    dateCreated = dateCreated,
    dateModified = dateModified,
    isDeleted = isDeleted,
    dateDeleted = dateDeleted
)

fun CteFirstLandDto.toCteFirstLand(
    location: Location,
    tlcSource: Location?,
) = CteFirstLand(
    id = id,
    cteType = cteType,
    location = location,
    ftlItem = ftlItem,
    tlc = tlc,
    variety = variety,
    foodDesc = foodDesc,
    quantity = quantity,
    unitOfMeasure = unitOfMeasure,
    harvestDateBegin = harvestDateBegin,
    harvestDateEnd = harvestDateEnd,
    harvestLocation = harvestLocation,
    tlcSource = tlcSource,
    tlcSourceReference = tlcSourceReference,
    landedDate = landedDate,
    landedTime = landedTime,    // not required
    referenceDocumentType = referenceDocumentType,
    referenceDocumentNum = referenceDocumentNum,
    dateCreated = dateCreated,
    dateModified = dateModified,
    isDeleted = isDeleted,
    dateDeleted = dateDeleted
)