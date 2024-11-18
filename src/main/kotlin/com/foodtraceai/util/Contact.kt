package com.foodtraceai.util

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import jakarta.persistence.Embeddable

@Embeddable
data class Contact(
    val firstName: String,
    val lastName: String,
    val email: String,
    val phone: String,
)

@Converter(autoApply = true)
class ContactToJsonConverter : AttributeConverter<Contact, String> {
    override fun convertToEntityAttribute(jsonPoc: String): Contact? {
        val typeRef = object : TypeReference<Contact>() {}
        return jacksonObjectMapper().readValue(jsonPoc, typeRef)
    }

    override fun convertToDatabaseColumn(poc: Contact?): String {
        return jacksonObjectMapper().writeValueAsString(poc)
    }
}