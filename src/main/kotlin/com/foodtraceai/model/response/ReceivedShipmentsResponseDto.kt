// ----------------------------------------------------------------------------
// Copyright 2025 FoodTraceAI LLC or its affiliates. All Rights Reserved.
// ----------------------------------------------------------------------------
package com.foodtraceai.model.response

import com.foodtraceai.model.BaseResponse
import com.foodtraceai.model.cte.CteReceive
import com.foodtraceai.util.FtlItem
import com.foodtraceai.util.ReferenceDocumentType
import com.foodtraceai.util.UnitOfMeasure
import java.time.LocalDate
import java.time.OffsetDateTime

data class ReceivedShipmentsResponseDto(
    override val id: Long,
    val cteReceiveId: Long,
    val tlcId: Long,
    val tlcVal: String,
    val tlcSrc: String,
    val tlcSrcRef: String?,
    val quantity: Int,
    val unitOfMeasure: UnitOfMeasure,
    val ftlItem: FtlItem,
    val variety: String,
    val prodDesc: String,
    val shipToBus: String,
    val shipToCity: String,
    val shipFromBus: String,
    val shipFromCity: String,
    val receiveDate: LocalDate,
    val referenceDocumentType: ReferenceDocumentType,
    val referenceDocumentNum: String,
    override var dateCreated: OffsetDateTime = OffsetDateTime.now(),
    override var dateModified: OffsetDateTime = OffsetDateTime.now(),
    override var isDeleted: Boolean = false,
    override var dateDeleted: OffsetDateTime? = null,
    override var authUsername: String? = null,
) : BaseResponse<ReceivedShipmentsResponseDto>()

fun CteReceive.toReceivedShipmentsResponseDto(): ReceivedShipmentsResponseDto {
    return ReceivedShipmentsResponseDto(
        id = id,
        cteReceiveId = id,
        tlcId = tlc.id,
        tlcVal = tlc.tlcVal,
        tlcSrc = tlc.tlcSourceLoc.foodBus.foodBusName + " " + tlc.tlcSourceLoc.address.city,
        tlcSrcRef = tlc.tlcSourceReference,
        ftlItem = ftlItem,
        variety = variety,
        quantity = quantity,
        unitOfMeasure = unitOfMeasure,
        prodDesc = prodDesc,
        shipToBus = location.foodBus.foodBusName,
        shipToCity = location.address.city,
        shipFromBus = ipsLocation.foodBus.foodBusName,
        shipFromCity = ipsLocation.address.city,
        receiveDate = receiveDate,
        referenceDocumentNum = referenceDocumentNum,
        referenceDocumentType = referenceDocumentType,
        dateCreated = dateCreated,
        dateModified = dateModified,
        isDeleted = isDeleted,
        dateDeleted = dateDeleted,
        authUsername = authUsername,
    )
}