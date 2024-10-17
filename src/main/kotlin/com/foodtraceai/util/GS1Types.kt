package com.foodtraceai.util

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import jakarta.persistence.Embeddable

// GS1 Data Types

@Embeddable
data class Sscc(        // serial shipping container code
    val sscc: String    // A(00) - 18 numeric digits
)

@Converter(autoApply = true)
class SsccConverter : AttributeConverter<Sscc?, String?> {
    override fun convertToDatabaseColumn(sscc: Sscc?): String? {
        return sscc?.sscc
    }

    override fun convertToEntityAttribute(sscc: String?): Sscc? {
        return sscc?.let { Sscc(it) }
    }
}

@Embeddable
data class GTIN(
    val gtin: String    // A(01) - 14 numeric digits
)

@Converter(autoApply = true)
class GTINConverter : AttributeConverter<GTIN?, String?> {
    override fun convertToDatabaseColumn(gtin: GTIN?): String? = gtin?.gtin

    override fun convertToEntityAttribute(gtin: String?): GTIN? = gtin?.let { GTIN(it) }
}

data class BatchLot(
    val value: String      // A(10) - 20 numeric digits
)

@Converter(autoApply = true)
class BatchLotConverter : AttributeConverter<BatchLot?, String?> {
    override fun convertToDatabaseColumn(batchLot: BatchLot?): String? = batchLot?.value

    override fun convertToEntityAttribute(batchLot: String?): BatchLot? = batchLot?.let { BatchLot(it) }
}

data class LogSerialNum(
    val serial: String // A(21) - pallet serial number
)

@Converter(autoApply = true)
class SerialConverter : AttributeConverter<LogSerialNum?, String?> {
    override fun convertToDatabaseColumn(serial: LogSerialNum?): String? = serial?.serial

    override fun convertToEntityAttribute(serial: String?): LogSerialNum? = serial?.let { LogSerialNum(it) }
}