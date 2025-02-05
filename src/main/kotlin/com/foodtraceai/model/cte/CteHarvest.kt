// ----------------------------------------------------------------------------
// Copyright 2025 FoodTraceAI LLC or its affiliates. All Rights Reserved.
// ----------------------------------------------------------------------------
package com.foodtraceai.model.cte

import com.foodtraceai.model.BaseResponse
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
https://producetraceability.org/wp-content/uploads/2024/02/PTI-FSMA-204-Implementation-Guidance-FINAL-2.12.24-1.pdf
Look at p.20

https://www.ecfr.gov/current/title-21/chapter-I/subchapter-A/part-1/subpart-S/subject-group-ECFRbfe98fb65ccc9f7/section-1.1325#p-1.1325(a)
§ 1.1325 What records must I keep and provide when I harvest or cool
a raw agricultural commodity on the Food Traceability List?
 **/

// (a) Harvesting.
@Entity
data class CteHarvest(
    @Id @GeneratedValue override val id: Long = 0,

    @Enumerated(EnumType.STRING)
    override val cteType: CteType = CteType.Harvest,

    @Enumerated(EnumType.STRING)
    override val ftlItem: FtlItem,

    // Location for this CTE
    @ManyToOne @JoinColumn
    override val location: Location,

    // Not used in this CTE
    @ManyToOne @JoinColumn
    override val tlc: TraceLotCode? = null,

    // ************** KDEs *************
    // (a)(1) For each raw agricultural commodity (not obtained from a fishing vessel)
    // on the Food Traceability List that you harvest, you must maintain records
    // containing the following information:

    // (a)(1)(i) The location description for the immediate subsequent recipient
    // (other than a transporter) of the food;
    // Will usually be the initial packer but could be the cooler
    @ManyToOne @JoinColumn
    val isrLocation: Location,

    // (a)(1)(ii) The commodity and, if applicable, variety of the food;
    // Called commodity for Harvest CTEs
    override val prodDesc: String,  // Commodity for this CTE
    override val variety: String? = null,   // variety of commodity if applicable

    // The harvest quantity and unit of measure
    // (a)(1)(iii) The quantity and unit of measure of the food (e.g., 75 bins, 200 pounds);
    override val quantity: Int,
    @Enumerated(EnumType.STRING)
    override val unitOfMeasure: UnitOfMeasure,

    // (a)(1)(iv) The location description for the farm where the food was harvested;
    @ManyToOne @JoinColumn
    val harvestLocation: Location,

    // (a)(1)(v) For produce, the name of the field or other growing area from which the
    // food was harvested (which must correspond to the name used by the grower),
    // or other information identifying the harvest location at least as precisely
    // as the field or other growing area name;
    val fieldName: String,
    val fieldDesc: String,

    // (a)(1)(vi) For aqua-cultured food, the name of the container
    // (e.g., pond, pool, tank, cage) from which the food was harvested
    // (which must correspond to the container name
    // used by the aquaculture farmer) or other information identifying the harvest
    // location at least as precisely as the container name;
    val containerName: String? = null,
    val containerDesc: String? = null,

    // (a)(1)(vii) The date of harvesting;
    val harvestDate: LocalDate,

    // (a)(1)(viii) The reference document type and reference document number.
    @Enumerated(EnumType.STRING)
    override val referenceDocumentType: ReferenceDocumentType,
    override val referenceDocumentNum: String,

    // (a)(2) For each raw agricultural commodity (not obtained from a fishing vessel)
    // on the Food Traceability List that you harvest, you must provide (in electronic,
    // paper, or other written form) your business name, phone number, and the
    // information in paragraphs (a)(1)(i) through (vii) of this section to the
    // initial packer of the raw agricultural commodity you harvest, either directly
    // or through the supply chain.

    @Column(updatable = false)
    override var dateCreated: OffsetDateTime = OffsetDateTime.now(),
    override var dateModified: OffsetDateTime = OffsetDateTime.now(),
    override var isDeleted: Boolean = false,
    override var dateDeleted: OffsetDateTime? = null,
    override var authUsername: String? = null,
) : CteBase<CteHarvest>()

data class CteHarvestRequestDto(
    val ftlItem: FtlItem,
    val locationId: Long,
    val isrLocationId: Long,
    val prodDesc: String,   // Commodity
    val variety: String?,
    val quantity: Int,
    val unitOfMeasure: UnitOfMeasure,
    val harvestLocationId: Long,
    val fieldName: String,
    val fieldDesc: String,
    val containerName: String?,
    val containerDesc: String?,
    val harvestDate: LocalDate,
    val referenceDocumentType: ReferenceDocumentType,
    val referenceDocumentNum: String,
)

data class CteHarvestResponseDto(
    override var id: Long,
    val cteType: CteType,
    val ftlItem: FtlItem,
    val locationId: Long,
    val isrLocationId: Long,
    val prodDesc: String,   // Commodity
    val variety: String?,
    val quantity: Int,
    val unitOfMeasure: UnitOfMeasure,
    val harvestLocationId: Long,
    val fieldName: String,
    val fieldDesc: String,
    val containerName: String?,
    val containerDesc: String?,
    val harvestDate: LocalDate,
    val referenceDocumentType: ReferenceDocumentType,
    val referenceDocumentNum: String,
    override var dateCreated: OffsetDateTime = OffsetDateTime.now(),
    override var dateModified: OffsetDateTime = OffsetDateTime.now(),
    override var isDeleted: Boolean = false,
    override var dateDeleted: OffsetDateTime? = null,
    override var authUsername: String? = null,
) : BaseResponse<CteHarvestResponseDto>()

fun CteHarvest.toCteHarvestResponseDto() = CteHarvestResponseDto(
    id = id,
    cteType = cteType,
    ftlItem = ftlItem,
    locationId = location.id,
    isrLocationId = isrLocation.id,
    prodDesc = prodDesc,
    variety = variety,
    quantity = quantity,
    unitOfMeasure = unitOfMeasure,
    harvestLocationId = harvestLocation.id,
    fieldName = fieldName,
    fieldDesc = fieldDesc,
    containerName = containerName,
    containerDesc = containerDesc,
    harvestDate = harvestDate,
    referenceDocumentType = referenceDocumentType,
    referenceDocumentNum = referenceDocumentNum,
    dateCreated = dateCreated,
    dateModified = dateModified,
    isDeleted = isDeleted,
    dateDeleted = dateDeleted,
    authUsername = authUsername,
)

fun CteHarvestRequestDto.toCteHarvest(
    id: Long,
    location: Location,
    isrLocation: Location,
    harvestLocation: Location,
) = CteHarvest(
    id = id,
    cteType = CteType.Harvest,
    ftlItem = ftlItem,
    location = location,
    isrLocation = isrLocation,
    prodDesc = prodDesc,
    variety = variety,
    quantity = quantity,
    unitOfMeasure = unitOfMeasure,
    harvestLocation = harvestLocation,
    fieldName = fieldName,
    fieldDesc = fieldDesc,
    containerName = containerName,
    containerDesc = containerDesc,
    harvestDate = harvestDate,
    referenceDocumentType = referenceDocumentType,
    referenceDocumentNum = referenceDocumentNum,
)