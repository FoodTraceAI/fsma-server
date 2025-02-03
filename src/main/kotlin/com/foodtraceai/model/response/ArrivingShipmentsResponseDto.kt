// ----------------------------------------------------------------------------
// Copyright 2025 FoodTraceAI LLC or its affiliates. All Rights Reserved.
// ----------------------------------------------------------------------------
package com.foodtraceai.model.response

import com.foodtraceai.model.BaseResponse
import com.foodtraceai.model.SupShipCte
import com.foodtraceai.util.FtlItem
import com.foodtraceai.util.ReferenceDocumentType
import com.foodtraceai.util.SupCteStatus
import com.foodtraceai.util.UnitOfMeasure
import java.time.LocalDate
import java.time.OffsetDateTime

data class ArrivingShipmentsResponseDto(
    val supShipCteId: Long,
    val supShipStatus: SupCteStatus,
    val sscc: String?,
    val logSerialNo: String?,
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
    val shipDate: LocalDate,
    val referenceDocumentType: ReferenceDocumentType,
    val referenceDocumentNum: String,
    override var dateCreated: OffsetDateTime = OffsetDateTime.now(),
    override var dateModified: OffsetDateTime = OffsetDateTime.now(),
    override var isDeleted: Boolean = false,
    override var dateDeleted: OffsetDateTime? = null,
    override var authUsername: String? = null,
) : BaseResponse<ArrivingShipmentsResponseDto>()

fun SupShipCte.toArrivingShipmentsResponseDto(): ArrivingShipmentsResponseDto {
    return ArrivingShipmentsResponseDto(
        supShipCteId = id,
        supShipStatus = supCteStatus,
        sscc = sscc,
        logSerialNo = logSerialNo,
        tlcId = tlc.id,
        tlcVal = tlc.tlcVal,
        tlcSrc = tlc.tlcSource.foodBus.foodBusName + " " + tlc.tlcSource.address.city,
        tlcSrcRef = tlc.tlcSourceReference,
        ftlItem = ftlItem,
        variety = variety,
        quantity = quantity,
        unitOfMeasure = unitOfMeasure,
        prodDesc = prodDesc,
        shipToBus = shipToLocation.foodBus.foodBusName,
        shipToCity = shipToLocation.address.city,
        shipFromBus = shipFromLocation.foodBus.foodBusName,
        shipFromCity = shipFromLocation.address.city,
        shipDate = shipDate,
        referenceDocumentNum = referenceDocumentNum,
        referenceDocumentType = referenceDocumentType,
        dateCreated = dateCreated,
        dateModified = dateModified,
        isDeleted = isDeleted,
        dateDeleted = dateDeleted,
        authUsername = authUsername,
    )
}