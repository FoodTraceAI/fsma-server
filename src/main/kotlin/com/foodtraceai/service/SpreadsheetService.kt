// ----------------------------------------------------------------------------
// Copyright 2024 FoodTraceAI LLC or its affiliates. All Rights Reserved.
// ----------------------------------------------------------------------------

package com.foodtraceai.service

import com.foodtraceai.model.cte.CteReceive
import com.foodtraceai.model.format
import com.foodtraceai.service.cte.CteReceiveService
import com.foodtraceai.util.ImplementationStyle.FDA
import com.foodtraceai.util.ImplementationStyle.PTI
import com.foodtraceai.util.myConfig
import org.apache.poi.ss.usermodel.*
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.springframework.core.io.ByteArrayResource
import org.springframework.stereotype.Service
import java.io.ByteArrayOutputStream

// Sample sortable spreadsheets are https://producetraceability.org/resources/#sortable
// This is the example for Receiver
// https://producetraceability.org/wp-content/uploads/2023/09/PTI-Sortable-Spreadsheet-Receiving-2-1.xlsx

// Example taken from
// https://www.baeldung.com/java-microsoft-excel

// See also
// https://stackoverflow.com/questions/51684550/how-to-download-an-excel-file-in-spring-restcontroller

// To Run it  http://localhost:8080/api/v1/sheet/cte?which=receive
@Service
class SpreadsheetService(
    private val cteReceiveService: CteReceiveService,
) {

    data class Duo(
        val label: String,
        val f: (cte: CteReceive) -> String,
    )

    private val fdaReceivingTabColHeaders = listOf(
        Duo("(a)(1) TLC - tlcVal", fun(cte: CteReceive) = cte.tlc.tlcVal),
        Duo("(a)(2) Qty & UOM", fun(cte: CteReceive) = "${cte.quantity} ${cte.unitOfMeasure}"),
        Duo("(a)(3) Product Description", fun(cte: CteReceive) = cte.prodDesc),
        Duo("(a)(3) Variety", fun(cte: CteReceive) = cte.variety),
        Duo("(a)(4) IPS Food Bus", fun(cte: CteReceive) = cte.ipsLocation.foodBus.foodBusName),
        Duo("(a)(4) IPS Address", fun(cte: CteReceive) = cte.ipsLocation.address.format()),
        Duo("(a)(5) Receive Location", fun(cte: CteReceive) = cte.location.foodBus.foodBusName),
        Duo("(a)(5) Receive Address", fun(cte: CteReceive) = cte.location.address.format()),
        Duo("(a)(6) Receive Date", fun(cte: CteReceive) = "${cte.receiveDate}"),
        Duo("(a)(7) TLC Source Food Bus", fun(cte: CteReceive) = cte.tlcSource.foodBus.foodBusName),
        Duo("(a)(7) TLC Source Address", fun(cte: CteReceive) = cte.tlcSource.address.format()),
        Duo("(a)(7) TLC Source Reference", fun(cte: CteReceive) = cte.tlcSourceReference ?: "null"),
        Duo("(a)(8) Ref Doc Type", fun(cte: CteReceive) = cte.referenceDocumentType.toString()),
        Duo("(a)(8) Ref Doc Num", fun(cte: CteReceive) = cte.referenceDocumentNum),
    )

    private val ptiReceivingTabColHeaders = listOf(
        Duo("(a)(1) TLC - tlcVal", fun(cte: CteReceive) = cte.tlc.tlcVal),
        Duo("(a)(1) TLC - GTIN", fun(cte: CteReceive) = cte.tlc.gtin?.gtinVal ?: "null"),
        Duo("(a)(1) TLC - Batch", fun(cte: CteReceive) = cte.tlc.batchLot?.batchLotVal ?: "null"),
        Duo("(a)(1) TLC - Date**", fun(cte: CteReceive) = cte.tlc.packDate.toString()),
        Duo("(a)(1) TLC - Date Type**", fun(cte: CteReceive) = "Pack Date"),
        Duo("(a)(1) TLC - SSCC**", fun(cte: CteReceive) = cte.tlc.sscc?.ssccVal ?: "null"),
        Duo("(b)(1) TLC - Assigned By", fun(cte: CteReceive) = cte.tlcSource.foodBus.foodBusDesc),
        Duo("(a)(2) Qty & UOM", fun(cte: CteReceive) = "${cte.quantity} ${cte.unitOfMeasure}"),
        Duo("(a)(3) Product Description", fun(cte: CteReceive) = cte.prodDesc),
        //"(a)(4) Immediate Previous Source (IPS) Location - (Shipped from Location)"
        Duo("(a)(4) IPS Location", fun(cte: CteReceive) = cte.ipsLocation.foodBus.foodBusName),
        Duo("(a)(5) Receive Location", fun(cte: CteReceive) = cte.location.foodBus.foodBusName),
        Duo("(a)(6) Receive Date", fun(cte: CteReceive) = cte.receiveDate.toString()),
        Duo("(a)(7) TLC Source ReferenceGLN", fun(cte: CteReceive) = "null"),
        Duo("(a)(7) TLC Source ReferenceFFRN", fun(cte: CteReceive) = "null"),
        Duo("(a)(7) TLC Source ReferenceURL", fun(cte: CteReceive) = "null"),
        Duo("(a)(7) TLC Source ReferenceGGN", fun(cte: CteReceive) = "null"),
        Duo("(b)(5) TLC Source Reference - Assigned By", fun(cte: CteReceive) = "null"),
        Duo("(a)(8) Ref Doc", fun(cte: CteReceive) = "null"),
    )

    private val pitLocTabColHeaders = listOf(
        Duo("Business or Farm Name", fun(cte: CteReceive) = cte.foodBus.foodBusName),
        Duo("Phone", fun(cte: CteReceive) = cte.ipsLocation.foodBus.contact.phone),
        Duo("Address", fun(cte: CteReceive) = cte.ipsLocation.address.format()),
        Duo("Field Name*", fun(cte: CteReceive) = "${cte.ipsLocation.description}"),
        Duo("Geo-Coordinates*", fun(cte: CteReceive) =  "${cte.ipsLocation.address.lat}, ${cte.ipsLocation.address.lon}"),
        Duo("GLN*", fun(cte: CteReceive) = "${cte.ipsLocation.address.gln}"),
        Duo("FFRN*", fun(cte: CteReceive) = "${cte.ipsLocation.address.ffrn}"),
    )

    private val ptiProductsTabColHeaders = listOf(
        Duo("FTL List Category", fun(cte: CteReceive) = cte.ftlItem.name),
        Duo("GTIN", fun(cte: CteReceive) = cte.tlc.gtin?.gtinVal ?: ""),
        Duo("Product Desc", fun(cte: CteReceive) = cte.prodDesc),
    )

    private fun makeTab(tabName: String, workbook: Workbook, colHeaders: List<Duo>, cteList: List<CteReceive>) {
        val receiving: Sheet = workbook.createSheet(tabName)
        colHeaders.forEachIndexed { idx, _ ->
            receiving.setColumnWidth(idx, 5000)
        }

        val header: Row = receiving.createRow(0)
        val headerStyle: CellStyle = workbook.createCellStyle()
        headerStyle.fillForegroundColor = IndexedColors.LIGHT_BLUE.getIndex()
        headerStyle.fillPattern = FillPatternType.FINE_DOTS
        headerStyle.wrapText = true

        val font = (workbook as XSSFWorkbook).createFont()
        font.fontName = "Arial"
        font.fontHeightInPoints = 11.toShort()
        font.bold = true
        headerStyle.setFont(font)
        headerStyle.alignment = HorizontalAlignment.CENTER

        colHeaders.forEachIndexed { idx, duo ->
            val headerCell = header.createCell(idx)
            headerCell.setCellValue(duo.label)
            headerCell.cellStyle = headerStyle
        }

        // ***************  Content Rows **********
        val style = workbook.createCellStyle()
        style.wrapText = true
        style.shrinkToFit = true
        style.alignment = HorizontalAlignment.CENTER

        cteList.forEachIndexed { rowNum, cte ->
            val row = receiving.createRow(rowNum + 1 /* 1 leave space for headers */)
            colHeaders.forEachIndexed { idx, duo ->
                val cell = row.createCell(idx)
                cell.cellStyle = style
                cell.setCellValue(duo.f(cte))
            }
        }
    }

    fun makeDownloadSpreadsheet(): ByteArrayResource {
        val workbook: Workbook = XSSFWorkbook()

        // Find all cte's in for this spreadsheet
        val cteList = cteReceiveService.findAll()

        when (myConfig.spreadsheetStyle) {
            FDA -> makeTab(tabName = "Receiving", workbook, fdaReceivingTabColHeaders, cteList)

            PTI -> {
                // Receiving Tab
                makeTab(tabName = "Receiving", workbook, ptiReceivingTabColHeaders, cteList)

                // Products Tab
                val set = mutableSetOf<String>()
                val uniqGtinCteList = mutableListOf<CteReceive>()
                cteList.forEach { cte ->
                    val gtinVal = cte.tlc.gtin?.gtinVal ?: throw RuntimeException("GTIN is null")
                    if (set.contains(gtinVal))
                        return@forEach
                    set.add(gtinVal)
                    uniqGtinCteList.add(cte)
                }
                makeTab(tabName = "Products", workbook, ptiProductsTabColHeaders, uniqGtinCteList)

                // Locations Tab
                set.clear()
                val uniqIpsLocationList = mutableListOf<CteReceive>()
                cteList.forEach{ cte ->
                    val key = cte.ipsLocation.address.format()
                    if (set.contains(key))
                        return@forEach
                    set.add(key)
                    uniqIpsLocationList.add(cte)
                }
                makeTab(tabName = "Locations", workbook, pitLocTabColHeaders, uniqIpsLocationList)
            }
        }

        val outputStream = ByteArrayOutputStream()
        workbook.write(outputStream)
        workbook.close()
        return ByteArrayResource(outputStream.toByteArray())
    }
}
