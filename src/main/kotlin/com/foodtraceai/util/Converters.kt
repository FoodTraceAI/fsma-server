// ----------------------------------------------------------------------------
// Copyright 2024 FoodTraceAI LLC or its affiliates. All Rights Reserved.
// ----------------------------------------------------------------------------
package com.foodtraceai.util

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter

@Converter(autoApply = true)
class LongListToJsonConverter : AttributeConverter<List<Long>, String> {
    override fun convertToDatabaseColumn(list: List<Long>) = list.toString()

    override fun convertToEntityAttribute(jsonList: String): List<Long>? {
        val typeRef = object : TypeReference<List<Long>>() {}
        return jacksonObjectMapper().readValue(jsonList, typeRef)
    }
}

@Converter(autoApply = true)
class RoleToJsonConverter : AttributeConverter<List<Role>, String> {
    override fun convertToDatabaseColumn(list: List<Role>): String =
        list.map { it.name }.toJsonString()

    override fun convertToEntityAttribute(jsonList: String): List<Role> =
        jsonList.toList().map {
            enumValueOrNull(it) ?: throw Exception("Enum '$it' converted to null")
        }
}
