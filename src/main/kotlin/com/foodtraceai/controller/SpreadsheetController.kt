// ----------------------------------------------------------------------------
// Copyright 2024 FoodTraceAI LLC or its affiliates. All Rights Reserved.
// ----------------------------------------------------------------------------
package com.foodtraceai.controller

import com.foodtraceai.model.cte.CteReceive
import com.foodtraceai.util.BadRequestException
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import org.springframework.core.io.ByteArrayResource
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate

private const val SHEET_BASE_URL = "/api/v1/sheet"
private const val SPREADSHEET_ALT_BASE_URL = "/api/v1/spreadsheet"

@RestController
@RequestMapping(value = [SHEET_BASE_URL, SPREADSHEET_ALT_BASE_URL])
@SecurityRequirement(name = "bearerAuth")
class SpreadsheetController : BaseController() {

    // http://localhost:8080/api/v1/sheet/cte?which=receive
    @GetMapping("/cte")
    fun downloadSortableWorksheet(
        @RequestParam(value = "which", required = true) which: String,
    ): ResponseEntity<ByteArrayResource> {
        val header = HttpHeaders()
        header.contentType = MediaType("application", "force-download")
        val byteArrayResource: ByteArrayResource
        when (which) {
            "receive" -> {
                header[HttpHeaders.CONTENT_DISPOSITION] = "attachment; filename=CteReceive.xlsx"
                byteArrayResource = spreadsheetService.makeDownloadSpreadsheet()
            }

            else -> throw BadRequestException("Unknown download which: $which")
        }

        return ResponseEntity(byteArrayResource, header, HttpStatus.CREATED)
    }

    // http://localhost:8080/api/v1/sheet - returns everything
    // http://localhost:8080/api/v1/sheet?locationId=3&tlcVal=TraceLotCode3
    @GetMapping
    fun findAllByOptionalParams(
        @RequestParam("tlcVal", required = false) tlcVal: String?,
        @RequestParam("locationId", required = false) locationId: Long?,
        @RequestParam("ipsLocationId", required = false) ipsLocationId: Long?,
        @RequestParam("dateFrom", required = false) dateFrom: LocalDate?,
        @RequestParam("dateTo", required = false) dateTo: LocalDate?,
//        @AuthenticationPrincipal fsaUser: FsmaUser
    ): ResponseEntity<List<CteReceive>> {

        val cteList = cteReceiveService.findAllByOptionalParams(
            tlcVal,
            locationId,
            ipsLocationId,
            dateFrom,
            dateTo,
        )
        return ResponseEntity.ok(cteList)
    }
}