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
look at p.27

https://www.ecfr.gov/current/title-21/chapter-I/subchapter-A/part-1/subpart-S/subject-group-ECFRbfe98fb65ccc9f7/section-1.1345
§ 1.1345 What records must I keep when I receive a food on the
Food Traceability List?
 **/

@Entity
data class CteReceive(
    @Id @GeneratedValue override val id: Long = 0,

    @Enumerated(EnumType.STRING)
    override val cteType: CteType = CteType.Receive,

    // (a)(5) The location description for where the food was received;
    @ManyToOne
    @JoinColumn
    override val location: Location,

    // ************** KDEs *************
    // (a) Except as specified in paragraphs (b) and (c) of this section,
    // for each traceability lot of a food on the Food Traceability List
    // you receive, you must maintain records containing the following
    // information and linking this information to the traceability lot:

    // (a)(1) The traceability lot code for the food;
    @ManyToOne
    @JoinColumn
    val traceLotCode: TraceLotCode,

    // (a)(2) The quantity and unit of measure of the food
    // (e.g., 6 cases, 25 reusable plastic containers, 100 tanks, 200 pounds);
    override val quantity: Short,
    @Enumerated(EnumType.STRING)
    override val unitOfMeasure: UnitOfMeasure,

    // (a)(3) The product description for the food;
    @Enumerated(EnumType.STRING)
    override val ftlItem: FtlItem,
    override val variety: String,
    override val foodDesc: String,

    // (a)(4) The location description for the immediate previous source
    // (other than a transporter) for the food;
    @ManyToOne
    @JoinColumn
    val ipsLocation: Location,   // e.g. ShipFromLocation on CteShip

    // (a)(6) The date you received the food;
    val receiveDate: LocalDate,
    val receiveTime: OffsetDateTime? = null,    // Not required but useful

    // (a)(7) The location description for the traceability lot code source,
    // or the traceability lot code source reference; and
    @ManyToOne
    @JoinColumn
    val tlcSource: Location? = null,
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

    // TODO: see below
    //(b) For each traceability lot of a food on the Food Traceability List
    // you receive from a person to whom this subpart does not apply,
    // you must maintain records containing the following information and
    // linking this information to the traceability lot:

    //(1) The traceability lot code for the food, which you must assign if
    // one has not already been assigned (except that this paragraph does not
    // apply if you are a retail food establishment or restaurant);
    //
    //(2) The quantity and unit of measure of the food (e.g., 6 cases,
    // 25 reusable plastic containers, 100 tanks, 200 pounds);
    //
    //(3) The product description for the food;
    //
    //(4) The location description for the immediate previous source
    // (other than a transporter) for the food;
    //
    //(5) The location description for where the food was received
    // (i.e., the traceability lot code source), and (if applicable)
    // the traceability lot code source reference;
    //
    //(6) The date you received the food; and
    //
    //(7) The reference document type and reference document number.
    //
    //(c) This section does not apply to receipt of a food that occurs
    // before the food is initially packed (if the food is a raw agricultural
    // commodity not obtained from a fishing vessel) or to the receipt
    // of a food by the first land-based receiver (if the food is obtained
    // from a fishing vessel).
) : CteBase<CteReceive>()

data class CteReceiveDto(
    val id: Long,
    val cteType: CteType,
    val locationId: Long,   // Receive location
    val ftlItem: FtlItem,
    val variety: String,
    val traceLotCodeId: Long,
    val quantity: Short,
    val unitOfMeasure: UnitOfMeasure,
    val foodDesc: String,
    val ipsLocationId: Long,
    val receiveDate: LocalDate,
    val receiveTime: OffsetDateTime?,
    val tlcSourceId: Long?,
    val tlcSourceReference: String?,
    val referenceDocumentType: ReferenceDocumentType,
    val referenceDocumentNum: String,
    val dateCreated: OffsetDateTime,
    val dateModified: OffsetDateTime,
    val isDeleted: Boolean,
    val dateDeleted: OffsetDateTime?,
)

fun CteReceive.toCteReceiveDto() = CteReceiveDto(
    id = id,
    cteType = cteType,
    locationId = location.id,
    ftlItem = ftlItem,
    variety = variety,
    traceLotCodeId = traceLotCode.id,
    quantity = quantity,
    unitOfMeasure = unitOfMeasure,
    foodDesc = foodDesc,
    ipsLocationId = ipsLocation.id,
    receiveDate = receiveDate,
    receiveTime = receiveTime,
    tlcSourceId = tlcSource?.id,
    tlcSourceReference = tlcSourceReference,
    referenceDocumentType = referenceDocumentType,
    referenceDocumentNum = referenceDocumentNum,
    dateCreated = dateCreated,
    dateModified = dateModified,
    isDeleted = isDeleted,
    dateDeleted = dateDeleted,
)

fun CteReceiveDto.toCteReceive(
    location: Location,
    traceLotCode: TraceLotCode,
    ipsLocation: Location,
    tlcSource: Location?,
) = CteReceive(
    id = id,
    cteType = cteType,
    location = location,
    ftlItem = ftlItem,
    variety = variety,
    traceLotCode = traceLotCode,
    quantity = quantity,
    unitOfMeasure = unitOfMeasure,
    foodDesc = foodDesc,
    ipsLocation = ipsLocation,
    receiveDate = receiveDate,
    receiveTime = receiveTime,
    tlcSource = tlcSource,
    tlcSourceReference = tlcSourceReference,
    referenceDocumentType = referenceDocumentType,
    referenceDocumentNum = referenceDocumentNum,
    dateCreated = dateCreated,
    dateModified = dateModified,
    isDeleted = isDeleted,
    dateDeleted = dateDeleted,
)