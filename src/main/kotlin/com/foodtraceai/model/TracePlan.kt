// ----------------------------------------------------------------------------
// Copyright 2025 FoodTraceAI LLC or its affiliates. All Rights Reserved.
// ----------------------------------------------------------------------------
package com.foodtraceai.model

import jakarta.persistence.*
import java.time.LocalDate
import java.time.OffsetDateTime

/**
https://producetraceability.org/wp-content/uploads/2024/02/PTI-FSMA-204-Implementation-Guidance-FINAL-2.12.24-1.pdf
Look at p.17

https://www.ecfr.gov/current/title-21/section-1.1315
§ 1.1315 What traceability plan must I have for foods on the Food Traceability List that
I manufacture, process, pack, or hold?

Examples of traceability plans
https://www.fda.gov/media/174057/download?attachment - for farms

https://www.fda.gov/media/174058/download?attachment - for restaurants

https://www.fda.gov/media/181575/download?attachment - for sprouts
 **/


@Entity
data class TracePlan(
    @Id @GeneratedValue
    override val id: Long = 0,

    val issueDate: LocalDate? = null,

    @ManyToOne @JoinColumn
    override val location: Location,

    // (a)(1) A description of the procedures you use to maintain the records you are required
    // to keep under this subpart, including the format and location of these records.
    val descProcRecordMaintenance: String,

    // (a)(2) A description of the procedures you use to identify foods on the Food
    // TraceabilityList that you manufacture, process, pack, or hold;
    val descProcIdentifyFoods: String,

    // (a)(3) A description of how you assign traceability lot codes to foods on the
    // Food Traceability List in accordance with § 1.1320, if applicable;
    val descAssignTraceLotCodes: String,

    // (a)(4) A statement identifying a point of contact for questions regarding
    // your traceability plan and records; and
    @ManyToOne @JoinColumn
    val tracePlanContact: Contact,

    // (a)(5) If you grow or raise a food on the Food Traceability List (other than eggs),
    // a farm map showing the areas in which you grow or raise such foods.
    //
    // (a)(5)(i) Except as specified in paragraph (a)(5)(ii) of this section,
    // the farm map must show the location and name of each field
    // (or other growing area) in which you grow a food on the Food Traceability List,
    // including geographic coordinates and any other information needed to identify
    // the location of each field or growing area.
    //
    // (a)(5)(ii) For aquaculture farms, the farm map must show the location and name
    // of each container (e.g., pond, pool, tank, cage) in which you raise
    // seafood on the Food Traceability List, including geographic coordinates
    // and any other information needed to identify the location of each container.
    @Lob
    val farmMap: ByteArray? = null,

    @Column(updatable = false)
    override var dateCreated: OffsetDateTime = OffsetDateTime.now(),
    override var dateModified: OffsetDateTime = OffsetDateTime.now(),
    override var isDeleted: Boolean = false,
    override var dateDeleted: OffsetDateTime? = null,
    override var authUsername: String? = null,
) : BaseLocationModel<TracePlan>()

data class TracePlanRequestDto(
    val issueDate: LocalDate? = null,
    val locationId: Long,
    val descProcRecordMaintenance: String,
    val descProcIdentifyFoods: String,
    val descAssignTraceLotCodes: String,
    val tracePlanContactId: Long,
    val farmMap: ByteArray? = null,
)

data class TracePlanResponseDto(
    override val id: Long,
    val issueDate: LocalDate? = null,
    val locationId: Long,
    val descProcRecordMaintenance: String,
    val descProcIdentifyFoods: String,
    val descAssignTraceLotCodes: String,
    val tracePlanContactId: Long,
    val farmMap: ByteArray? = null,
    override var dateCreated: OffsetDateTime = OffsetDateTime.now(),
    override var dateModified: OffsetDateTime = OffsetDateTime.now(),
    override var isDeleted: Boolean = false,
    override var dateDeleted: OffsetDateTime? = null,
    override var authUsername: String? = null,
) : BaseResponse<TracePlanResponseDto>()

fun TracePlan.toTracePlanResponseDto() = TracePlanResponseDto(
    id = id,
    issueDate = issueDate,
    locationId = location.id,
    descProcRecordMaintenance = descProcRecordMaintenance,
    descProcIdentifyFoods = descProcIdentifyFoods,
    descAssignTraceLotCodes = descAssignTraceLotCodes,
    tracePlanContactId = tracePlanContact.id,
    farmMap = farmMap,
    dateCreated = dateCreated,
    dateModified = dateModified,
    isDeleted = isDeleted,
    dateDeleted = dateDeleted,
    authUsername = authUsername,
)

fun TracePlanRequestDto.toTracePlan(
    id: Long,
    location: Location,
    contact: Contact,
) = TracePlan(
    id = id,
    issueDate = issueDate,
    location = location,
    descProcRecordMaintenance = descProcRecordMaintenance,
    descProcIdentifyFoods = descProcIdentifyFoods,
    descAssignTraceLotCodes = descAssignTraceLotCodes,
    tracePlanContact = contact,
    farmMap = farmMap,
)