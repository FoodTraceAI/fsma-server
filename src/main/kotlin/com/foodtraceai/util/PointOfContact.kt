package com.foodtraceai.util

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import jakarta.persistence.Embeddable

@Embeddable
data class PointOfContact(
    val name: String,
    val email: String,
    val phone: String,
)

@Converter(autoApply = true)
class PointOfContactToJsonConverter : AttributeConverter<PointOfContact, String> {
    override fun convertToEntityAttribute(jsonPoc: String): PointOfContact? {
        val typeRef = object : TypeReference<PointOfContact>() {}
        return jacksonObjectMapper().readValue(jsonPoc, typeRef)
    }

    override fun convertToDatabaseColumn(poc: PointOfContact?): String {
        return jacksonObjectMapper().writeValueAsString(poc)
    }
}