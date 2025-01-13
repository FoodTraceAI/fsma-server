// ----------------------------------------------------------------------------
// Copyright 2025 FoodTraceAI LLC or its affiliates. All Rights Reserved.
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
https://producetraceability.org/wp-content/uploads/2024/02/PTI-FSMA-204-Implementation-Guidance-FINAL-2.12.24-1.pdf
look at p.27

https://www.ecfr.gov/current/title-21/chapter-I/subchapter-A/part-1/subpart-S/subject-group-ECFRbfe98fb65ccc9f7/section-1.1345
§ 1.1345 What records must I keep when I receive a food on the
Food Traceability List?
 **/

@Entity
data class CteReceiveExempt(
    @Id @GeneratedValue override val id: Long = 0,

    @Enumerated(EnumType.STRING)
    override val cteType: CteType = CteType.ReceiveExempt,

    @Enumerated(EnumType.STRING)
    override val ftlItem: FtlItem,

    // ************** KDEs *************
    // (b) For each traceability lot of a food on the Food Traceability List
    // you receive from a person to whom this subpart does not apply,
    // you must maintain records containing the following information and
    // linking this information to the traceability lot:

    // (b)(1) The traceability lot code for the food, which you must assign if
    // one has not already been assigned (except that this paragraph does not
    // apply if you are a retail food establishment or restaurant);
    @ManyToOne
    @JoinColumn
    val tlc: TraceLotCode,

    // (b)(2) The quantity and unit of measure of the food
    // (e.g., 6 cases, 25 reusable plastic containers, 100 tanks, 200 pounds);
    override val quantity: Int,
    @Enumerated(EnumType.STRING)
    override val unitOfMeasure: UnitOfMeasure,

    // (b)(3) The product description for the food;
    override val prodDesc: String,
    override val variety: String,

    // (b)(4) The location description for the immediate previous source
    // (other than a transporter) for the food;
    @ManyToOne
    @JoinColumn
    val ipsLocation: Location,   // e.g. ShipFromLocation on CteShip

    // (b)(5) The location description for where the food was received
    // (i.e., the traceability lot code source), and (if applicable)
    // the traceability lot code source reference;
    @ManyToOne
    @JoinColumn
    override val location: Location,    // location = tlcSource for this Cte
    val tlcSourceReference: String? = null,

    // (b)(6) The date you received the food;
    val receiveDate: LocalDate,
    val receiveTime: OffsetDateTime? = null,    // Not required but useful

    // (b)(7) The reference document type and reference document number.
    @Enumerated(EnumType.STRING)
    override val referenceDocumentType: ReferenceDocumentType,
    override val referenceDocumentNum: String,

    @Column(updatable = false)
    override var dateCreated: OffsetDateTime = OffsetDateTime.now(),
    override var dateModified: OffsetDateTime = OffsetDateTime.now(),
    override var isDeleted: Boolean = false,
    override var dateDeleted: OffsetDateTime? = null,

    //(c) This section does not apply to receipt of a food that occurs
    // before the food is initially packed (if the food is a raw agricultural
    // commodity not obtained from a fishing vessel) or to the receipt
    // of a food by the first land-based receiver (if the food is obtained
    // from a fishing vessel).
) : CteBase<CteReceiveExempt>()

data class CteReceiveExemptDto(
    val id: Long,
    val cteType: CteType,
    val ftlItem: FtlItem,
    val traceLotCodeId: Long,
    val quantity: Int,
    val unitOfMeasure: UnitOfMeasure,
    val prodDesc: String,
    val variety: String,
    val ipsLocationId: Long,
    val locationId: Long,   // Receive location
    val tlcSourceReference: String?,
    val receiveDate: LocalDate,
    val receiveTime: OffsetDateTime?,
    val referenceDocumentType: ReferenceDocumentType,
    val referenceDocumentNum: String,
    val dateCreated: OffsetDateTime,
    val dateModified: OffsetDateTime,
    val isDeleted: Boolean,
    val dateDeleted: OffsetDateTime?,
)

fun CteReceiveExempt.toCteReceiveExemptDto() = CteReceiveExemptDto(
    id = id,
    cteType = cteType,
    ftlItem = ftlItem,
    traceLotCodeId = tlc.id,
    quantity = quantity,
    unitOfMeasure = unitOfMeasure,
    prodDesc = prodDesc,
    variety = variety,
    ipsLocationId = ipsLocation.id,
    locationId = location.id,
    receiveDate = receiveDate,
    receiveTime = receiveTime,
    tlcSourceReference = tlcSourceReference,
    referenceDocumentType = referenceDocumentType,
    referenceDocumentNum = referenceDocumentNum,
    dateCreated = dateCreated,
    dateModified = dateModified,
    isDeleted = isDeleted,
    dateDeleted = dateDeleted,
)

fun CteReceiveExemptDto.toCteReceiveExempt(
    traceLotCode: TraceLotCode,
    ipsLocation: Location,
    location: Location,
) = CteReceiveExempt(
    id = id,
    cteType = cteType,
    ftlItem = ftlItem,
    tlc = traceLotCode,
    quantity = quantity,
    unitOfMeasure = unitOfMeasure,
    prodDesc = prodDesc,
    variety = variety,
    ipsLocation = ipsLocation,
    location = location,
    receiveDate = receiveDate,
    receiveTime = receiveTime,
    tlcSourceReference = tlcSourceReference,
    referenceDocumentType = referenceDocumentType,
    referenceDocumentNum = referenceDocumentNum,
    dateCreated = dateCreated,
    dateModified = dateModified,
    isDeleted = isDeleted,
    dateDeleted = dateDeleted,
)