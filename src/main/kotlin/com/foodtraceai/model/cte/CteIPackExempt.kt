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
Look at p.24 Look for Exempt Entities

https://www.ecfr.gov/current/title-21/chapter-I/subchapter-A/part-1/subpart-S/subject-group-ECFRbfe98fb65ccc9f7/section-1.1330
§ 1.1330 What records must I keep when I am performing the initial packing of a raw
agricultural commodity (other than a food obtained from a fishing vessel) on the
Food Traceability List?
 **/

@Entity
@Table(name = "cte_ipack_exempt")
data class CteIPackExempt(
    @Id @GeneratedValue override val id: Long = 0,

    @Enumerated(EnumType.STRING)
    override val cteType: CteType = CteType.InitPackExempt,

    @Enumerated(EnumType.STRING)
    override val ftlItem: FtlItem,

    // Location for this CTE
    @ManyToOne @JoinColumn
    override val location: Location,

    // ************** KDEs *************
    // (c) For each traceability lot of a raw agricultural commodity
    // (other than a food obtained from a fishing vessel) on the Food
    // Traceability List you initially pack that you receive from a
    // person to whom this subpart does not apply, you must maintain
    // records containing the following information and linking this
    // information to the traceability lot:

    // (c)(1) The commodity and, if applicable, variety of the food received;
    override val prodDesc: String,  // Commodity for this CTE
    override val variety: String,

    // (c)(2) The date you received the food;
    val receiveDate: LocalDate,
    val receiveTime: OffsetDateTime,    // Not required but useful

    // (c)(3) The quantity and unit of measure of the food received (e.g., 75 bins, 200 pounds);
    val receiveQuantity: Double,
    @Enumerated(EnumType.STRING)
    val receiveUnitOfMeasure: UnitOfMeasure,

    // (c)(4) The location description for the person from whom you received the food;
    @ManyToOne @JoinColumn
    val sourceLocation: Location,

    // (c)(5) The traceability lot code you assigned;
    @ManyToOne @JoinColumn
    override val tlc: TraceLotCode,

    // (c)(6) The product description of the packed food;
    // TODO From PTI This is the description for the Case,
    //  not the saleable unit in the case.
    //  Product description should include:
    //      product name (including, if applicable,
    //      the brand name, commodity, and variety)
    //      packaging size
    //      packaging style
    val packFoodDesc: String,

    // (c)(7) The quantity and unit of measure of the packed food
    // (e.g., 6 cases, 25 reusable plastic containers, 100 tanks, 200 pounds);
    override val quantity: Int, // pack quantity
    @Enumerated(EnumType.STRING)
    override val unitOfMeasure: UnitOfMeasure,  // packed unit of measure

    // (c)(8) The location description for where you initially packed the food
    // (i.e., the traceability lot code source), and (if applicable) the traceability
    // lot code source reference;
    @ManyToOne @JoinColumn
    val tlcSource: Location? = null,    // i.e., the location since TLC is created at this CTE
    val tlcSourceReference: String? = null,

    // (c)(9) The date of initial packing; and
    val packDate: LocalDate,

    // (c)(10) The reference document type and reference document number.
    @Enumerated(EnumType.STRING)
    override val referenceDocumentType: ReferenceDocumentType,
    override val referenceDocumentNum: String,

    @Column(updatable = false)
    override var dateCreated: OffsetDateTime = OffsetDateTime.now(),
    override var dateModified: OffsetDateTime = OffsetDateTime.now(),
    override var isDeleted: Boolean = false,
    override var dateDeleted: OffsetDateTime? = null,
    override var authUsername: String? = null,
) : CteBase<CteIPackExempt>()

data class CteIPackExemptRequestDto(
    val ftlItem: FtlItem,
    val locationId: Long,
    val prodDesc: String,
    val variety: String,
    val receiveDate: LocalDate,
    val receiveTime: OffsetDateTime,
    val receiveQuantity: Double,
    val receiveUnitOfMeasure: UnitOfMeasure,
    val sourceLocationId: Long,
    val packTlcId: Long,
    val packFoodDesc: String,
    val quantity: Int,
    val unitOfMeasure: UnitOfMeasure,
    val packTlcSourceId: Long?,
    val packTlcSourceReference: String?,
    val packDate: LocalDate,
    val referenceDocumentType: ReferenceDocumentType,
    val referenceDocumentNum: String,
)

data class CteIPackExemptResponseDto(
    override var id: Long,
    val cteType: CteType,
    val ftlItem: FtlItem,
    val locationId: Long,
    val prodDesc: String,
    val variety: String,
    val receiveDate: LocalDate,
    val receiveTime: OffsetDateTime,
    val receiveQuantity: Double,
    val receiveUnitOfMeasure: UnitOfMeasure,
    val sourceLocationId: Long,
    val tlcId: Long,
    val packFoodDesc: String,
    val quantity: Int,
    val unitOfMeasure: UnitOfMeasure,
    val packTlcSourceId: Long?,
    val packTlcSourceReference: String?,
    val packDate: LocalDate,
    val referenceDocumentType: ReferenceDocumentType,
    val referenceDocumentNum: String,
    override var dateCreated: OffsetDateTime = OffsetDateTime.now(),
    override var dateModified: OffsetDateTime = OffsetDateTime.now(),
    override var isDeleted: Boolean = false,
    override var dateDeleted: OffsetDateTime? = null,
    override var authUsername: String? = null,
) : BaseResponse<CteIPackExemptResponseDto>()

fun CteIPackExempt.toCteIPackExemptResponseDto() = CteIPackExemptResponseDto(
    id = id,
    cteType = cteType,
    locationId = location.id,
    ftlItem = ftlItem,
    variety = variety,
    prodDesc = prodDesc,
    receiveDate = receiveDate,
    receiveTime = receiveTime,
    receiveQuantity = receiveQuantity,
    receiveUnitOfMeasure = receiveUnitOfMeasure,
    sourceLocationId = sourceLocation.id,
    tlcId = tlc.id,
    packFoodDesc = packFoodDesc,
    quantity = quantity,
    unitOfMeasure = unitOfMeasure,
    packTlcSourceId = tlcSource?.id,
    packTlcSourceReference = tlcSourceReference,
    packDate = packDate,
    referenceDocumentType = referenceDocumentType,
    referenceDocumentNum = referenceDocumentNum,
    dateCreated = dateCreated,
    dateModified = dateModified,
    isDeleted = isDeleted,
    dateDeleted = dateDeleted,
    authUsername = authUsername,
)

fun CteIPackExemptRequestDto.toCteIPackExempt(
    id:Long,
    location: Location,
    sourceLocation: Location,
    tlc: TraceLotCode,
    tlcSource: Location?,
) = CteIPackExempt(
    id = id,
    cteType = CteType.InitPackExempt,
    location = location,
    ftlItem = ftlItem,
    variety = variety,
    prodDesc = prodDesc,
    receiveDate = receiveDate,
    receiveTime = receiveTime,
    receiveQuantity = receiveQuantity,
    receiveUnitOfMeasure = receiveUnitOfMeasure,
    sourceLocation = sourceLocation,
    tlc = tlc,
    packFoodDesc = packFoodDesc,
    quantity = quantity,
    unitOfMeasure = unitOfMeasure,
    tlcSource = tlcSource,
    tlcSourceReference = packTlcSourceReference,
    packDate = packDate,
    referenceDocumentType = referenceDocumentType,
    referenceDocumentNum = referenceDocumentNum,
)