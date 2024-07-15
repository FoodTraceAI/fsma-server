// ----------------------------------------------------------------------------
// Copyright 2024 FoodTraceAI LLC or its affiliates. All Rights Reserved.
// ----------------------------------------------------------------------------

package com.foodtraceai.service

import com.foodtraceai.service.cte.CteReceiveService
import com.foodtraceai.util.ColHeaderType
import org.apache.poi.ss.usermodel.*
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.springframework.core.io.ByteArrayResource
import org.springframework.stereotype.Service
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

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
    private val usePti: Boolean = false,
    private val cteReceiveService: CteReceiveService,
) {
    private val masterColHeaders = listOf(
        Triple("(a)(1) TLC - GTIN", ColHeaderType.Required, 'A'),
        Triple("(a)(1) TLC - Batch", ColHeaderType.Required, 'B'),
        Triple("(a)(1) TLC - Date**", ColHeaderType.PtiBestPractices, 'C'),
        Triple("(a)(1) TLC - Date Type**", ColHeaderType.PtiBestPractices, 'D'),
        Triple("(a)(1) TLC - SSCC**", ColHeaderType.PtiBestPractices, 'E'),
        Triple("(b)(1) TLC - Assigned By", ColHeaderType.Required, 'F'),
        Triple("(a)(2) Qty & UOM", ColHeaderType.Required, 'G'),
        Triple("(a)(3) Product Description", ColHeaderType.Required, 'H'),
        //"(a)(4) Immediate Previous Source (IPS) Location - (Shipped from Location)"
        Triple("(a)(4) IPS Location", ColHeaderType.Required, 'I'),
        Triple("(a)(5) Receive Location", ColHeaderType.Required, 'J'),
        Triple("(a)(6) Receive Date", ColHeaderType.Required, 'K'),
        Triple("(a)(7) TLC Source ReferenceGLN", ColHeaderType.Required, 'L'),
        Triple("(a)(7) TLC Source ReferenceFFRN", ColHeaderType.Required, 'M'),
        Triple("(a)(7) TLC Source ReferenceURL", ColHeaderType.Required, 'N'),
        Triple("(a)(7) TLC Source ReferenceGGN", ColHeaderType.Required, 'O'),
        Triple("(b)(5) TLC Source Reference - Assigned By", ColHeaderType.Required, 'P'),
        Triple("(a)(8) Ref Doc", ColHeaderType.Required, 'Q'),
    )

    // What columns to display
    private val colHeaders = masterColHeaders.filter {
        it.second == ColHeaderType.Required || usePti
    }

    private val oldReceiveColHeaders = listOf(
        "(a)(1) TLC - GTIN",
        "(a)(1) TLC - Batch",
        "(a)(1) TLC - Date**",
        "(a)(1) TLC - Date Type**",
        "(a)(1) TLC - SSCC**",
        "(b)(1) TLC - Assigned By",
        "(a)(2) Qty & UOM",
        "(a)(3) Product Description",
        "(a)(4) IPS Location", //"(a)(4) Immediate Previous Source (IPS) Location - (Shipped from Location)",
        "(a)(5) Receive Location",
        "(a)(6) Receive Date",
        "(a)(7) TLC Source ReferenceGLN",
        "(a)(7) TLC Source ReferenceFFRN",
        "(a)(7) TLC Source ReferenceURL",
        "(a)(7) TLC Source ReferenceGGN",
        "(b)(5) TLC Source Reference - Assigned By",
        "(a)(8) Ref Doc",

        "(a)(7) TLC Source Name",
        "(a)(7) TLC Source Street",
        "(a)(7) TLC Source City",
        "(a)(7) TLC Source State",
        "(a)(7) TLC Source Country",
        "(a)(7) TLC Source Zip Code",
        "(a)(7) TLC Source Phone Number",
    )

    private fun makeReceivingTab(workbook: Workbook) {
        val receiving: Sheet = workbook.createSheet("Receiving")
        colHeaders.forEachIndexed { idx, _ ->
            receiving.setColumnWidth(idx, 5000)
        }

        val header: Row = receiving.createRow(0)

        val headerStyle: CellStyle = workbook.createCellStyle()
//        headerStyle.fillForegroundColor = IndexedColors.LIGHT_BLUE.getIndex()
//        headerStyle.fillPattern = FillPatternType.SOLID_FOREGROUND

        val font = (workbook as XSSFWorkbook).createFont()
        font.fontName = "Arial"
        font.fontHeightInPoints = 11.toShort()
        font.bold = true
        headerStyle.setFont(font)

        colHeaders.forEachIndexed { idx, triple ->
            val headerCell = header.createCell(idx)
            headerCell.setCellValue(triple.first)
            headerCell.cellStyle = headerStyle
        }

        // ***************  Content Rows **********
        val style = workbook.createCellStyle()
        style.wrapText = false
//        style.shrinkToFit = true
        style.alignment = HorizontalAlignment.CENTER

        val cteList = cteReceiveService.findAll()

        cteList.forEachIndexed { rowNum, cte ->
            val row = receiving.createRow(rowNum + 1 /* 1 leave space for headers */)
            colHeaders.forEachIndexed { idx, triple ->
                val cell = row.createCell(idx)
                cell.cellStyle = style
                when (triple.third) {
                    'A' -> cell.setCellValue(cte.traceLotCode.tlcVal)
                    'B' -> cell.setCellValue(cte.traceLotCode.batch)
                    'C' -> cell.setCellValue(cte.traceLotCode.tlcDate)
                    'D' -> cell.setCellValue(cte.traceLotCode.tlcDateType?.name)
                    'E' -> cell.setCellValue(cte.traceLotCode.sscc)
                    'F' -> {}
                    'G' -> cell.setCellValue("${cte.quantity} ${cte.unitOfMeasure.name}")
                    'H' -> cell.setCellValue(cte.foodDesc)
                    'I' -> cell.setCellValue(cte.ipsLocation.foodBus.foodBusName)
                    'J' -> cell.setCellValue(cte.location.foodBus.foodBusName)
                    'K' -> cell.setCellValue(cte.receiveDate)
                    'M' -> cell.setCellValue(cte.tlcSourceReference)
                    'Q' -> cell.setCellValue(cte.referenceDocumentNum)
                    else -> {}
                }
            }
        }
    }

    private fun makeLocationsTab(workbook: Workbook) {
        val locations = workbook.createSheet("Locations")
    }

    private fun makeProductsTab(workbook: Workbook) {
        val products = workbook.createSheet("Products")
    }

    fun makeSortableSpreadsheet() {
        val workbook: Workbook = XSSFWorkbook()

        makeReceivingTab(workbook)
        makeLocationsTab(workbook)
        makeProductsTab(workbook)

        val currDir: File = File(".")
        val path: String = currDir.getAbsolutePath()
        val fileLocation = path.substring(0, path.length - 1) + "temp.xlsx"

        val outputStream = FileOutputStream(fileLocation)
//        val outputStream = ByteArrayOutputStream()
        workbook.write(outputStream)
        workbook.close()
    }

    fun makeDownloadSpreadsheet(): ByteArrayResource {
        val workbook: Workbook = XSSFWorkbook()
        makeReceivingTab(workbook)
        makeLocationsTab(workbook)
        makeProductsTab(workbook)

        val currDir: File = File(".")
        val path: String = currDir.getAbsolutePath()
        val fileLocation = path.substring(0, path.length - 1) + "temp.xlsx"

//        val outputStream = FileOutputStream(fileLocation)
        val outputStream = ByteArrayOutputStream()
        workbook.write(outputStream)
        workbook.close()
        return ByteArrayResource(outputStream.toByteArray())
    }
}
