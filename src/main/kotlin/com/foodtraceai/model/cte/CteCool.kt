// ----------------------------------------------------------------------------
// Copyright 2024 FoodTraceAI LLC or its affiliates. All Rights Reserved.
// ----------------------------------------------------------------------------
package com.foodtraceai.model.cte

import com.foodtraceai.model.Location
import com.foodtraceai.util.CteType
import com.foodtraceai.util.FtlItem
import com.foodtraceai.util.ReferenceDocumentType
import com.foodtraceai.util.UnitOfMeasure
import jakarta.persistence.*
import java.time.LocalDate
import java.time.OffsetDateTime

/**
https://producetraceability.org/wp-content/uploads/2024/02/PTI-FSMA-204-Implementation-Guidance-FINAL-2.12.24.pdf
Look at p.21

https://www.ecfr.gov/current/title-21/chapter-I/subchapter-A/part-1/subpart-S/subject-group-ECFRbfe98fb65ccc9f7/section-1.1325#p-1.1325(b)
§ 1.1325 What records must I keep and provide when I harvest or cool
a raw agricultural commodity on the Food Traceability List?
 **/

// (b) Cooling before initial packing.

@Entity
data class CteCool(
    @Id @GeneratedValue override val id: Long = 0,

    @Enumerated(EnumType.STRING)
    override val cteType: CteType = CteType.Cool,

    // Location for this CTE
    @ManyToOne @JoinColumn
    override val location: Location,

    // ************** KDEs *************
    // (b)(1) For each raw agricultural commodity
    // (not obtained from a fishing vessel) on the Food Traceability List
    // that you cool before it is initially packed, you must maintain records
    // containing the following information:

    // (b)(1)(i) The location description for the immediate subsequent
    // recipient (other than a transporter) of the food;
    @ManyToOne @JoinColumn
    val isrLocation: Location,

    // (b)(1)(ii) The commodity and, if applicable, variety of the food;
    @Enumerated(EnumType.STRING)
    override val ftlItem: FtlItem,  // Commodity harvested
    override val variety: String,
    override val foodDesc: String,  // not required for this CTE

    // (b)(1)(iii) The quantity and unit of measure of the food (e.g., 75 bins, 200 pounds);
    override val quantity: Short,
    @Enumerated(EnumType.STRING)
    override val unitOfMeasure: UnitOfMeasure,

    // (b)(1)(iv) The location description for where you cooled the food;
    @ManyToOne @JoinColumn
    val coolLocation: Location,

    // (b)(1)(v) The date of cooling;
    val coolDate: LocalDate,

    // (b)(1)(vi) The location description for the farm where the food was harvested;
    @ManyToOne @JoinColumn
    val harvestLocation: Location,

    // (b)(1)(vii) The reference document type and reference document number.
    @Enumerated(EnumType.STRING)
    override val referenceDocumentType: ReferenceDocumentType,
    override val referenceDocumentNum: String,

    // (b)(2) For each raw agricultural commodity (not obtained from a fishing vessel)
    // on the Food Traceability List that you harvest, you must provide (in electronic,
    // paper, or other written form) your business name, phone number, and the
    // information in paragraphs (a)(1)(i) through (vii) of this section to the
    // initial packer of the raw agricultural commodity you harvest, either directly
    // or through the supply chain.

    @Column(updatable = false)
    override var dateCreated: OffsetDateTime = OffsetDateTime.now(),
    override var dateModified: OffsetDateTime = OffsetDateTime.now(),
    override var isDeleted: Boolean = false,
    override var dateDeleted: OffsetDateTime? = null
) : CteBase<CteCool>()

data class CteCoolDto(
    val id: Long,
    val cteType: CteType = CteType.Cool,
    val locationId: Long,
    val isrLocationId: Long,
    val ftlItem: FtlItem,
    val variety: String,
    val foodDesc: String,
    val quantity: Short,
    val unitOfMeasure: UnitOfMeasure,
    val coolLocation: Location,
    val coolDate: LocalDate,
    val harvestLocation: Location,
    val referenceDocumentType: ReferenceDocumentType,
    val referenceDocumentNum: String,
    val dateCreated: OffsetDateTime,
    val dateModified: OffsetDateTime,
    val isDeleted: Boolean,
    val dateDeleted: OffsetDateTime?,
)

fun CteCool.toCteCoolDto() = CteCoolDto(
    id = id,
    cteType = cteType,
    locationId = location.id,
    isrLocationId = isrLocation.id,
    ftlItem = ftlItem,
    variety = variety,
    foodDesc = foodDesc,
    quantity = quantity,
    unitOfMeasure = unitOfMeasure,
    coolLocation = coolLocation,
    coolDate = coolDate,
    harvestLocation = harvestLocation,
    referenceDocumentType = referenceDocumentType,
    referenceDocumentNum = referenceDocumentNum,
    dateCreated = dateCreated,
    dateModified = dateModified,
    isDeleted = isDeleted,
    dateDeleted = dateDeleted,
)

fun CteCoolDto.toCteCool(
    location: Location,
    isrLocation: Location,
) = CteCool(
    id = id,
    cteType = cteType,
    location = location,
    isrLocation = isrLocation,
    ftlItem = ftlItem,
    variety = variety,
    foodDesc = foodDesc,
    quantity = quantity,
    unitOfMeasure = unitOfMeasure,
    coolLocation = coolLocation,
    coolDate = coolDate,
    harvestLocation = harvestLocation,
    referenceDocumentType = referenceDocumentType,
    referenceDocumentNum = referenceDocumentNum,
    dateCreated = dateCreated,
    dateModified = dateModified,
    isDeleted = isDeleted,
    dateDeleted = dateDeleted,
)