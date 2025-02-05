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
look at p.26

https://www.ecfr.gov/current/title-21/chapter-I/subchapter-A/part-1/subpart-S/subject-group-ECFRbfe98fb65ccc9f7/section-1.1340
§ 1.1340 What records must I keep and provide when I ship a food on the
Food Traceability List?
 **/

@Entity
data class CteShip(
    @Id @GeneratedValue override val id: Long = 0,

    @Enumerated(EnumType.STRING)
    override val cteType: CteType = CteType.Ship,

    @Enumerated(EnumType.STRING)
    override val ftlItem: FtlItem,

    // ************** KDEs *************
    // (a) For each traceability lot of a food on the Food Traceability List
    // you ship, you must maintain records containing the following information
    // and linking this information to the traceability lot:

    // (a)(1) The traceability lot code for the food;
    @ManyToOne @JoinColumn
    override val tlc: TraceLotCode,  // from Initial Packer or Transformer

    // (a)(2) The quantity and unit of measure of the food
    // (e.g., 6 cases, 25 reusable plastic containers, 100 tanks, 200 pounds);
    override val quantity: Int,   // from Initial Packer or Transformer
    @Enumerated(EnumType.STRING)
    override val unitOfMeasure: UnitOfMeasure,   // from Initial Packer or Transformer

    // (a)(3) The product description for the food;
    override val prodDesc: String,
    override val variety: String,

    // (a)(4) The location description for the immediate subsequent recipient
    // (other than a transporter) of the food;
    @ManyToOne @JoinColumn
    val shipToLocation: Location,

    // (a)(5) The location description for the location from which you shipped the food;
    @ManyToOne @JoinColumn
    override val location: Location,    // location = shipFromLocation

    // (a)(6) The date you shipped the food;
    val shipDate: LocalDate,
    val shipTime: OffsetDateTime,   // Not required but possibly useful

    // (a)(7) The location description for the traceability lot code source,
    // or the traceability lot code source reference; and
    @ManyToOne @JoinColumn
    val tlcSource: Location,
    val tlcSourceReference: String? = null,

    // (a)(8) The reference document type and reference document number.
    @Enumerated(EnumType.STRING)
    override val referenceDocumentType: ReferenceDocumentType,
    override val referenceDocumentNum: String,

    @Column(updatable = false)
    override var dateCreated: OffsetDateTime = OffsetDateTime.now(),
    override var dateModified: OffsetDateTime = OffsetDateTime.now(),
    override var isDeleted: Boolean = false,
    override var dateDeleted: OffsetDateTime? = null,
    override var authUsername: String? = null,

    // (b) You must provide (in electronic, paper, or other written form) the
    // information in paragraphs (a)(1) through (7) of this section
    // to the immediate subsequent recipient (other than a transporter)
    // of each traceability lot that you ship.

    // (c) This section does not apply to the shipment of a food that occurs
    // before the food is initially packed (if the food is a raw agricultural
    // commodity not obtained from a fishing vessel).
) : CteBase<CteShip>()

data class CteShipRequestDto(
    val ftlItem: FtlItem,
    val tlcId: Long,
    val quantity: Int,
    val unitOfMeasure: UnitOfMeasure,
    val prodDesc: String,
    val variety: String = "",
    val shipToLocationId: Long,
    val locationId: Long,   // ShipFromLocation
    val shipDate: LocalDate = LocalDate.now(),
    val shipTime: OffsetDateTime = OffsetDateTime.now(),
    val tlcSourceId: Long,
    val tlcSourceReference: String? = null,
    val referenceDocumentType: ReferenceDocumentType,
    val referenceDocumentNum: String,
)

data class CteShipResponseDto(
    override var id: Long,
    val cteType: CteType,
    val ftlItem: FtlItem,
    val tlcId: Long,
    val quantity: Int,
    val unitOfMeasure: UnitOfMeasure,
    val prodDesc: String,
    val variety: String,
    val shipToLocationId: Long,
    val locationId: Long,   // ShipFromLocation
    val shipDate: LocalDate,
    val shipTime: OffsetDateTime,
    val tlcSourceId: Long,
    val tlcSourceReference: String?,
    val referenceDocumentType: ReferenceDocumentType,
    val referenceDocumentNum: String,
    override var dateCreated: OffsetDateTime = OffsetDateTime.now(),
    override var dateModified: OffsetDateTime = OffsetDateTime.now(),
    override var isDeleted: Boolean = false,
    override var dateDeleted: OffsetDateTime? = null,
    override var authUsername: String? = null,
) : BaseResponse<CteShipResponseDto>()

fun CteShip.toCteShipResponseDto() = CteShipResponseDto(
    id = id,
    cteType = cteType,
    ftlItem = ftlItem,
    tlcId = tlc.id,
    quantity = quantity,
    unitOfMeasure = unitOfMeasure,
    prodDesc = prodDesc,
    variety = variety,
    shipToLocationId = shipToLocation.id,
    locationId = location.id,   // ShipFromLocation
    shipDate = shipDate,
    shipTime = shipTime,
    tlcSourceId = tlcSource.id,
    tlcSourceReference = tlcSourceReference,
    referenceDocumentType = referenceDocumentType,
    referenceDocumentNum = referenceDocumentNum,
    dateCreated = dateCreated,
    dateModified = dateModified,
    isDeleted = isDeleted,
    dateDeleted = dateDeleted,
    authUsername = authUsername,
)

fun CteShipRequestDto.toCteShip(
    id: Long,
    tlc: TraceLotCode,
    shipToLocation: Location,
    location: Location,
    tlcSource: Location,
) = CteShip(
    id = id,
    cteType = CteType.Ship,
    ftlItem = ftlItem,
    tlc = tlc,
    quantity = quantity,
    unitOfMeasure = unitOfMeasure,
    prodDesc = prodDesc,
    variety = variety,
    shipToLocation = shipToLocation,
    location = location,    // ShipFromLocation
    shipDate = shipDate,
    shipTime = shipTime,
    tlcSource = tlcSource,
    tlcSourceReference = tlcSourceReference,
    referenceDocumentType = referenceDocumentType,
    referenceDocumentNum = referenceDocumentNum,
)