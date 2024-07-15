// ----------------------------------------------------------------------------
// Copyright 2024 FoodTraceAI LLC or its affiliates. All Rights Reserved.
// ----------------------------------------------------------------------------
package com.foodtraceai.model

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import jakarta.persistence.EmbeddedId
import jakarta.persistence.Entity
import java.io.Serializable
import java.time.OffsetDateTime

// Taken from: https://www.baeldung.com/jpa-composite-primary-keys
@Entity
//@EntityListeners(BaseModelEntityListener::class)
data class FranchisorProperty(

    // Pattern for and list of keys
    // key=reseller-smtp-credentials
    // key=reseller-background-color
    // key ...

    @EmbeddedId
    val franchisorKey: FranchisorKey,
    @Column(columnDefinition = "TEXT")
    val propertyValue: String,

    @Column(updatable = false)
    override var dateCreated: OffsetDateTime = OffsetDateTime.now(),
    override var dateModified: OffsetDateTime = OffsetDateTime.now(),
    override var isDeleted: Boolean = false,
    override var dateDeleted: OffsetDateTime? = null

) : BaseModel<FranchisorProperty>() {

    @Embeddable
    data class FranchisorKey(
        val franchisorId: Long = 0, // ResellerId for Reseller table
        val propertyName: String = "",
    ) : Serializable
}

fun FranchisorProperty.toFranchisorPropertyResponseDto() = FranchisorPropertyResponseDto(
    propertyName = franchisorKey.propertyName,
    propertyValue = propertyValue,
    dateCreated = dateCreated,
    dateModified = dateModified,
    isDeleted = isDeleted,
    dateDeleted = dateDeleted
)

data class FranchisorPropertyRequestDto(
    val propertyName: String,
    val propertyValue: String,
    val franchisorId: Long
)

data class FranchisorPropertyResponseDto(
    val propertyName: String,
    val propertyValue: String,
    val dateCreated: OffsetDateTime,
    val dateModified: OffsetDateTime,
    val isDeleted: Boolean,
    val dateDeleted: OffsetDateTime?,
)

fun FranchisorPropertyRequestDto.toFranchisorProperty() = FranchisorProperty(
    franchisorKey = FranchisorProperty.FranchisorKey(franchisorId, propertyName),
    propertyValue = propertyValue,
)
