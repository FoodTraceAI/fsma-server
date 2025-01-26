// ----------------------------------------------------------------------------
// Copyright 2025 FoodTraceAI LLC or its affiliates. All Rights Reserved.
// ----------------------------------------------------------------------------
package com.foodtraceai.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import java.time.OffsetDateTime

@Entity
data class Contact(
    @Id @GeneratedValue
    override val id: Long = 0,

    val firstname: String,
    val lastname: String,
    val email: String,
    val phone: String,

    @Column(updatable = false)
    override var dateCreated: OffsetDateTime = OffsetDateTime.now(),
    override var dateModified: OffsetDateTime = OffsetDateTime.now(),
    override var isDeleted: Boolean = false,
    override var dateDeleted: OffsetDateTime? = null,
    override var authUsername: String? = null,
) : BaseModel<Contact>()

data class ContactRequestDto(
    val firstname: String,
    val lastname: String,
    val email: String,
    val phone: String,
)

data class ContactResponseDto(
    override val id: Long,
    val firstname: String,
    val lastname: String,
    val email: String,
    val phone: String,
    override var dateCreated: OffsetDateTime = OffsetDateTime.now(),
    override var dateModified: OffsetDateTime = OffsetDateTime.now(),
    override var isDeleted: Boolean = false,
    override var dateDeleted: OffsetDateTime? = null,
    override var authUsername: String? = null,
) : BaseResponse<ContactResponseDto>()

fun Contact.toContactResponseDto() = ContactResponseDto(
    id = id,
    firstname = firstname,
    lastname = lastname,
    email = email,
    phone = phone,
    dateCreated = dateCreated,
    dateModified = dateModified,
    isDeleted = isDeleted,
    dateDeleted = dateDeleted,
    authUsername = authUsername,
)

fun ContactRequestDto.toContact(id: Long = 0) = Contact(
    id = id,
    firstname = firstname,
    lastname = lastname,
    email = email,
    phone = phone,
)

fun ContactResponseDto.toContact() = Contact(
    id = id,
    firstname = firstname,
    lastname = lastname,
    email = email,
    phone = phone,
    dateCreated = dateCreated,
    dateModified = dateModified,
    isDeleted = isDeleted,
    dateDeleted = dateDeleted,
    authUsername = authUsername,
)