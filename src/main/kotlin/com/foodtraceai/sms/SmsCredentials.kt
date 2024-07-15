// ----------------------------------------------------------------------------
// Copyright Kaleidoscope, Inc. or its affiliates. All Rights Reserved.
// ----------------------------------------------------------------------------
package com.kscopeinc.sms

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter

@JsonInclude(JsonInclude.Include.NON_NULL)
class SmsCredentials(

    // -- (required) SMS service provider name
    val name: String,

    // -- mutable property map
    val properties: Map<String, Any>,

    // -- (optional) email connection/session timeout in milliseconds
    val timeoutMS: Long? = null,

    // -- (optional) set true to enable displaying additional debug information
    val debug: Boolean? = null
) {

    @JsonIgnore
    fun isBlank(): Boolean = name == ""

    @JsonIgnore
    fun isNotBlank(): Boolean = !isBlank()

    companion object {
        fun blankCredentials() = SmsCredentials(
            name = "",
            properties = mapOf(),
        )
    }

    //
}

@Converter(autoApply = true)
class SmsCredentialsConverter : AttributeConverter<SmsCredentials, String> {
    override fun convertToDatabaseColumn(smsCred: SmsCredentials?): String {
        if (smsCred != null) {
            try {
                return jacksonObjectMapper().writeValueAsString(smsCred)
            } catch (th: Throwable) {
                // -- catch any errors to prevent not being able to save record
                println("*** Error converting Object to JSON String: ")
            }
        }
        return ""
    }

    override fun convertToEntityAttribute(smsCred: String?): SmsCredentials? {
        if (!smsCred.isNullOrEmpty()) {
            try {
                val typeRef = object : TypeReference<SmsCredentials>() {}
                return jacksonObjectMapper().readValue(smsCred, typeRef)
            } catch (th: Throwable) {
                // -- catch any errors to prevent not being able to load record
                println("*** Error converting JSON String to Object: $smsCred")
            }
        }
        return null
    }
}
